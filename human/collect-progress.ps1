# Collect Tasks from package doc/tasks-v*.md into human/progress.html (+ progress.json)
# Docs table: | Task | Description | Status |
# Dates live in human/implemented.json (gitignored)
# Run: powershell -File human/collect-progress.ps1
$ErrorActionPreference = "Stop"
$root = Resolve-Path (Join-Path $PSScriptRoot "..")
$lade = Join-Path $root "app\src\main\java\app\lade"
$datesFile = Join-Path $PSScriptRoot "implemented.json"
$today = Get-Date -Format "yyyy-MM-dd"

function Escape-Html([string]$s) {
	if ([string]::IsNullOrEmpty($s)) { return '' }
	return ($s -replace '&', '&amp;' -replace '<', '&lt;' -replace '>', '&gt;' -replace '"', '&quot;')
}

$dates = @{}
if (Test-Path -LiteralPath $datesFile) {
	$jsonText = [System.IO.File]::ReadAllText($datesFile, [System.Text.UTF8Encoding]::new($false))
	$raw = $jsonText | ConvertFrom-Json
	foreach ($p in $raw.PSObject.Properties) {
		$dates[$p.Name] = [string]$p.Value
	}
}

$taskFiles = @(Get-ChildItem -Path $lade -Recurse -Filter "tasks-v*.md" |
	Where-Object { $_.Directory.Name -eq "doc" } |
	Sort-Object { $_.Directory.Parent.Name }, Name)

$waveCount = $taskFiles.Count

$rows = New-Object System.Collections.Generic.List[object]
foreach ($file in $taskFiles) {
	$pkg = $file.Directory.Parent.Name
	$wave = [regex]::Match($file.BaseName, '^tasks-v(\d+)$').Groups[1].Value
	if ([string]::IsNullOrWhiteSpace($wave)) { continue }
	$lines = Get-Content -LiteralPath $file.FullName -Encoding UTF8
	foreach ($line in $lines) {
		if ($line -match '^\|\s*Task\s*\|') { continue }
		if ($line -match '^\|\s*-+') { continue }
		if ($line -match '^\|\s*([^|]+)\|\s*([^|]+)\|\s*([^|]+)\|') {
			$task = $Matches[1].Trim().Trim([char]0x60)
			$desc = $Matches[2].Trim()
			$status = $Matches[3].Trim()
			if ($status -notin @('done', 'todo', 'backlog')) { continue }
			$key = $pkg + '/' + $task
			$impl = '-'
			if ($status -eq 'done') {
				if (-not $dates.ContainsKey($key) -or [string]::IsNullOrWhiteSpace($dates[$key])) {
					$dates[$key] = $today
				}
				$impl = $dates[$key]
			} elseif ($dates.ContainsKey($key)) {
				$dates.Remove($key) | Out-Null
			}
			$rows.Add([pscustomobject]@{
				Pkg    = $pkg
				Wave   = [int]$wave
				Task   = $task
				Desc   = $desc
				Status = $status
				Date   = $impl
			})
		}
	}
}

$map = @{}
foreach ($k in ($dates.Keys | Sort-Object)) { $map[$k] = $dates[$k] }
$jsonOut = ($map | ConvertTo-Json -Compress)
[System.IO.File]::WriteAllText($datesFile, $jsonOut, [System.Text.UTF8Encoding]::new($false))

$sorted = @($rows | Sort-Object Pkg, Wave, @{ Expression = { if ($_.Task -eq 'scope') { '0' } else { $_.Task } } })
$doneCount = @($sorted | Where-Object { $_.Status -eq 'done' }).Count
$todoCount = @($sorted | Where-Object { $_.Status -eq 'todo' }).Count
$backCount = @($sorted | Where-Object { $_.Status -eq 'backlog' }).Count
$total = $sorted.Count
$pct = 0
if ($total -gt 0) { $pct = [math]::Round(100.0 * $doneCount / $total, 1) }

$pkgNames = @($sorted | Select-Object -ExpandProperty Pkg -Unique | Sort-Object)
$pkgStatsHtml = New-Object System.Collections.Generic.List[string]
$pkgStatsJson = New-Object System.Collections.Generic.List[object]
foreach ($pkgName in $pkgNames) {
	$pkgRows = @($sorted | Where-Object { $_.Pkg -eq $pkgName })
	$pDone = @($pkgRows | Where-Object { $_.Status -eq 'done' }).Count
	$pTodo = @($pkgRows | Where-Object { $_.Status -eq 'todo' }).Count
	$pBack = @($pkgRows | Where-Object { $_.Status -eq 'backlog' }).Count
	$pTotal = $pkgRows.Count
	$pOther = $pDone + $pTodo
	$pPct = 0
	if ($pTotal -gt 0) { $pPct = [math]::Round(100.0 * $pDone / $pTotal, 0) }
	$otherPct = 0
	$backPct = 0
	if ($pTotal -gt 0) {
		$backPct = [math]::Round(100.0 * $pBack / $pTotal, 0)
		$otherPct = 100 - $backPct
	}
	$doneInOther = 0
	if ($pOther -gt 0) { $doneInOther = [math]::Round(100.0 * $pDone / $pOther, 0) }
	$pkgEsc = Escape-Html $pkgName
	$pkgTitle = Escape-Html ("{0}: {1} done / {2} todo / {3} backlog" -f $pkgName, $pDone, $pTodo, $pBack)
	[void]$pkgStatsHtml.Add(@"
<button type="button" class="pkg-stat" data-pkg="$pkgEsc" aria-pressed="false" title="$pkgTitle">
  <span class="pkg-name mono">$pkgEsc</span>
  <span class="pkg-meta"><strong>$pDone</strong>/$pTotal</span>
  <span class="pkg-bar" aria-hidden="true">
    <span class="seg-other" style="width:${otherPct}%"><span class="seg-done" style="width:${doneInOther}%"></span></span>
    <span class="seg-backlog" style="width:${backPct}%"></span>
  </span>
</button>
"@)
	$pkgStatsJson.Add([pscustomobject]@{
		pkg     = $pkgName
		done    = $pDone
		todo    = $pTodo
		backlog = $pBack
		total   = $pTotal
		pct     = $pPct
	})
}

$byDate = @{}
foreach ($r in $sorted) {
	if ($r.Status -ne 'done' -or $r.Date -eq '-') { continue }
	if (-not $byDate.ContainsKey($r.Date)) { $byDate[$r.Date] = 0 }
	$byDate[$r.Date]++
}
$velocityDates = @($byDate.Keys | Sort-Object -Descending)
$velMax = 1
foreach ($d in $velocityDates) {
	if ($byDate[$d] -gt $velMax) { $velMax = $byDate[$d] }
}
$velocityHtml = New-Object System.Collections.Generic.List[string]
$velocityJson = New-Object System.Collections.Generic.List[object]
foreach ($d in $velocityDates) {
	$cnt = $byDate[$d]
	$w = [math]::Round(100.0 * $cnt / $velMax, 0)
	$dEsc = Escape-Html $d
	[void]$velocityHtml.Add(@"
<div class="vel-row">
  <span class="mono vel-date">$dEsc</span>
  <span class="vel-bar" aria-hidden="true"><span style="width:${w}%"></span></span>
  <span class="vel-count">$cnt</span>
</div>
"@)
	$velocityJson.Add([pscustomobject]@{ date = $d; count = $cnt })
}

$bodyRows = New-Object System.Collections.Generic.List[string]
foreach ($r in $sorted) {
	$pkg = Escape-Html $r.Pkg
	$task = Escape-Html $r.Task
	$desc = Escape-Html $r.Desc
	$status = Escape-Html $r.Status
	$date = Escape-Html $r.Date
	$wave = $r.Wave
	[void]$bodyRows.Add(@"
<tr data-status="$status" data-pkg="$pkg">
<td class="mono">$pkg</td>
<td data-sort="$wave">v$wave</td>
<td class="mono">$task</td>
<td>$desc</td>
<td><span class="badge badge-$status">$status</span></td>
<td class="mono">$date</td>
</tr>
"@)
}

$pkgStatsBlock = $pkgStatsHtml -join "`n"
$velocityBlock = if ($velocityHtml.Count -gt 0) { $velocityHtml -join "`n" } else { '<p class="muted">No completed tasks yet.</p>' }

$html = @"
<!DOCTYPE html>
<html lang="ru">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Lade progress</title>
<style>
:root {
  --bg: #f7f6f3;
  --surface: #fff;
  --text: #1c1b19;
  --muted: #6b6860;
  --line: #e4e1d8;
  --done: #1f6b3a;
  --done-bg: #e6f4eb;
  --todo: #8a5a00;
  --todo-bg: #fbf0d9;
  --backlog: #5c5a54;
  --backlog-bg: #efeee9;
  --accent: #2c4a6e;
  --accent-soft: #e8eef5;
  --bar: #9bb0c9;
}
* { box-sizing: border-box; }
body {
  margin: 0;
  font: 15px/1.45 "Segoe UI", system-ui, sans-serif;
  color: var(--text);
  background: var(--bg);
}
main {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 1.25rem 3rem;
}
.muted { color: var(--muted); font-size: 0.9rem; }
.top-bar {
  position: sticky;
  top: 0;
  z-index: 30;
  background: var(--bg);
  margin: 0 -1.25rem;
  padding: 0.45rem 0 0.35rem;
  border-bottom: 1px solid var(--line);
}
.stats {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
  padding: 0 1.25rem;
}
.stat {
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: 8px;
  padding: 0.4rem 0.7rem;
  min-width: 5.25rem;
  font: inherit;
  color: inherit;
  text-align: left;
  cursor: pointer;
}
.stat:hover { border-color: #c8c4b8; }
.stat[aria-pressed="true"] {
  border-color: #b8c8dc;
  background: var(--accent-soft);
  box-shadow: inset 0 0 0 1px #b8c8dc;
}
.stat strong {
  display: block;
  font-size: 1.1rem;
  font-weight: 650;
  line-height: 1.15;
}
.stat span {
  color: var(--muted);
  font-size: 0.72rem;
}
.pkg-grid {
  display: flex;
  flex-wrap: nowrap;
  gap: 0.35rem;
  overflow-x: auto;
  margin-top: 0.4rem;
  padding: 0.15rem 1.25rem 0.4rem;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
}
.pkg-stat {
  flex: 0 0 auto;
  width: 7.25rem;
  display: grid;
  grid-template-columns: 1fr auto;
  grid-template-rows: auto auto;
  gap: 0.2rem 0.3rem;
  align-items: center;
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: 8px;
  padding: 0.35rem 0.45rem;
  font: inherit;
  color: inherit;
  text-align: left;
  cursor: pointer;
}
.pkg-stat:hover { border-color: #c8c4b8; }
.pkg-stat[aria-pressed="true"] {
  border-color: #b8c8dc;
  background: var(--accent-soft);
  box-shadow: inset 0 0 0 1px #b8c8dc;
}
.pkg-name {
  font-size: 0.72rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.pkg-meta {
  font-size: 0.68rem;
  color: var(--muted);
  justify-self: end;
  white-space: nowrap;
}
.pkg-meta strong { color: var(--text); font-weight: 650; }
.pkg-bar {
  grid-column: 1 / -1;
  display: flex;
  height: 5px;
  border-radius: 999px;
  background: var(--line);
  overflow: hidden;
}
.seg-other {
  display: block;
  height: 100%;
  background: var(--todo-bg);
  min-width: 0;
}
.seg-done {
  display: block;
  height: 100%;
  background: var(--done);
  opacity: 0.85;
}
.seg-backlog {
  display: block;
  height: 100%;
  background: var(--backlog);
  opacity: 0.55;
  min-width: 0;
}
.velocity {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  max-width: 28rem;
  margin-top: 0.85rem;
}
.vel-row {
  display: grid;
  grid-template-columns: 6.5rem 1fr 2rem;
  gap: 0.5rem;
  align-items: center;
}
.vel-date { font-size: 0.85rem; color: var(--muted); }
.vel-bar {
  height: 8px;
  border-radius: 999px;
  background: var(--line);
  overflow: hidden;
}
.vel-bar > span {
  display: block;
  height: 100%;
  background: var(--done);
  border-radius: inherit;
  opacity: 0.75;
}
.vel-count {
  font-size: 0.85rem;
  font-weight: 650;
  text-align: right;
}
.table-wrap {
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: 12px;
  overflow: auto;
  max-height: calc(100vh - 11rem);
  margin-top: 0.15rem;
}
table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.92rem;
}
th, td {
  padding: 0.55rem 0.7rem;
  text-align: left;
  border-bottom: 1px solid var(--line);
  vertical-align: top;
}
th {
  position: sticky;
  top: 0;
  background: #faf9f6;
  z-index: 1;
  font-size: 0.78rem;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--muted);
  white-space: nowrap;
  user-select: none;
}
th button {
  all: unset;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
}
th button:hover { color: var(--text); }
th button::after {
  content: "";
  width: 0.45rem;
  opacity: 0.35;
}
th[data-dir="asc"] button::after { content: "▲"; opacity: 0.8; }
th[data-dir="desc"] button::after { content: "▼"; opacity: 0.8; }
tr:last-child td { border-bottom: 0; }
tbody tr:hover { background: #faf8f2; }
.mono {
  font-family: ui-monospace, "Cascadia Mono", Consolas, monospace;
  font-size: 0.86em;
}
.badge {
  display: inline-block;
  border-radius: 999px;
  padding: 0.12rem 0.55rem;
  font-size: 0.78rem;
  font-weight: 600;
}
.badge-done { color: var(--done); background: var(--done-bg); }
.badge-todo { color: var(--todo); background: var(--todo-bg); }
.badge-backlog { color: var(--backlog); background: var(--backlog-bg); }
.hidden { display: none !important; }
</style>
</head>
<body>
<main>
  <div class="top-bar">
    <div class="stats" role="group" aria-label="Filter by status">
      <button type="button" class="stat" data-status="done" aria-pressed="false"><strong>$doneCount</strong><span>done</span></button>
      <button type="button" class="stat" data-status="todo" aria-pressed="false"><strong>$todoCount</strong><span>todo</span></button>
      <button type="button" class="stat" data-status="backlog" aria-pressed="false"><strong>$backCount</strong><span>backlog</span></button>
      <button type="button" class="stat" data-status="all" aria-pressed="true"><strong>$total</strong><span>total ($pct%)</span></button>
    </div>
    <div class="pkg-grid" role="group" aria-label="Filter by feature">
$pkgStatsBlock
    </div>
  </div>
  <div class="table-wrap">
    <table id="tasks">
      <thead>
        <tr>
          <th data-col="0"><button type="button">Feature</button></th>
          <th data-col="1"><button type="button">Wave</button></th>
          <th data-col="2"><button type="button">Task</button></th>
          <th data-col="3"><button type="button">Description</button></th>
          <th data-col="4"><button type="button">Status</button></th>
          <th data-col="5"><button type="button">Implemented</button></th>
        </tr>
      </thead>
      <tbody>
$($bodyRows -join "`n")
      </tbody>
    </table>
  </div>
  <div class="velocity">
$velocityBlock
  </div>
</main>
<script>
(function () {
  const table = document.getElementById("tasks");
  const tbody = table.tBodies[0];
  const statusBtns = document.querySelectorAll(".stats .stat");
  const pkgBtns = document.querySelectorAll(".pkg-stat");
  let statusFilter = "all";
  let pkgFilter = "all";
  let sortCol = 0;
  let sortDir = "asc";

  function cellKey(row, col) {
    const cell = row.cells[col];
    if (cell.dataset.sort != null) return Number(cell.dataset.sort);
    return cell.textContent.trim().toLowerCase();
  }

  function apply() {
    const rows = Array.from(tbody.rows);
    rows.sort((a, b) => {
      const av = cellKey(a, sortCol);
      const bv = cellKey(b, sortCol);
      let cmp = 0;
      if (typeof av === "number" && typeof bv === "number") cmp = av - bv;
      else cmp = String(av).localeCompare(String(bv), "ru");
      return sortDir === "asc" ? cmp : -cmp;
    });
    rows.forEach((row) => {
      const okStatus = statusFilter === "all" || row.dataset.status === statusFilter;
      const okPkg = pkgFilter === "all" || row.dataset.pkg === pkgFilter;
      row.classList.toggle("hidden", !(okStatus && okPkg));
      tbody.appendChild(row);
    });
    table.querySelectorAll("th").forEach((th) => {
      const col = Number(th.dataset.col);
      th.dataset.dir = col === sortCol ? sortDir : "";
    });
  }

  table.querySelectorAll("th button").forEach((btn) => {
    btn.addEventListener("click", () => {
      const col = Number(btn.parentElement.dataset.col);
      if (sortCol === col) sortDir = sortDir === "asc" ? "desc" : "asc";
      else { sortCol = col; sortDir = "asc"; }
      apply();
    });
  });

  statusBtns.forEach((btn) => {
    btn.addEventListener("click", () => {
      statusFilter = btn.dataset.status;
      statusBtns.forEach((b) => b.setAttribute("aria-pressed", String(b === btn)));
      apply();
    });
  });

  pkgBtns.forEach((btn) => {
    btn.addEventListener("click", () => {
      const pkg = btn.dataset.pkg;
      if (pkgFilter === pkg) {
        pkgFilter = "all";
        pkgBtns.forEach((b) => b.setAttribute("aria-pressed", "false"));
      } else {
        pkgFilter = pkg;
        pkgBtns.forEach((b) => b.setAttribute("aria-pressed", String(b === btn)));
      }
      apply();
    });
  });

  apply();
})();
</script>
</body>
</html>
"@

$out = Join-Path $PSScriptRoot 'progress.html'
[System.IO.File]::WriteAllText($out, $html, [System.Text.UTF8Encoding]::new($false))

$taskJson = New-Object System.Collections.Generic.List[object]
foreach ($r in $sorted) {
	$implDate = $null
	if ($r.Date -ne '-') { $implDate = $r.Date }
	[void]$taskJson.Add([pscustomobject]@{
		pkg         = $r.Pkg
		wave        = $r.Wave
		task        = $r.Task
		description = $r.Desc
		status      = $r.Status
		implemented = $implDate
	})
}
$progressPayload = [pscustomobject]@{
	generated   = (Get-Date -Format 'yyyy-MM-ddTHH:mm:ss')
	versionCode = $waveCount
	summary     = [pscustomobject]@{
		done    = $doneCount
		todo    = $todoCount
		backlog = $backCount
		total   = $total
		pct     = $pct
	}
	byFeature   = $pkgStatsJson.ToArray()
	velocity    = $velocityJson.ToArray()
	tasks       = $taskJson.ToArray()
}
$progressJsonPath = Join-Path $PSScriptRoot 'progress.json'
$progressJson = ($progressPayload | ConvertTo-Json -Depth 6)
[System.IO.File]::WriteAllText($progressJsonPath, $progressJson, [System.Text.UTF8Encoding]::new($false))

$legacyMd = Join-Path $PSScriptRoot 'progress.md'
if (Test-Path -LiteralPath $legacyMd) {
	Remove-Item -LiteralPath $legacyMd -Force
}

Write-Host ('Wrote {0} + progress.json ({1} tasks, {2}% done, versionCode={3})' -f $out, $total, $pct, $waveCount)

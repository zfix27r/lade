# Collect Tasks from package doc/tasks-v*.md into human/progress.html
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

$bodyRows = New-Object System.Collections.Generic.List[string]
foreach ($r in $sorted) {
	$pkg = Escape-Html $r.Pkg
	$task = Escape-Html $r.Task
	$desc = Escape-Html $r.Desc
	$status = Escape-Html $r.Status
	$date = Escape-Html $r.Date
	$wave = $r.Wave
	[void]$bodyRows.Add(@"
<tr data-status="$status">
<td class="mono">$pkg</td>
<td data-sort="$wave">v$wave</td>
<td class="mono">$task</td>
<td>$desc</td>
<td><span class="badge badge-$status">$status</span></td>
<td class="mono">$date</td>
</tr>
"@)
}

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
  padding: 1.5rem 1.25rem 3rem;
}
h1 {
  margin: 0 0 0.35rem;
  font-size: 1.65rem;
  font-weight: 650;
  letter-spacing: -0.02em;
}
.stats {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
  margin: 1rem 0;
}
.stat {
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 0.65rem 0.9rem;
  min-width: 6.5rem;
}
.stat strong {
  display: block;
  font-size: 1.35rem;
  font-weight: 650;
  line-height: 1.2;
}
.stat span {
  color: var(--muted);
  font-size: 0.8rem;
}
.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
  margin-bottom: 0.85rem;
}
.filters button {
  border: 1px solid var(--line);
  background: var(--surface);
  color: var(--text);
  border-radius: 999px;
  padding: 0.35rem 0.75rem;
  font: inherit;
  font-size: 0.85rem;
  cursor: pointer;
}
.filters button[aria-pressed="true"] {
  background: var(--accent-soft);
  border-color: #b8c8dc;
  color: var(--accent);
}
.table-wrap {
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: 12px;
  overflow: auto;
  max-height: calc(100vh - 12rem);
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
  <h1>Progress</h1>
  <div class="stats">
    <div class="stat"><strong>$doneCount</strong><span>done</span></div>
    <div class="stat"><strong>$todoCount</strong><span>todo</span></div>
    <div class="stat"><strong>$backCount</strong><span>backlog</span></div>
    <div class="stat"><strong>$total</strong><span>total ($pct%)</span></div>
  </div>
  <div class="filters" role="group" aria-label="Filter by status">
    <button type="button" data-filter="all" aria-pressed="true">all</button>
    <button type="button" data-filter="done" aria-pressed="false">done</button>
    <button type="button" data-filter="todo" aria-pressed="false">todo</button>
    <button type="button" data-filter="backlog" aria-pressed="false">backlog</button>
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
</main>
<script>
(function () {
  const table = document.getElementById("tasks");
  const tbody = table.tBodies[0];
  const filterBtns = document.querySelectorAll(".filters button");
  let filter = "all";
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
      const show = filter === "all" || row.dataset.status === filter;
      row.classList.toggle("hidden", !show);
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

  filterBtns.forEach((btn) => {
    btn.addEventListener("click", () => {
      filter = btn.dataset.filter;
      filterBtns.forEach((b) => b.setAttribute("aria-pressed", String(b === btn)));
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

$legacyMd = Join-Path $PSScriptRoot 'progress.md'
if (Test-Path -LiteralPath $legacyMd) {
	Remove-Item -LiteralPath $legacyMd -Force
}

Write-Host ('Wrote {0} ({1} tasks, {2}% done, versionCode={3})' -f $out, $total, $pct, $waveCount)

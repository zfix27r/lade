package app.lade.draft.internal.chiper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import app.lade.draft.R
import app.lade.entrykind.EntryKind
import java.time.LocalDate
import java.time.LocalTime

internal val barChipPresets: List<BarChipPreset> = listOf(
    BarChipPreset(
        labelRes = R.string.draft_chip_today,
        icon = Icons.Default.DateRange,
        visibleFor = setOf(EntryKind.TASK, EntryKind.EVENT, EntryKind.SCHEDULE),
        apply = { it.copy(dateFrom = LocalDate.now()) },
        isActual = { it.dateFrom == LocalDate.now() },
    ),
    BarChipPreset(
        labelRes = R.string.draft_chip_tomorrow,
        icon = Icons.Default.DateRange,
        visibleFor = setOf(EntryKind.TASK, EntryKind.EVENT, EntryKind.SCHEDULE),
        apply = { it.copy(dateFrom = LocalDate.now().plusDays(1)) },
        isActual = { it.dateFrom == LocalDate.now().plusDays(1) },
    ),
    BarChipPreset(
        labelRes = R.string.draft_chip_morning,
        icon = Icons.Default.Schedule,
        visibleFor = setOf(EntryKind.TASK, EntryKind.SCHEDULE),
        apply = { it.copy(timeFrom = LocalTime.of(9, 0)) },
        isActual = { it.timeFrom == LocalTime.of(9, 0) },
    ),
    BarChipPreset(
        labelRes = R.string.draft_chip_evening,
        icon = Icons.Default.Schedule,
        visibleFor = setOf(EntryKind.TASK, EntryKind.SCHEDULE),
        apply = { it.copy(timeFrom = LocalTime.of(18, 0)) },
        isActual = { it.timeFrom == LocalTime.of(18, 0) },
    ),
    BarChipPreset(
        labelRes = R.string.draft_chip_daily,
        icon = Icons.Default.Repeat,
        visibleFor = setOf(EntryKind.HABIT, EntryKind.SCHEDULE),
        apply = { it.copy(rrule = "RRULE:FREQ=DAILY") },
        isActual = { it.rrule == "RRULE:FREQ=DAILY" },
    ),
    BarChipPreset(
        labelRes = R.string.draft_chip_weekdays,
        icon = Icons.Default.Repeat,
        visibleFor = setOf(EntryKind.HABIT, EntryKind.SCHEDULE),
        apply = { it.copy(rrule = "RRULE:FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR") },
        isActual = { it.rrule == "RRULE:FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR" },
    ),
    BarChipPreset(
        labelRes = R.string.draft_chip_weekends,
        icon = Icons.Default.Repeat,
        visibleFor = setOf(EntryKind.HABIT, EntryKind.SCHEDULE),
        apply = { it.copy(rrule = "RRULE:FREQ=WEEKLY;BYDAY=SA,SU") },
        isActual = { it.rrule == "RRULE:FREQ=WEEKLY;BYDAY=SA,SU" },
    ),
)
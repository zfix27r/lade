package app.lade.schedule.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.daypart.domain.matchingPart
import app.lade.daypart.ui.DayPartChipRow
import app.lade.resources.R
import app.lade.schedule.ui.components.AlarmDropdown
import app.lade.schedule.ui.components.CollapsedRecurrenceRow
import app.lade.schedule.ui.components.ReminderDropdown
import app.lade.schedule.ui.components.SingleDateButton
import app.lade.schedule.ui.components.SingleTimeButton
import app.lade.schedule.ui.components.TemporalSection
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemporalOptionsPanel(
	value: TemporalOptions,
	onChange: (TemporalOptions) -> Unit,
	config: TemporalOptionsConfig,
	modifier: Modifier = Modifier,
) {
	val timePattern = stringResource(R.string.format_time_hm)
	val timeFmt = remember(timePattern) { DateTimeFormatter.ofPattern(timePattern) }
	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
	) {
		if (config.showDate) {
			TemporalSection(
				title = stringResource(R.string.temporal_section_date),
				useCard = config.useSectionCards,
			) {
				SingleDateButton(
					label = value.date?.toString() ?: stringResource(R.string.temporal_pick_date),
					date = value.date ?: LocalDate.now(),
					onPicked = { onChange(value.copy(date = it)) },
				)
			}
		}
		if (config.showDateRange) {
			TemporalSection(
				title = stringResource(R.string.temporal_section_date_range),
				useCard = config.useSectionCards,
			) {
				DateRangePickerField(
					dateFrom = value.dateFrom ?: LocalDate.now(),
					dateTo = value.dateTo,
					onDateFromChange = { onChange(value.copy(dateFrom = it)) },
					onDateToChange = { onChange(value.copy(dateTo = it)) },
				)
			}
		}
		if (config.showTime) {
			TemporalSection(
				title = stringResource(R.string.temporal_section_time),
				useCard = config.useSectionCards,
			) {
				val clock = config.dayPartClock
				if (clock != null) {
					val matched = value.time?.let { clock.matchingPart(it) }
					val customSelected = value.time != null && matched == null
					DayPartChipRow(
						selected = matched,
						onSelect = { part ->
							onChange(value.copy(time = clock.timeOf(part)))
						},
						clock = clock,
						customSelected = customSelected,
						onCustomClick = {
							if (value.time == null) {
								onChange(value.copy(time = LocalTime.of(9, 0)))
							}
						},
					)
				}
				val timeText = value.time?.format(timeFmt)
					?: stringResource(R.string.temporal_time_unset)
				SingleTimeButton(
					label = stringResource(R.string.format_time_optional, timeText),
					time = value.time ?: LocalTime.of(9, 0),
					onPicked = { onChange(value.copy(time = it)) },
					onClear = { onChange(value.copy(time = null)) },
					clearable = true,
				)
			}
		}
		if (config.showTimeRange) {
			TemporalSection(
				title = stringResource(R.string.temporal_section_time_range),
				useCard = config.useSectionCards,
			) {
				TimeRangePickerField(
					start = value.time ?: LocalTime.of(9, 0),
					end = value.timeEnd ?: LocalTime.of(18, 0),
					onStartChange = { onChange(value.copy(time = it)) },
					onEndChange = { onChange(value.copy(timeEnd = it)) },
				)
			}
		}
		if (config.showRecurrence) {
			TemporalSection(
				title = stringResource(R.string.temporal_section_recurrence),
				useCard = config.useSectionCards,
			) {
				CollapsedRecurrenceRow(
					draft = value.recurrence,
					presets = config.recurrencePresets,
					onDraftChange = { onChange(value.copy(recurrence = it)) },
				)
			}
		}
		if (config.showAlarm) {
			TemporalSection(
				title = stringResource(R.string.temporal_section_alarm),
				useCard = config.useSectionCards,
			) {
				AlarmDropdown(
					selected = value.alarmMode,
					onSelected = { onChange(value.copy(alarmMode = it)) },
				)
			}
		}
		if (config.showReminder) {
			TemporalSection(
				title = stringResource(R.string.temporal_section_reminder),
				useCard = config.useSectionCards,
			) {
				ReminderDropdown(
					minutes = value.reminderMinutesBefore,
					onChange = { onChange(value.copy(reminderMinutesBefore = it)) },
				)
			}
		}
	}
}
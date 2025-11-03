@file:Suppress("NewApi")

package com.gearsnap.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.gearsnap.model.ActivityItem
import com.gearsnap.model.Recurrence
import java.time.LocalDate
import java.time.YearMonth

class PlanningViewModel : ViewModel() {
    var currentMonth by mutableStateOf(YearMonth.now())
        private set

    var selectedDate by mutableStateOf(LocalDate.now())
        private set

    private val _activitiesByDate: SnapshotStateMap<LocalDate, SnapshotStateList<ActivityItem>> = mutableStateMapOf()

    fun activitiesFor(date: LocalDate): SnapshotStateList<ActivityItem> =
        _activitiesByDate.getOrPut(date) { mutableListOf<ActivityItem>().toMutableStateList() }

    fun activitiesForSelected(): SnapshotStateList<ActivityItem> = activitiesFor(selectedDate)

    fun addActivity(title: String, time: String) {
        val item = ActivityItem(title = title.trim(), time = time.trim(), date = selectedDate)
        activitiesForSelected().add(item)
    }

    fun addRecurringActivity(
        title: String,
        time: String,
        startDate: java.time.LocalDate = selectedDate,
        recurrence: Recurrence,
        occurrences: Int
    ) {
        if (occurrences <= 0) return
        var date = startDate
        repeat(occurrences) {
            val item = ActivityItem(title = title.trim(), time = time.trim(), date = date)
            activitiesFor(date).add(item)
            date = when (recurrence) {
                Recurrence.DAILY -> date.plusDays(1)
                Recurrence.WEEKLY -> date.plusWeeks(1)
                Recurrence.MONTHLY -> date.plusMonths(1)
                Recurrence.NONE -> date // if NONE, just add once and break
            }
            if (recurrence == Recurrence.NONE) return
        }
    }

    fun onDateSelected(date: LocalDate) {
        selectedDate = date
        currentMonth = YearMonth.from(date)
    }

    fun prevMonth() { currentMonth = currentMonth.minusMonths(1) }
    fun nextMonth() { currentMonth = currentMonth.plusMonths(1) }
}

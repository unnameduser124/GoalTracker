package com.example.goaltracker.goal

import java.util.Calendar

class TimeGoal(
    var goalTimeAmount: Double,
    var currentTimeAmount: Double,
    var unit: TimeMeasureUnit,
    val startTime: Calendar,
    var endTime: Calendar,
    val goalEntries: MutableList<GoalEntry> = mutableListOf(),
    var goalID: Long = -1
) {
    init {
        startTime.set(Calendar.HOUR_OF_DAY, 0)
        startTime.set(Calendar.MINUTE, 0)
        endTime.set(Calendar.HOUR_OF_DAY, 0)
        endTime.set(Calendar.MINUTE, 0)
    }

    fun getEntriesForMonth(month: Calendar): List<GoalEntry> {
        return goalEntries.filter {
            it.date.get(Calendar.MONTH) == month.get(Calendar.MONTH)
        }
    }
}
package com.example.goaltracker.goal

import com.example.goaltracker.dataClasses.DataTimeGoal
import java.util.Calendar

class TimeGoal(
    val ID: Long,
    var name: String,
    var goalTimeAmount: Double,
    var currentTimeAmount: Double,
    val startTime: Calendar,
    var deadline: Calendar,
    var goalSessions: MutableList<GoalSession> = mutableListOf(),
) {

    constructor(data: DataTimeGoal): this(data.goalID,
        data.goalName,
        -1.0,
        0.0,
        Calendar.getInstance(),
        Calendar.getInstance(),
        mutableListOf()){
        startTime.timeInMillis = data.startTime * 1000
        deadline.timeInMillis = data.deadline * 1000
        goalTimeAmount = data.goalTimeAmount/3600.0
        currentTimeAmount = data.currentTimeAmount/3600.0
    }

    init {
        startTime.set(Calendar.HOUR_OF_DAY, 0)
        startTime.set(Calendar.MINUTE, 0)
        deadline.set(Calendar.HOUR_OF_DAY, 0)
        deadline.set(Calendar.MINUTE, 0)
    }

    fun getEntriesForMonth(month: Calendar): List<GoalSession> {
        return goalSessions.filter {
            it.date.get(Calendar.MONTH) == month.get(Calendar.MONTH)
        }
    }

    fun getEntriesForYear(year: Calendar): List<GoalSession> {
        return goalSessions.filter {
            it.date.get(Calendar.YEAR) == year.get(Calendar.MONTH)
        }
    }

}
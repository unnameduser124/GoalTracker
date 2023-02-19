package com.example.goaltracker.goal

import android.content.Context
import com.example.goaltracker.dataClasses.DataTimeGoal
import com.example.goaltracker.database.SessionDatabaseService
import java.util.Calendar

class TimeGoal(
    val ID: Long,
    var name: String,
    var goalTimeAmount: Double,
    val startTime: Calendar,
    var deadline: Calendar,
    var goalSessions: MutableList<GoalSession> = mutableListOf(),
) {

    constructor(data: DataTimeGoal): this(data.goalID,
        data.goalName,
        -1.0,
        Calendar.getInstance(),
        Calendar.getInstance(),
        mutableListOf()){
        startTime.timeInMillis = data.startTime
        deadline.timeInMillis = data.deadline
        goalTimeAmount = data.goalTimeAmount
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

    fun getCurrentTimeAmount(context: Context): Double{
        val dbService = SessionDatabaseService(context)
        return dbService.getCurrentTimeForGoal(ID)
    }

    fun loadSessions(context: Context){
        val dbService = SessionDatabaseService(context)
        goalSessions = dbService.getSessionsForGoal(ID)
    }

}
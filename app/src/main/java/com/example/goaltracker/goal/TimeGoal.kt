package com.example.goaltracker.goal

import android.content.Context
import com.example.goaltracker.dataClasses.DataTimeGoal
import com.example.goaltracker.database.SessionDatabaseService
import com.example.goaltracker.getTimeDifferenceInDays
import java.util.Calendar
import kotlin.math.floor

class TimeGoal(
    val ID: Long,
    var name: String,
    var goalTimeAmount: Double,
    var startTime: Calendar,
    var deadline: Calendar,
    var goalSessions: MutableList<GoalSession> = mutableListOf(),
) {

    constructor(data: DataTimeGoal): this(data.goalID,
        data.goalName,
        data.goalTimeAmount,
        Calendar.getInstance().apply { timeInMillis = data.startTime },
        Calendar.getInstance().apply { timeInMillis = data.deadline },
        mutableListOf())

    fun getCurrentTimeAmount(context: Context): Double{
        val dbService = SessionDatabaseService(context)
        return dbService.getCurrentTimeForGoal(ID)
    }

    fun loadSessions(context: Context){
        val dbService = SessionDatabaseService(context)
        goalSessions = dbService.getSessionsForGoal(ID)
    }

    private fun getGoalTotalTime(): Int{
        val timeInMillis = deadline.timeInMillis - startTime.timeInMillis
        return floor(timeInMillis/1000.0/60/60/24).toInt() + 2
    }

    private fun getInitialExpectedDailyAverageTime(): Double{
        val timeConstraint = getGoalTotalTime()
        return goalTimeAmount/timeConstraint
    }

    fun getTimeDebt(context: Context): Double{
        val currentTime = Calendar.getInstance()
        if(currentTime.timeInMillis >= deadline.timeInMillis){
            return goalTimeAmount - getCurrentTimeAmount(context)
        }

        val timePassed = getTimeDifferenceInDays(startTime.timeInMillis, currentTime.timeInMillis) + 1
        val expectedDaily = getInitialExpectedDailyAverageTime()

        val expectedTimeAmount = timePassed * expectedDaily

        return expectedTimeAmount - getCurrentTimeAmount(context)
    }

}
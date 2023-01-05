package com.example.goaltracker.goal

import java.util.*
import kotlin.math.floor

fun getTimeDifferenceInDays(start: Calendar, end: Calendar): Int{
    val timeInMillis = end.timeInMillis - start.timeInMillis
    return floor(timeInMillis/1000.0/60/60/24).toInt()
}

fun getGoalTotalTime(goal: TimeGoal): Int{
    val timeInMillis = goal.endTime.timeInMillis - goal.startTime.timeInMillis
    return floor(timeInMillis/1000.0/60/60/24).toInt() + 2
}

fun getInitialExpectedDailyAverageTime(goal: TimeGoal): Double{
    val timeConstraint = getGoalTotalTime(goal)
    return goal.goalTimeAmount/timeConstraint
}
fun getInitialExpectedMonthlyAverage(goal: TimeGoal): Double{
    TODO("Not implemented yet")
}
fun getInitialExpectedYearlyAverage(goal: TimeGoal): Double{
    TODO("Not implemented yet")
}

fun getCurrentExpectedDailyAverageTime(goal: TimeGoal): Double{
    TODO("Not implemented yet")
}
fun getCurrentExpectedMonthlyAverageTime(goal: TimeGoal): Double{
    TODO("Not implemented yet")
}
fun getCurrentExpectedYearlyAverageTime(goal: TimeGoal): Double{
    TODO("Not implemented yet")
}

fun getRealDailyAverageTime(goal: TimeGoal): Double{
    TODO("Not implemented yet")
}
fun getRealMonthlyAverageTime(goal: TimeGoal): Double{
    TODO("Not implemented yet")
}
fun getRealYearlyAverageTime(goal: TimeGoal): Double{
    TODO("Not implemented yet")
}

fun getTimeDebt(goal: TimeGoal): Double{
    val currentTime = Calendar.getInstance().apply{
    }

    println(currentTime.time)

    val passedTime = getTimeDifferenceInDays(goal.startTime, currentTime)
    val expectedDaily = getInitialExpectedDailyAverageTime(goal)

    val expectedTimeAmount = passedTime * expectedDaily

    return expectedTimeAmount - goal.currentTimeAmount
}

fun getTotalProgressPercentage(goal: TimeGoal): Double{
    return goal.currentTimeAmount/goal.goalTimeAmount
}
fun getMonthlyProgressPercentage(goal: TimeGoal, month: Calendar): Double{
    val entries = goal.getEntriesForMonth(month)
    var currentMonthTime = 0.0
    entries.forEach{
        currentMonthTime+=it.timeAmount
    }

    TODO("Have to implement current expected montly time before implementing this one")
}
fun getYearlyProgressPercentage(goal: TimeGoal): Double{
    TODO("Not implemented yet")
}

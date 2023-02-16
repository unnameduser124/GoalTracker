package com.example.goaltracker.goal

import android.content.Context
import java.util.*
import kotlin.math.floor

fun getTimeDifferenceInDays(start: Calendar, end: Calendar): Int{
    val timeInMillis = end.timeInMillis - start.timeInMillis
    return floor(timeInMillis/1000.0/60/60/24).toInt()
}

fun getTimeDifferenceInMonths(start: Calendar, end: Calendar): Double{
    val months = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12 + end.get(Calendar.MONTH) - start.get(Calendar.MONTH)
    val days = end.get(Calendar.DATE) - start.get(Calendar.DATE)
    return months + days.toDouble() / end.getActualMaximum(Calendar.DATE)
}

fun getTimeDifferenceInYears(start: Calendar, end: Calendar): Double{
    val dayDiff = getTimeDifferenceInDays(start, end)
    return dayDiff.toDouble()/365.0
}

fun getGoalTotalTime(goal: TimeGoal): Int{
    val timeInMillis = goal.deadline.timeInMillis - goal.startTime.timeInMillis
    return floor(timeInMillis/1000.0/60/60/24).toInt() + 2
}

fun getInitialExpectedDailyAverageTime(goal: TimeGoal): Double{
    val timeConstraint = getGoalTotalTime(goal)
    return goal.goalTimeAmount/timeConstraint
}

fun getInitialExpectedMonthlyAverage(goal: TimeGoal): Double{
    return goal.goalTimeAmount / getTimeDifferenceInMonths(goal.startTime, goal.deadline)
}

fun getInitialExpectedYearlyAverage(goal: TimeGoal): Double{
    return goal.goalTimeAmount / getTimeDifferenceInYears(goal.startTime, goal.deadline)
}

fun getCurrentExpectedDailyAverageTime(goal: TimeGoal, context: Context): Double{
    val goalTimeLeft = goal.goalTimeAmount - goal.getCurrentTimeAmount(context)
    val timeLeft = getTimeDifferenceInDays(Calendar.getInstance(), goal.deadline)
    return goalTimeLeft/timeLeft
}

fun getCurrentExpectedMonthlyAverageTime(goal: TimeGoal, context: Context): Double{
    val goalTimeLeft = goal.goalTimeAmount - goal.getCurrentTimeAmount(context)
    val timeLeft = getTimeDifferenceInMonths(Calendar.getInstance(), goal.deadline)
    return goalTimeLeft/timeLeft
}

fun getCurrentExpectedYearlyAverageTime(goal: TimeGoal, context: Context): Double{
    val goalTimeLeft = goal.goalTimeAmount - goal.getCurrentTimeAmount(context)
    val timeLeft = getTimeDifferenceInYears(Calendar.getInstance(), goal.deadline)
    return goalTimeLeft/timeLeft
}

fun getRealDailyAverageTime(goal: TimeGoal, context: Context): Double{
    return goal.getCurrentTimeAmount(context) / getTimeDifferenceInDays(goal.startTime, Calendar.getInstance())
}
fun getRealMonthlyAverageTime(goal: TimeGoal, context: Context): Double{
    return goal.getCurrentTimeAmount(context) / getTimeDifferenceInMonths(goal.startTime, Calendar.getInstance())
}
fun getRealYearlyAverageTime(goal: TimeGoal, context: Context): Double{
    return goal.getCurrentTimeAmount(context) / getTimeDifferenceInYears(goal.startTime, Calendar.getInstance())
}

fun getTimeDebt(goal: TimeGoal, context: Context): Double{
    val currentTime = Calendar.getInstance()
    if(currentTime.timeInMillis >= goal.deadline.timeInMillis){
        return goal.goalTimeAmount - goal.getCurrentTimeAmount(context)
    }

    val timePassed= getTimeDifferenceInDays(goal.startTime, currentTime) + 1
    val expectedDaily = getInitialExpectedDailyAverageTime(goal)

    val expectedTimeAmount = timePassed * expectedDaily

    return expectedTimeAmount - goal.getCurrentTimeAmount(context)
}

fun getTotalProgressPercentage(goal: TimeGoal, context: Context): Double{
    return goal.getCurrentTimeAmount(context)/goal.goalTimeAmount
}
fun getMonthlyProgressPercentage(goal: TimeGoal, month: Calendar, context: Context): Double{
    val entries = goal.getEntriesForMonth(month)
    var currentMonthTime = 0.0
    entries.forEach{
        currentMonthTime+=it.timeAmount
    }

    return getCurrentExpectedMonthlyAverageTime(goal, context)/currentMonthTime
}
fun getYearlyProgressPercentage(goal: TimeGoal, year: Calendar, context: Context): Double{
    val entries = goal.getEntriesForYear(year)
    var currentYearTime = 0.0
    entries.forEach{
        currentYearTime+=it.timeAmount
    }

    return getCurrentExpectedYearlyAverageTime(goal, context)/currentYearTime
}

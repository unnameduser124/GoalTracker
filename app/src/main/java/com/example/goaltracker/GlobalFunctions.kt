package com.example.goaltracker

import java.util.*
import kotlin.math.floor

fun roundDouble(value: Double, multiplier: Int): Double{
    return (floor(value * multiplier)) / multiplier
}

fun setCalendarToDayStart(calendar: Calendar): Calendar {
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar
}

fun setCalendarToDayEnd(calendar: Calendar): Calendar{
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar
}

fun getTimeDifferenceInDays(start: Calendar, end: Calendar): Int{
    val timeInMillis = end.timeInMillis - start.timeInMillis
    return floor(timeInMillis/ MILLIS_IN_DAY.toDouble()).toInt()
}

fun getTimeInMonths(start: Long, end: Long): Int{

    var monthDifference = 0
    val firstDateCalendar = Calendar.getInstance().apply { timeInMillis = start }
    val lastDateCalendar = Calendar.getInstance().apply { timeInMillis = end }

    val yearDifference = lastDateCalendar.get(Calendar.YEAR) - firstDateCalendar.get(Calendar.YEAR)
    if(yearDifference == 0){
        monthDifference = lastDateCalendar.get(Calendar.MONTH) - firstDateCalendar.get(Calendar.MONTH) + 1
    }
    else{
        monthDifference = MONTHS_IN_YEAR * (yearDifference - 1)
        monthDifference += MONTHS_IN_YEAR - (firstDateCalendar.get(Calendar.MONTH) - 1)
        monthDifference += lastDateCalendar.get(Calendar.MONTH)
    }
    return monthDifference
}

fun getTimeInYears(start: Long, end: Long): Int{
    val startCal = setCalendarToDayStart(Calendar.getInstance().apply{ timeInMillis = start})
    val endCal = setCalendarToDayStart(Calendar.getInstance().apply{ timeInMillis = end})
    if(endCal.get(Calendar.YEAR) == startCal.get(Calendar.YEAR)){
        return 1
    }
    return endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)
}

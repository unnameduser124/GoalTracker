package com.example.goaltracker.database

import android.content.Context
import android.provider.BaseColumns
import com.example.goaltracker.*
import java.util.*

class StatsDatabaseService(context: Context): GoalDatabase(context) {
    fun getTotalTime(): Double{
        val db = this.readableDatabase

        val columnName = "SUMA"
        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $columnName")

        val cursor = db.query(
            GoalDatabaseConstants.SessionTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null)

        with(cursor){
            while(moveToNext()){
                return getDouble(getColumnIndexOrThrow(columnName))
            }
        }
        return 0.0
    }
    fun getMostTimeInDay(): Double{
        val db = this.readableDatabase

        val columnName = "SUMA"
        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $columnName")

        val groupBy = "${GoalDatabaseConstants.SessionTable.SESSION_DATE}"
        val orderBy = "$columnName DESC"

        val cursor = db.query(
            GoalDatabaseConstants.SessionTable.TABLE_NAME,
            projection,
            null,
            null,
            groupBy,
            null,
            orderBy)

        with(cursor) {
            while (moveToNext()) {
                return getDouble(getColumnIndexOrThrow(columnName))
            }
        }
        return 0.0
    }
    fun getGoalWithMostTime(): Long{
        val db = this.readableDatabase

        val columnName = "SUMA"
        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $columnName", "${GoalDatabaseConstants.SessionTable.GOAL_ID}")

        val groupBy = "${GoalDatabaseConstants.SessionTable.GOAL_ID}"
        val orderBy = "$columnName DESC"

        val cursor = db.query(
            GoalDatabaseConstants.SessionTable.TABLE_NAME,
            projection,
            null,
            null,
            groupBy,
            null,
            orderBy)

        with(cursor) {
            if(moveToNext()){
                return getLong(getColumnIndexOrThrow("${GoalDatabaseConstants.SessionTable.GOAL_ID}"))
            }

        }
        return 0L
    }
    fun getLongestActiveGoal(): Long{
        val db = this.readableDatabase

        val projection = arrayOf("(${GoalDatabaseConstants.TimeGoalTable.DEADLINE}-${GoalDatabaseConstants.TimeGoalTable.START_TIME}) as DIFF", "${BaseColumns._ID}")

        val groupBy = "${BaseColumns._ID}"
        val orderBy = "DIFF DESC"

        val cursor = db.query(
            GoalDatabaseConstants.TimeGoalTable.TABLE_NAME,
            projection,
            null,
            null,
            groupBy,
            null,
            orderBy)

        with(cursor) {
            if(moveToNext()){
                return getLong(getColumnIndexOrThrow("${BaseColumns._ID}"))
            }

        }
        return 0L
    }
    fun getAverageTimePerGoal(): Double{
        val db = this.readableDatabase
        val columnName = "AVERAGE"

        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT})/(SELECT COUNT( DISTINCT ${BaseColumns._ID}) FROM ${GoalDatabaseConstants.TimeGoalTable.TABLE_NAME}) as $columnName")

        val cursor = db.query(
            GoalDatabaseConstants.SessionTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null)

        with(cursor) {
            if(moveToNext()){
                return getDouble(getColumnIndexOrThrow(columnName))
            }

        }
        return 0.0
    }
    fun getAverageTimePerDay(): Double{
        val db = this.readableDatabase

        val columnName = "VALUE"
        val dayDifference = "(MAX(${GoalDatabaseConstants.SessionTable.SESSION_DATE}) - MIN(${GoalDatabaseConstants.SessionTable.SESSION_DATE})) / ($MILLIS_IN_DAY) + 1"
        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) / CAST($dayDifference as REAL) as $columnName")

        val cursor = db.query(
            GoalDatabaseConstants.SessionTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null,
        )

        with(cursor){
            while(moveToNext()){
                return getDouble(getColumnIndexOrThrow(columnName))
            }
        }
        return 0.0
    }
    fun getAverageTimePerWeek(): Double{
        val db = this.readableDatabase

        val columnName = "VALUE"
        val dayDifference = "(MAX(${GoalDatabaseConstants.SessionTable.SESSION_DATE}) - MIN(${GoalDatabaseConstants.SessionTable.SESSION_DATE})) / ($MILLIS_IN_DAY) + 1"
        val numberOfWeeksDecimal = "CAST(($dayDifference) as REAL) / $DAYS_IN_WEEK.0"
        val numberOfWeeksCeilRound = "CAST ( $numberOfWeeksDecimal as INT ) + ( $numberOfWeeksDecimal > CAST ( $numberOfWeeksDecimal as INT ))"
        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) / CAST($numberOfWeeksCeilRound as REAL) as $columnName")

        val cursor = db.query(
            GoalDatabaseConstants.SessionTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null,
        )

        with(cursor){
            while(moveToNext()){
                return getDouble(getColumnIndexOrThrow(columnName))
            }
        }
        return 0.0
    }
    fun getAverageTimePerMonth(): Double{
        val db = this.readableDatabase

        val columnName = "VALUE"
        val firstDateColumn = "FIRST"
        val lastDateColumn = "LAST"


        val projection = arrayOf("MIN(${GoalDatabaseConstants.SessionTable.SESSION_DATE}) as $firstDateColumn", "MAX(${GoalDatabaseConstants.SessionTable.SESSION_DATE}) as $lastDateColumn")

        val cursor = db.query(
            GoalDatabaseConstants.SessionTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null,
        )

        var monthDifference = 0

        with(cursor){
            while(moveToNext()){
                val firstDate = getLong(getColumnIndexOrThrow(firstDateColumn))
                val lastDate = getLong(getColumnIndexOrThrow(lastDateColumn))

                val firstDateCalendar = Calendar.getInstance().apply { timeInMillis = firstDate }
                val lastDateCalendar = Calendar.getInstance().apply { timeInMillis = lastDate }

                val yearDifference = lastDateCalendar.get(Calendar.YEAR) - firstDateCalendar.get(Calendar.YEAR)
                if(yearDifference == 0){
                    monthDifference = lastDateCalendar.get(Calendar.MONTH) - firstDateCalendar.get(Calendar.MONTH) + 1
                }
                else{
                    monthDifference = MONTHS_IN_YEAR * (yearDifference - 1)
                    monthDifference += MONTHS_IN_YEAR - (firstDateCalendar.get(Calendar.MONTH) - 1)
                    monthDifference += lastDateCalendar.get(Calendar.MONTH)
                }
            }
        }

        val resultProjection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) / $monthDifference.0 as $columnName")
        val resultCursor = db.query(
            GoalDatabaseConstants.SessionTable.TABLE_NAME,
            resultProjection,
            null,
            null,
            null,
            null,
            null
        )
        with(resultCursor){
            while(moveToNext()){
                return getDouble(getColumnIndexOrThrow(columnName))
            }
        }
        return 0.0
    }
    fun getAverageTimePerYear(): Double{
        val db = this.readableDatabase

        val columnName = "VALUE"
        val firstDateColumn = "FIRST"
        val lastDateColumn = "LAST"


        val projection = arrayOf("MIN(${GoalDatabaseConstants.SessionTable.SESSION_DATE}) as $firstDateColumn", "MAX(${GoalDatabaseConstants.SessionTable.SESSION_DATE}) as $lastDateColumn")

        val cursor = db.query(
            GoalDatabaseConstants.SessionTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null,
        )

        var yearDifference = 0

        with(cursor){
            while(moveToNext()){
                val firstDate = getLong(getColumnIndexOrThrow(firstDateColumn))
                val lastDate = getLong(getColumnIndexOrThrow(lastDateColumn))

                val firstDateCalendar = Calendar.getInstance().apply { timeInMillis = firstDate }
                val lastDateCalendar = Calendar.getInstance().apply { timeInMillis = lastDate }

                yearDifference = lastDateCalendar.get(Calendar.YEAR) - firstDateCalendar.get(Calendar.YEAR) + 1
            }
        }

        val resultProjection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) / $yearDifference.0 as $columnName")
        val resultCursor = db.query(
            GoalDatabaseConstants.SessionTable.TABLE_NAME,
            resultProjection,
            null,
            null,
            null,
            null,
            null
        )
        with(resultCursor){
            while(moveToNext()){
                return getDouble(getColumnIndexOrThrow(columnName))
            }
        }
        return 0.0
    }
    fun getTimeWithinLastWeek(): Double{
        val db = this.readableDatabase
        val resultColumn = "SUMA"

        val currentDate = clearHoursAndMinutes(Calendar.getInstance())
        val sevenDaysAgo = clearHoursAndMinutes(Calendar.getInstance())
        sevenDaysAgo.timeInMillis -= MILLIS_IN_DAY.toLong() * (DAYS_IN_WEEK.toLong() - 1)

        val moreThan = sevenDaysAgo.timeInMillis
        val lessThan = currentDate.timeInMillis

        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $resultColumn")
        val selection = "${GoalDatabaseConstants.SessionTable.SESSION_DATE} >= ? AND ${GoalDatabaseConstants.SessionTable.SESSION_DATE} <= ?"
        val selectionArgs = arrayOf(moreThan.toString(), lessThan.toString())

        val cursor = db.query(
            GoalDatabaseConstants.SessionTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor){
            while(moveToNext()){
                return getDouble(getColumnIndexOrThrow(resultColumn))
            }
        }

        return 0.0
    }
    fun getTimeThisMonth(): Double{
        val db = this.readableDatabase
        val resultColumn = "SUMA"

        val monthStart = clearHoursAndMinutes(Calendar.getInstance())
        monthStart.set(Calendar.DAY_OF_MONTH, monthStart.getActualMinimum(Calendar.DAY_OF_MONTH))
        val monthEnd = clearHoursAndMinutes(Calendar.getInstance())
        monthEnd.set(Calendar.DAY_OF_MONTH, monthEnd.getActualMaximum(Calendar.DAY_OF_MONTH))

        val moreThan = monthStart.timeInMillis
        val lessThan = monthEnd.timeInMillis

        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $resultColumn")
        val selection = "${GoalDatabaseConstants.SessionTable.SESSION_DATE} >= ? AND ${GoalDatabaseConstants.SessionTable.SESSION_DATE} <= ?"
        val selectionArgs = arrayOf(moreThan.toString(), lessThan.toString())

        val cursor = db.query(
            GoalDatabaseConstants.SessionTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor){
            while(moveToNext()){
                return getDouble(getColumnIndexOrThrow(resultColumn))
            }
        }

        return 0.0
    }
    fun getTimeThisYear(): Double{
        val db = this.readableDatabase
        val resultColumn = "SUMA"

        val yearStart = clearHoursAndMinutes(Calendar.getInstance())
        yearStart.set(Calendar.DAY_OF_YEAR, yearStart.getActualMinimum(Calendar.DAY_OF_YEAR))
        val yearEnd = clearHoursAndMinutes(Calendar.getInstance())
        yearEnd.set(Calendar.DAY_OF_YEAR, yearEnd.getActualMaximum(Calendar.DAY_OF_YEAR))

        val moreThan = yearStart.timeInMillis
        val lessThan = yearEnd.timeInMillis

        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $resultColumn")
        val selection = "${GoalDatabaseConstants.SessionTable.SESSION_DATE} >= ? AND ${GoalDatabaseConstants.SessionTable.SESSION_DATE} <= ?"
        val selectionArgs = arrayOf(moreThan.toString(), lessThan.toString())

        val cursor = db.query(
            GoalDatabaseConstants.SessionTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor){
            while(moveToNext()){
                return getDouble(getColumnIndexOrThrow(resultColumn))
            }
        }

        return 0.0
    }
    private fun clearHoursAndMinutes(calendar: Calendar): Calendar{
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }
}
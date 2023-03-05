package com.example.goaltracker.database

import android.content.Context
import android.provider.BaseColumns
import com.example.goaltracker.*
import java.util.*

class GlobalStatsDatabaseService(context: Context): GoalDatabase(context) {
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
            if(moveToFirst()){
                return getDouble(getColumnIndexOrThrow(columnName))
            }
        }
        return 0.0
    }
    fun getMostTimeInDay(): Double{
        val db = this.readableDatabase

        val columnName = "MAX_A_DAY"
        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $columnName")

        val groupBy = GoalDatabaseConstants.SessionTable.SESSION_DATE
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
            if (moveToFirst()) {
                return getDouble(getColumnIndexOrThrow(columnName))
            }
        }
        return 0.0
    }
    fun getGoalWithMostTime(): Long{
        val db = this.readableDatabase

        val columnName = "SUMA"
        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $columnName",
            GoalDatabaseConstants.SessionTable.GOAL_ID)

        val groupBy = GoalDatabaseConstants.SessionTable.GOAL_ID
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
            if(moveToFirst()){
                return getLong(getColumnIndexOrThrow(GoalDatabaseConstants.SessionTable.GOAL_ID))
            }

        }
        return 0L
    }
    fun getLongestActiveGoal(): Long{
        val db = this.readableDatabase

        val projection = arrayOf(
            GoalDatabaseConstants.TimeGoalTable.START_TIME,
            GoalDatabaseConstants.TimeGoalTable.DEADLINE,
            BaseColumns._ID
        )

        val cursor = db.query(
            GoalDatabaseConstants.TimeGoalTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null)

        val goalDurationList = mutableListOf<Pair<Long, Int>>()

        with(cursor) {
            while(moveToNext()){
                val startTime = getLong(getColumnIndexOrThrow(GoalDatabaseConstants.TimeGoalTable.START_TIME))
                val deadline = getLong(getColumnIndexOrThrow(GoalDatabaseConstants.TimeGoalTable.DEADLINE))
                val goalID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val today = setCalendarToDayStart(Calendar.getInstance())

                if(deadline > today.timeInMillis){
                    val goalDuration = getTimeDifferenceInDays(startTime, today.timeInMillis)
                    goalDurationList.add(Pair(goalID, goalDuration))
                } else{
                    val goalDuration = getTimeDifferenceInDays(startTime, deadline)
                    goalDurationList.add(Pair(goalID, goalDuration))
                }
            }
        }
        if(goalDurationList.isNotEmpty()){
            return goalDurationList.maxByOrNull { it.second }!!.first
        }
        return 0L
    }
    fun getAverageTimePerGoal(): Double{
        val db = this.readableDatabase
        val averageColumnName = "AVERAGE"

        val countGoalsQuery = "SELECT COUNT( DISTINCT ${BaseColumns._ID}) FROM ${GoalDatabaseConstants.TimeGoalTable.TABLE_NAME}"
        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT})/($countGoalsQuery) as $averageColumnName")

        val cursor = db.query(
            GoalDatabaseConstants.SessionTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null)

        with(cursor) {
            if(moveToFirst()){
                return getDouble(getColumnIndexOrThrow(averageColumnName))
            }

        }
        return 0.0
    }
    fun getAverageTimePerDay(): Double{
        val db = this.readableDatabase

        val timeAmountColumn = "TimeSum"
        val startColumn = "MinDate"
        val endColumn = "MaxDate"
        val projection = arrayOf(
            "SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $timeAmountColumn",
            "MAX(${GoalDatabaseConstants.SessionTable.SESSION_DATE}) as $endColumn",
            "MIN(${GoalDatabaseConstants.SessionTable.SESSION_DATE}) as $startColumn"
        )

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
            if(moveToFirst()){
                val timeAmount = getDouble(getColumnIndexOrThrow(timeAmountColumn))
                val startTime = getLong(getColumnIndexOrThrow(startColumn))
                val endTime = getLong(getColumnIndexOrThrow(endColumn))
                val today = setCalendarToDayStart(Calendar.getInstance())

                return if(endTime > today.timeInMillis){
                    timeAmount / (getTimeDifferenceInDays(
                            startTime,
                            endTime
                    ) + 1)
                } else{
                    timeAmount / (getTimeDifferenceInDays(
                        startTime,
                        today.timeInMillis
                    ) + 1)
                }
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
        //maybe just get data from the database and perform calculations locally instead of using sql?

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
            if(moveToFirst()){
                return getDouble(getColumnIndexOrThrow(columnName))
            }
        }
        return 0.0
    }
    fun getAverageTimePerMonth(): Double{
        val db = this.readableDatabase

        val columnName = "TOTAL_TIME"
        val firstDateColumn = "FIRST"
        val lastDateColumn = "LAST"


        val projection = arrayOf("MIN(${GoalDatabaseConstants.SessionTable.SESSION_DATE}) as $firstDateColumn",
            "MAX(${GoalDatabaseConstants.SessionTable.SESSION_DATE}) as $lastDateColumn")

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

                monthDifference = getTimeInMonths(firstDateCalendar.timeInMillis, lastDateCalendar.timeInMillis)
            }
        }

        val resultProjection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $columnName")
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
            if(moveToFirst()){
                val totalTime =  getDouble(getColumnIndexOrThrow(columnName))
                return totalTime / monthDifference
            }
        }
        return 0.0
    }
    fun getAverageTimePerYear(): Double{
        val db = this.readableDatabase

        val columnName = "TOTAL_TIME"
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

        val resultProjection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $columnName")
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
                val totalTime =  getDouble(getColumnIndexOrThrow(columnName))
                return totalTime / yearDifference
            }
        }
        return 0.0
    }
    fun getTimeWithinLastWeek(): Double{
        val db = this.readableDatabase
        val resultColumn = "TOTAL_TIME"

        val currentDate = setCalendarToDayStart(Calendar.getInstance())
        val sevenDaysAgo = setCalendarToDayStart(Calendar.getInstance())
        sevenDaysAgo.timeInMillis -= MILLIS_IN_DAY.toLong() * (DAYS_IN_WEEK.toLong() - 1)

        val moreThan = sevenDaysAgo.timeInMillis
        val lessThan = currentDate.timeInMillis

        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $resultColumn")
        val selection = "${GoalDatabaseConstants.SessionTable.SESSION_DATE} >= ? " +
                "AND ${GoalDatabaseConstants.SessionTable.SESSION_DATE} <= ?"
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
            if(moveToFirst()){
                return getDouble(getColumnIndexOrThrow(resultColumn))
            }
        }
        return 0.0
    }
    fun getTimeThisMonth(): Double{
        val db = this.readableDatabase
        val resultColumn = "TOTAL_TIME"

        val monthStart = setCalendarToDayStart(Calendar.getInstance())
        monthStart.set(Calendar.DAY_OF_MONTH, monthStart.getActualMinimum(Calendar.DAY_OF_MONTH))
        val monthEnd = setCalendarToDayStart(Calendar.getInstance())
        monthEnd.set(Calendar.DAY_OF_MONTH, monthEnd.getActualMaximum(Calendar.DAY_OF_MONTH))

        val moreThan = monthStart.timeInMillis
        val lessThan = monthEnd.timeInMillis

        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $resultColumn")
        val selection = "${GoalDatabaseConstants.SessionTable.SESSION_DATE} >= ? " +
                "AND ${GoalDatabaseConstants.SessionTable.SESSION_DATE} <= ?"
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

        val yearStart = setCalendarToDayStart(Calendar.getInstance())
        yearStart.set(Calendar.DAY_OF_YEAR, yearStart.getActualMinimum(Calendar.DAY_OF_YEAR))
        val yearEnd = setCalendarToDayStart(Calendar.getInstance())
        yearEnd.set(Calendar.DAY_OF_YEAR, yearEnd.getActualMaximum(Calendar.DAY_OF_YEAR))

        val moreThan = yearStart.timeInMillis
        val lessThan = yearEnd.timeInMillis

        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $resultColumn")
        val selection = "${GoalDatabaseConstants.SessionTable.SESSION_DATE} >= ? " +
                "AND ${GoalDatabaseConstants.SessionTable.SESSION_DATE} <= ?"
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

}
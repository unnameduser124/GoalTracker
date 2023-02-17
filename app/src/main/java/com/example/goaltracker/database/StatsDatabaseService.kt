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

        println(projection[0])
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
                    monthDifference = lastDateCalendar.get(Calendar.MONTH) - firstDateCalendar.get(Calendar.MONTH)
                }
                else{
                    monthDifference = MONTHS_IN_YEAR * (yearDifference - 1)
                    monthDifference += MONTHS_IN_YEAR - (firstDateCalendar.get(Calendar.MONTH) - 1)
                    monthDifference += lastDateCalendar.get(Calendar.MONTH)
                }
                println(monthDifference)
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
        TODO("Not implemented yet")
    }
    fun getTimeThisMonth(): Double{
        TODO("Not implemented yet")
    }
    fun getTimeThisYear(): Double{
        TODO("Not implemented yet")
    }
}
package com.example.goaltracker.database

import android.content.Context
import android.provider.BaseColumns
import com.example.goaltracker.*
import java.util.*

class GoalStatsDatabaseService(val context: Context, val goalID: Long): GoalDatabase(context) {

    fun getGoalDayTime(): Double{
        val db = this.readableDatabase
        val resultColumn = "DayTime"

        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $resultColumn")

        val today = setCalendarToDayStart(Calendar.getInstance())

        val selection = "${GoalDatabaseConstants.SessionTable.GOAL_ID} = ? AND ${GoalDatabaseConstants.SessionTable.SESSION_DATE} = ?"
        val selectionArgs = arrayOf(goalID.toString(), today.timeInMillis.toString())

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

    fun getGoalExpectedDayTime(): Double{
        val db = this.readableDatabase
        val resultColumn = "TimeLeft"
        val goalTimeColumn = "GoalTime"
        val deadlineColumn = "GoalDeadline"

        val goalTotalTime = "SELECT ${GoalDatabaseConstants.TimeGoalTable.GOAL_TIME_AMOUNT} FROM ${GoalDatabaseConstants.TimeGoalTable.TABLE_NAME} WHERE ${BaseColumns._ID} = $goalID"
        val projection = arrayOf(
            "($goalTotalTime) as $goalTimeColumn",
            "SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $resultColumn",
            "(SELECT ${GoalDatabaseConstants.TimeGoalTable.DEADLINE} FROM ${GoalDatabaseConstants.TimeGoalTable.TABLE_NAME} WHERE ${BaseColumns._ID} = $goalID) as $deadlineColumn"
        )
        val selection = "${GoalDatabaseConstants.SessionTable.GOAL_ID} = ?"
        val selectionArgs = arrayOf(goalID.toString())

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
                val timeSpent = getDouble(getColumnIndexOrThrow(resultColumn))
                val goalTime = getDouble(getColumnIndexOrThrow(goalTimeColumn))
                val deadline = getLong(getColumnIndexOrThrow(deadlineColumn))
                val untilDeadline = (deadline - setCalendarToDayStart(Calendar.getInstance()).timeInMillis)/ MILLIS_IN_DAY
                return if(untilDeadline == 0L) goalTime - timeSpent else if(timeSpent.isNaN()) goalTime/untilDeadline else (goalTime - timeSpent)/untilDeadline
            }
        }

        return 0.0
    }

    fun getGoalAverageDailyTime(): Double{
        val db = this.readableDatabase
        val totalTimeColumn = "TotalTime"
        val startTimeColumn = "goalStartTime"

        val startTimeQuery = "(SELECT ${GoalDatabaseConstants.TimeGoalTable.START_TIME} FROM ${GoalDatabaseConstants.TimeGoalTable.TABLE_NAME} WHERE ${BaseColumns._ID} = $goalID) as $startTimeColumn"
        val goalTotalTime = "SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $totalTimeColumn"

        val projection = arrayOf(startTimeQuery, goalTotalTime)
        val selection = "${GoalDatabaseConstants.SessionTable.GOAL_ID} = ?"
        val selectionArgs = arrayOf(goalID.toString())

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
                val startTime = getLong(getColumnIndexOrThrow(startTimeColumn))
                val today = setCalendarToDayStart(Calendar.getInstance())
                val totalTime = getDouble(getColumnIndexOrThrow(totalTimeColumn))

                val days = getTimeDifferenceInDays(Calendar.getInstance().apply { timeInMillis = startTime }, today) + 1
                return totalTime/days
            }
        }

        return 0.0
    }

    fun getGoalTimeThisMonth(): Double{
        val db = this.readableDatabase
        val resultColumn = "SUMA"

        val monthStart = setCalendarToDayStart(Calendar.getInstance())
        monthStart.set(Calendar.DAY_OF_MONTH, monthStart.getActualMinimum(Calendar.DAY_OF_MONTH))
        val monthEnd = setCalendarToDayStart(Calendar.getInstance())
        monthEnd.set(Calendar.DAY_OF_MONTH, monthEnd.getActualMaximum(Calendar.DAY_OF_MONTH))

        val moreThan = monthStart.timeInMillis
        val lessThan = monthEnd.timeInMillis

        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $resultColumn")
        val selection = "${GoalDatabaseConstants.SessionTable.SESSION_DATE} >= ? AND " +
                "${GoalDatabaseConstants.SessionTable.SESSION_DATE} <= ? AND " +
                "${GoalDatabaseConstants.SessionTable.GOAL_ID} = ?"
        val selectionArgs = arrayOf(moreThan.toString(), lessThan.toString(), goalID.toString())

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

    fun getGoalAverageMonthlyTime(): Double{
        val db = this.readableDatabase
        val totalTimeColumn = "TotalTime"
        val startTimeColumn = "goalStartTime"

        val startTimeQuery = "(SELECT ${GoalDatabaseConstants.TimeGoalTable.START_TIME} FROM ${GoalDatabaseConstants.TimeGoalTable.TABLE_NAME} WHERE ${BaseColumns._ID} = $goalID) as $startTimeColumn"
        val goalTotalTime = "SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $totalTimeColumn"

        val projection = arrayOf(startTimeQuery, goalTotalTime)
        val selection = "${GoalDatabaseConstants.SessionTable.GOAL_ID} = ?"
        val selectionArgs = arrayOf(goalID.toString())

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
                val startTime = getLong(getColumnIndexOrThrow(startTimeColumn))
                val today = setCalendarToDayStart(Calendar.getInstance())
                val totalTime = getDouble(getColumnIndexOrThrow(totalTimeColumn))

                val months = getTimeInMonths(startTime, today.timeInMillis)

                return totalTime/months
            }
        }

        return 0.0
    }

    fun getGoalExpectedMonthTime(): Double{
        val db = this.readableDatabase
        val timeSpentColumn = "TimeSpent"
        val deadlineColumn = "GoalDeadline"
        val goalTimeColumn = "GoalTime"

        val goalTotalTime = "SELECT ${GoalDatabaseConstants.TimeGoalTable.GOAL_TIME_AMOUNT} FROM ${GoalDatabaseConstants.TimeGoalTable.TABLE_NAME} WHERE ${BaseColumns._ID} = $goalID"
        val projection = arrayOf(
            "($goalTotalTime) as $goalTimeColumn",
            "SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $timeSpentColumn",
            "(SELECT ${GoalDatabaseConstants.TimeGoalTable.DEADLINE} FROM ${GoalDatabaseConstants.TimeGoalTable.TABLE_NAME} WHERE ${BaseColumns._ID} = $goalID) as $deadlineColumn"
        )
        val selection = "${GoalDatabaseConstants.SessionTable.GOAL_ID} = ?"
        val selectionArgs = arrayOf(goalID.toString())

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
                val timeSpent = getDouble(getColumnIndexOrThrow(timeSpentColumn))
                val goalTime = getDouble(getColumnIndexOrThrow(goalTimeColumn))
                val deadline = getLong(getColumnIndexOrThrow(deadlineColumn))
                val today = setCalendarToDayStart(Calendar.getInstance())
                val untilDeadline = getTimeInMonths(today.timeInMillis, deadline)
                return if(timeSpent.isNaN()) (goalTime + getGoalTimeThisMonth())/untilDeadline else (goalTime - timeSpent + getGoalTimeThisMonth())/untilDeadline
            }
        }

        return 0.0
    }

    fun getGoalTimeThisYear(): Double{
        val db = this.readableDatabase
        val resultColumn = "SUMA"

        val yearStart = setCalendarToDayStart(Calendar.getInstance())
        yearStart.set(Calendar.MONTH, yearStart.getActualMinimum(Calendar.MONTH))
        yearStart.set(Calendar.DAY_OF_MONTH, yearStart.getActualMinimum(Calendar.DAY_OF_MONTH))
        val yearEnd = setCalendarToDayStart(Calendar.getInstance())
        yearEnd.set(Calendar.MONTH, yearStart.getActualMaximum(Calendar.MONTH))
        yearEnd.set(Calendar.DAY_OF_MONTH, yearEnd.getActualMaximum(Calendar.DAY_OF_MONTH))


        val moreThan = yearStart.timeInMillis
        val lessThan = yearEnd.timeInMillis

        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $resultColumn")
        val selection = "${GoalDatabaseConstants.SessionTable.SESSION_DATE} >= ? AND " +
                "${GoalDatabaseConstants.SessionTable.SESSION_DATE} <= ? AND " +
                "${GoalDatabaseConstants.SessionTable.GOAL_ID} = ?"
        val selectionArgs = arrayOf(moreThan.toString(), lessThan.toString(), goalID.toString())

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

    fun getGoalAverageYearlyTime(): Double{
        val db = this.readableDatabase
        val totalTimeColumn = "TotalTime"
        val startTimeColumn = "goalStartTime"

        val startTimeQuery = "(SELECT ${GoalDatabaseConstants.TimeGoalTable.START_TIME} FROM ${GoalDatabaseConstants.TimeGoalTable.TABLE_NAME} WHERE ${BaseColumns._ID} = $goalID) as $startTimeColumn"
        val goalTotalTime = "SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $totalTimeColumn"

        val projection = arrayOf(startTimeQuery, goalTotalTime)
        val selection = "${GoalDatabaseConstants.SessionTable.GOAL_ID} = ?"
        val selectionArgs = arrayOf(goalID.toString())

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
                val startTime = getLong(getColumnIndexOrThrow(startTimeColumn))
                val today = setCalendarToDayStart(Calendar.getInstance())
                val totalTime = getDouble(getColumnIndexOrThrow(totalTimeColumn))

                val years = getTimeInYears(startTime, today.timeInMillis)

                return totalTime/years
            }
        }

        return 0.0
    }

    fun getGoalExpectedYearTime(): Double{
        val db = this.readableDatabase
        val timeSpentColumn = "TimeSpent"
        val deadlineColumn = "GoalDeadline"
        val goalTimeColumn = "GoalTime"

        val goalTotalTime = "SELECT ${GoalDatabaseConstants.TimeGoalTable.GOAL_TIME_AMOUNT} FROM ${GoalDatabaseConstants.TimeGoalTable.TABLE_NAME} WHERE ${BaseColumns._ID} = $goalID"
        val projection = arrayOf(
            "($goalTotalTime) as $goalTimeColumn",
            "SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as $timeSpentColumn",
            "(SELECT ${GoalDatabaseConstants.TimeGoalTable.DEADLINE} FROM ${GoalDatabaseConstants.TimeGoalTable.TABLE_NAME} WHERE ${BaseColumns._ID} = $goalID) as $deadlineColumn"
        )
        val selection = "${GoalDatabaseConstants.SessionTable.GOAL_ID} = ?"
        val selectionArgs = arrayOf(goalID.toString())

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
                val timeSpent = getDouble(getColumnIndexOrThrow(timeSpentColumn))
                val goalTime = getDouble(getColumnIndexOrThrow(goalTimeColumn))
                val deadline = getLong(getColumnIndexOrThrow(deadlineColumn))
                val today = setCalendarToDayStart(Calendar.getInstance())
                val untilDeadline = getTimeInYears(today.timeInMillis, deadline)
                return if(timeSpent.isNaN()) goalTime/untilDeadline else (goalTime - timeSpent + getGoalTimeThisYear())/untilDeadline
            }
        }

        return 0.0
    }
}

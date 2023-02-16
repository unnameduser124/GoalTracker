package com.example.goaltracker.database

import android.content.Context
import android.provider.BaseColumns

class StatsDatabaseService(context: Context): GoalDatabase(context) {
    fun getTotalTime(): Double{
        val db = this.readableDatabase

        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as SUMA")

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
                return getDouble(getColumnIndexOrThrow("SUMA"))
            }
        }
        return 0.0
    }
    fun getMostTimeInDay(): Double{
        val db = this.readableDatabase

        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as SUMA")

        val groupBy = "${GoalDatabaseConstants.SessionTable.SESSION_DATE}"
        val orderBy = "SUMA DESC"

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
                return getDouble(getColumnIndexOrThrow("SUMA"))
            }
        }
        return 0.0
    }
    fun getGoalWithMostTime(): Long{
        val db = this.readableDatabase

        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT}) as SUMA", "${GoalDatabaseConstants.SessionTable.GOAL_ID}")

        val groupBy = "${GoalDatabaseConstants.SessionTable.GOAL_ID}"
        val orderBy = "SUMA DESC"

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

        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT})/COUNT( DISTINCT ${GoalDatabaseConstants.SessionTable.GOAL_ID}) as AVERAGE")

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
                return getDouble(getColumnIndexOrThrow("AVERAGE"))
            }

        }
        return 0.0
    }
    fun getAverageTimePerDay(): Double{
        val db = this.readableDatabase

        val projection = arrayOf("SUM(${GoalDatabaseConstants.SessionTable.TIME_AMOUNT})/COUNT( DISTINCT ${GoalDatabaseConstants.SessionTable.SESSION_DATE}) as AVERAGE")

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
                return getDouble(getColumnIndexOrThrow("AVERAGE"))
            }

        }
        return 0.0
    }
    fun getAverageTimePerWeek(): Double{
        TODO("Not implemented yet")
    }
    fun getAverageTimePerMonth(): Double{
        TODO("Not implemented yet")
    }
    fun getAverageTimePerYear(): Double{
        TODO("Not implemented yet")
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
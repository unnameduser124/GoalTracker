package com.example.goaltracker.database

import android.provider.BaseColumns

object GoalDatabaseConstants {

    const val DATABASE_NAME = "GoalDB.db"
    const val DATABASE_VERSION = 1

    const val CREATE_TIME_GOAL_TABLE_QUERY = "CREATE TABLE ${TimeGoalTable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
            "${TimeGoalTable.GOAL_NAME} TEXT NOT NULL," +
            "${TimeGoalTable.START_TIME} INTEGER NOT NULL," +
            "${TimeGoalTable.GOAL_TIME_AMOUNT} REAL NOT NULL," +
            "${TimeGoalTable.DEADLINE} INTEGER NOT NULL)"

    const val CREATE_SESSION_TABLE_QUERY = "CREATE TABLE ${SessionTable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
            "${SessionTable.SESSION_DATE} INTEGER NOT NULL," +
            "${SessionTable.GOAL_ID} INTEGER NOT NULL," +
            "${SessionTable.TIME_AMOUNT} REAL NOT NULL)"

    const val CLEAR_SESSION_TABLE_DATA = "DELETE FROM ${SessionTable.TABLE_NAME}"
    const val CLEAR_TIME_GOAL_TABLE_DATA = "DELETE FROM ${TimeGoalTable.TABLE_NAME}"

    object TimeGoalTable: BaseColumns{
        const val TABLE_NAME = "TimeGoals"
        const val GOAL_NAME = "goalName"
        const val GOAL_TIME_AMOUNT = "goalTimeAmount"
        const val START_TIME = "startTime"
        const val DEADLINE = "deadline"
    }
    object SessionTable: BaseColumns{
        const val TABLE_NAME = "Sessions"
        const val SESSION_DATE = "sessionDate"
        const val TIME_AMOUNT = "timeAmount"
        const val GOAL_ID = "goalID"
    }
}
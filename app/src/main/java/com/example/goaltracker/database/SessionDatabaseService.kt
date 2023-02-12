package com.example.goaltracker.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.example.goaltracker.dataClasses.DataCountdownGoal
import com.example.goaltracker.dataClasses.DataGoalSession
import com.example.goaltracker.dataClasses.DataTimeGoal
import com.example.goaltracker.goal.TimeGoal
import com.example.goaltracker.database.GoalDatabaseConstants.SessionTable.TABLE_NAME
import com.example.goaltracker.database.GoalDatabaseConstants.SessionTable.GOAL_ID
import com.example.goaltracker.database.GoalDatabaseConstants.SessionTable.TIME_AMOUNT
import com.example.goaltracker.database.GoalDatabaseConstants.SessionTable.SESSION_DATE
import com.example.goaltracker.goal.GoalSession

class SessionDatabaseService(context: Context): GoalDatabase(context) {

    fun addGoal(session: GoalSession){
        val goalSession = DataGoalSession(session)

        val contentValues = ContentValues().apply{
            put(TIME_AMOUNT, goalSession.timeAmount)
            put(TIME_AMOUNT, goalSession.date)
            put(TIME_AMOUNT, goalSession.goalID)
        }

        this.writableDatabase.insert(TABLE_NAME, null, contentValues)
    }

    fun getGoalByID(id: Long): DataGoalSession?{
        val db = this.readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            TIME_AMOUNT,
            SESSION_DATE,
            GOAL_ID
        )

        val cursor = db.query(
            TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor){
            while(moveToNext()) {
                val goalID = getLong(getColumnIndexOrThrow(GOAL_ID))
                val sessionID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val timeAmount = getLong(getColumnIndexOrThrow(TIME_AMOUNT))
                val date = getLong(getColumnIndexOrThrow(SESSION_DATE))

                return DataGoalSession(
                    sessionID,
                    timeAmount,
                    date,
                    goalID
                )
            }
        }
        return null
    }

    fun deleteGoalByID(id: Long){
        val db = this.writableDatabase

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf(id.toString())

        db.delete(TABLE_NAME, selection, selectionArgs)
    }

}
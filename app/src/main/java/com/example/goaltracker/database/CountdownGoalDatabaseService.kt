package com.example.goaltracker.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.example.goaltracker.dataClasses.DataCountdownGoal
import com.example.goaltracker.database.GoalDatabaseConstants.CountdownGoalTable.END_TIME
import com.example.goaltracker.database.GoalDatabaseConstants.CountdownGoalTable.GOAL_NAME
import com.example.goaltracker.database.GoalDatabaseConstants.CountdownGoalTable.START_TIME
import com.example.goaltracker.database.GoalDatabaseConstants.CountdownGoalTable.TABLE_NAME
import com.example.goaltracker.goal.CountdownGoal

class CountdownGoalDatabaseService(context: Context): GoalDatabase(context) {

    fun addGoal(countdownGoal: CountdownGoal){
        val goal = DataCountdownGoal(countdownGoal)

        val contentValues = ContentValues().apply{
            put(GOAL_NAME, goal.goalName)
            put(START_TIME, goal.goalStartTime)
            put(END_TIME, goal.goalEndTime)
        }

        this.writableDatabase.insert(TABLE_NAME, null, contentValues)
    }

    fun getGoalByID(id: Long): DataCountdownGoal?{
        val db = this.readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            GOAL_NAME,
            START_TIME,
            END_TIME
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
                val goalID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val goalName = getString(getColumnIndexOrThrow(GOAL_NAME))
                val startTime = getLong(getColumnIndexOrThrow(START_TIME))
                val endTime = getLong(getColumnIndexOrThrow(END_TIME))

                return DataCountdownGoal(
                    goalID,
                    goalName,
                    startTime,
                    endTime
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
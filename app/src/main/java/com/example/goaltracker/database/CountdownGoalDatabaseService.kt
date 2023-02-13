package com.example.goaltracker.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.example.goaltracker.dataClasses.DataCountdownGoal
import com.example.goaltracker.dataClasses.DataGoalSession
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

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        val cursor = db.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
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

    fun updateGoalByID(id: Long, goal: CountdownGoal){
        val db = this.writableDatabase

        val dataGoal = DataCountdownGoal(goal)

        val contentValues = ContentValues().apply{
            put(START_TIME, dataGoal.goalStartTime)
            put(END_TIME, dataGoal.goalEndTime)
            put(GOAL_NAME, dataGoal.goalName)
        }

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }

    fun deleteGoalByID(id: Long){
        val db = this.writableDatabase

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf(id.toString())

        db.delete(TABLE_NAME, selection, selectionArgs)
    }

}
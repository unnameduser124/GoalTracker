package com.example.goaltracker.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.example.goaltracker.dataClasses.DataTimeGoal
import com.example.goaltracker.database.GoalDatabaseConstants.TimeGoalTable.CURRENT_TIME_AMOUNT
import com.example.goaltracker.database.GoalDatabaseConstants.TimeGoalTable.DEADLINE
import com.example.goaltracker.goal.TimeGoal
import com.example.goaltracker.database.GoalDatabaseConstants.TimeGoalTable.GOAL_NAME
import com.example.goaltracker.database.GoalDatabaseConstants.TimeGoalTable.GOAL_TIME_AMOUNT
import com.example.goaltracker.database.GoalDatabaseConstants.TimeGoalTable.START_TIME
import com.example.goaltracker.database.GoalDatabaseConstants.TimeGoalTable.TABLE_NAME

class TimeGoalDatabaseService(context: Context): GoalDatabase(context){

    fun addGoal(timeGoal: TimeGoal){
        val goal = DataTimeGoal(timeGoal)

        val contentValues = ContentValues().apply{
            put(GOAL_NAME, goal.goalName)
            put(START_TIME, goal.startTime)
            put(DEADLINE, goal.deadline)
            put(GOAL_TIME_AMOUNT, goal.goalTimeAmount)
            put(CURRENT_TIME_AMOUNT, goal.currentTimeAmount)
        }

        this.writableDatabase.insert(TABLE_NAME, null, contentValues)
    }

    fun getGoalByID(id: Long): DataTimeGoal?{
        val db = this.readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            GOAL_NAME,
            GOAL_TIME_AMOUNT,
            CURRENT_TIME_AMOUNT,
            START_TIME,
            DEADLINE
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
                val goalTimeAmount = getLong(getColumnIndexOrThrow(GOAL_TIME_AMOUNT))
                val currentTimeAmount = getLong(getColumnIndexOrThrow(CURRENT_TIME_AMOUNT))
                val startTime = getLong(getColumnIndexOrThrow(START_TIME))
                val deadline = getLong(getColumnIndexOrThrow(DEADLINE))

                return DataTimeGoal(
                    goalID,
                    goalName,
                    goalTimeAmount,
                    currentTimeAmount,
                    startTime,
                    deadline
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
package com.example.goaltracker.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import androidx.core.content.contentValuesOf
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

    fun updateGoalByID(id: Long, goal: TimeGoal){
        val db = this.writableDatabase

        val dataGoal = DataTimeGoal(goal)

        val contentValues = ContentValues().apply{
            put(GOAL_TIME_AMOUNT, dataGoal.goalTimeAmount)
            put(CURRENT_TIME_AMOUNT, dataGoal.currentTimeAmount)
            put(START_TIME, dataGoal.startTime)
            put(DEADLINE, dataGoal.deadline)
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

    fun getAllGoalNames(): List<TimeGoal>{
        val db = this.readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            GOAL_NAME
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

        val goalList = mutableListOf<TimeGoal>()

        with(cursor){
            while(moveToNext()){
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val goalName = getString(getColumnIndexOrThrow(GOAL_NAME))

                val dataGoal = DataTimeGoal(id, goalName, 0, 0, 0, 0)
                goalList.add(TimeGoal(dataGoal))
            }
        }

        return goalList
    }
}
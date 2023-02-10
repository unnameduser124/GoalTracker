package com.example.goaltracker.database

import android.content.ContentValues
import android.content.Context
import com.example.goaltracker.dataClasses.DataTimeGoal
import com.example.goaltracker.database.GoalDatabaseConstants.TimeGoalTable.CURRENT_TIME_AMOUNT
import com.example.goaltracker.database.GoalDatabaseConstants.TimeGoalTable.DEADLINE
import com.example.goaltracker.goal.TimeGoal
import com.example.goaltracker.database.GoalDatabaseConstants.TimeGoalTable.GOAL_NAME
import com.example.goaltracker.database.GoalDatabaseConstants.TimeGoalTable.GOAL_TIME_AMOUNT
import com.example.goaltracker.database.GoalDatabaseConstants.TimeGoalTable.START_TIME

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
    }

}
package com.example.goaltracker.dataClasses

import com.example.goaltracker.goal.TimeGoal

data class DataTimeGoal(val goalID: Long,
                        val goalName: String,
                        var goalTimeAmount: Long,
                        var currentTimeAmount: Long,
                        var startTime: Long,
                        var deadline: Long){
    constructor(goal: TimeGoal): this(goal.goalID, goal.goalName, -1L, -1L, -1L, 1L){
        goalTimeAmount = (goal.goalTimeAmount * 3600).toLong()
        currentTimeAmount = (goal.currentTimeAmount * 3600).toLong()
        startTime = goal.startTime.timeInMillis / 1000
        deadline = goal.deadline.timeInMillis / 1000
    }
}

package com.example.goaltracker.dataClasses

import com.example.goaltracker.goal.TimeGoal

data class DataTimeGoal(val goalID: Long,
                        val goalName: String,
                        var goalTimeAmount: Double,
                        var startTime: Long,
                        var deadline: Long){
    constructor(goal: TimeGoal): this(goal.ID, goal.name, goal.goalTimeAmount,  -1L, 1L){
        startTime = goal.startTime.timeInMillis / 1000
        deadline = goal.deadline.timeInMillis / 1000
    }
}

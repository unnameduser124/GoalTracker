package com.example.goaltracker.dataClasses

import com.example.goaltracker.goal.CountdownGoal

data class DataCountdownGoal(val goalID: Long,
                             val goalName: String,
                             var goalStartTime: Long,
                             var goalEndTime: Long){
    constructor(goal: CountdownGoal): this(goal.ID,
    goal.name,
    goal.startTime.timeInMillis/1000,
    goal.endTime.timeInMillis/1000)
}
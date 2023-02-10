package com.example.goaltracker.goal

import com.example.goaltracker.dataClasses.DataCountdownGoal
import java.util.*

class CountdownGoal(val goalID: Long,
                    var goalName: String,
                    var startTime: Calendar,
                    var endTime: Calendar,
                    val sessionList: MutableList<GoalSession>){
    constructor(data: DataCountdownGoal): this(data.goalID, data.goalName, Calendar.getInstance(), Calendar.getInstance(), mutableListOf()){
        startTime.timeInMillis = data.goalStartTime * 1000
        endTime.timeInMillis = data.goalEndTime * 1000
    }
}
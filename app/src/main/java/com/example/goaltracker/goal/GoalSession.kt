package com.example.goaltracker.goal

import com.example.goaltracker.dataClasses.DataGoalSession
import java.util.*

class GoalSession(val sessionID: Long,
                       val date: Calendar,
                       var timeAmount: Double,
                       val goalID: Long){
    constructor(data: DataGoalSession): this(data.sessionID, Calendar.getInstance(), -1.0, data.goalID){
        date.timeInMillis = data.date * 1000
        timeAmount = data.timeAmount/3600.0
    }
}

package com.example.goaltracker.goal

import com.example.goaltracker.dataClasses.DataGoalSession
import java.util.*

class GoalSession(val ID: Long,
                  val date: Calendar,
                  var timeAmount: Double,
                  val goalID: Long){
    constructor(data: DataGoalSession): this(data.sessionID, Calendar.getInstance(), -1.0, data.goalID){
        println(data.timeAmount)
        date.timeInMillis = data.date
        timeAmount = data.timeAmount
    }
}

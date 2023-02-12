package com.example.goaltracker.dataClasses

import com.example.goaltracker.goal.GoalSession

data class DataGoalSession(val sessionID: Long,
                           val date: Long,
                           val timeAmount: Long,
                           val goalID: Long){
    constructor(goalSession: GoalSession): this(goalSession.sessionID,
        goalSession.date.timeInMillis/1000,
        (goalSession.timeAmount*3600).toLong(),
        goalSession.goalID)
}

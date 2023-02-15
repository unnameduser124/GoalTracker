package com.example.goaltracker.dataClasses

import com.example.goaltracker.goal.GoalSession

data class DataGoalSession(val sessionID: Long,
                           val date: Long,
                           val timeAmount: Double,
                           val goalID: Long){

    constructor(goalSession: GoalSession): this(goalSession.ID,
        goalSession.date.timeInMillis,
        goalSession.timeAmount,
        goalSession.goalID)

}

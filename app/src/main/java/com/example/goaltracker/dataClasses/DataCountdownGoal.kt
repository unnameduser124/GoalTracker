package com.example.goaltracker.dataClasses

data class DataCountdownGoal(val goalID: Long,
                             val goalName: String,
                             var goalStartTime: Long,
                             var goalEndTime: Long)
package com.example.goaltracker

import com.example.goaltracker.goal.TimeGoal
import com.example.goaltracker.goal.TimeMeasureUnit
import com.example.goaltracker.goal.getTimeDebt
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TimeGoalTests {

    @Test
    fun timeDebtTest(){

        val startTime = Calendar.getInstance().apply{
            set(Calendar.DAY_OF_YEAR, 1)
        }

        val endTime = Calendar.getInstance().apply{
            set(Calendar.DAY_OF_YEAR, 9)
        }
        val goal = TimeGoal(100.0, 20.0, TimeMeasureUnit.Hour, startTime, endTime)

        assertEquals(getTimeDebt(goal), 20.0, 0.1)

        goal.currentTimeAmount = 47.0

        assertEquals(getTimeDebt(goal), -7.0, 0.1)
    }
}
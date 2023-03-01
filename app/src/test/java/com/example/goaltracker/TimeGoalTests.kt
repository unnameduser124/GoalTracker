package com.example.goaltracker

import com.example.goaltracker.goal.*
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TimeGoalTests {

    /*@Test
    fun timeDebtTest() {

        val startTime = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, 1)
        }

        val endTime = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, 9)
        }
        val goal = TimeGoal(100.0, 20.0, TimeMeasureUnit.Hour, startTime, endTime)

        assertEquals(20.0, getTimeDebt(goal), 0.1)

        goal.currentTimeAmount = 47.0

        assertEquals(-7.0, getTimeDebt(goal), 0.1)
    }*/

    /*@Test
    fun monthDifferenceTest() {
        val startTime = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, 20)
            set(Calendar.YEAR, 2022)
        }

        val endTime = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, 74)
        }

        val monthDiff = getTimeDifferenceInMonths(startTime, endTime)

        assertEquals(13.83, monthDiff, 0.01)
    }
    @Test
    fun yearDifferenceTest() {
        val startTime = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.YEAR, 2020)
        }

        val endTime = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, 65)
        }

        val yearDiff = getTimeDifferenceInYears(startTime, endTime)

        assertEquals(3.17, yearDiff, 0.01)
    }*/
}
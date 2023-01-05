package com.example.goaltracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.goaltracker.goal.TimeGoal
import com.example.goaltracker.goal.TimeMeasureUnit
import com.example.goaltracker.goal.getTimeDebt
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startTime = Calendar.getInstance().apply{
            set(Calendar.DAY_OF_YEAR, 1)
        }
        println(startTime.time)

        val endTime = Calendar.getInstance().apply{
            set(Calendar.DAY_OF_YEAR, 9)
        }
        println(endTime.time)
        val goal = TimeGoal(100.0, 20.0, TimeMeasureUnit.Hour, startTime, endTime)

        println(getTimeDebt(goal))
    }
}
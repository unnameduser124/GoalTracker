package com.example.goaltracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.goaltracker.database.GoalDatabase
import com.example.goaltracker.goal.TimeGoal
import com.example.goaltracker.goal.getTimeDebt
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GoalDatabase(this)
    }
}
package com.example.goaltracker.goalActivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.goaltracker.MainActivity
import com.example.goaltracker.R
import com.example.goaltracker.database.TimeGoalDatabaseService
import com.example.goaltracker.databinding.GoalActivityBinding
import com.example.goaltracker.goal.TimeGoal
import java.text.SimpleDateFormat
import java.util.*

class GoalActivity: AppCompatActivity() {
    private lateinit var binding: GoalActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GoalActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val goalID = intent.getLongExtra("GOAL_ID", 0)

        val dbService = TimeGoalDatabaseService(this)
        val dataGoal = dbService.getGoalByID(goalID)

        lateinit var goal: TimeGoal
        if(dataGoal!=null){
            goal = TimeGoal(dataGoal)
        }
        else{
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
            return
        }

        binding.appbarGoalName.text = goal.name
        binding.goalTimeAmount.text = String.format(getString(R.string.goal_time_amount_placeholder), goal.goalTimeAmount)
        binding.currentTimeAmount.text = String.format(getString(R.string.goal_time_amount_placeholder), goal.currentTimeAmount)
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
        binding.goalStartTime.text = simpleDateFormat.format(goal.startTime.time)
        binding.goalDeadline.text = simpleDateFormat.format(goal.deadline.time)
    }
}
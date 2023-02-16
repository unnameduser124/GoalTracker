package com.example.goaltracker.sessionList

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.goaltracker.database.SessionDatabaseService
import com.example.goaltracker.databinding.SessionListBinding
import com.example.goaltracker.goalActivity.GoalActivity

class GoalSessionListActivity: AppCompatActivity() {

   private lateinit var binding: SessionListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SessionListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val goalID = intent.getLongExtra("GOAL_ID", 0)
        if(goalID==0L){
            return
        }

        val dbService = SessionDatabaseService(this)
        val dataset = dbService.getSessionsForGoal(goalID)
        dataset.sortByDescending { it.date.timeInMillis }

        val itemAdapter = SessionListItemAdapter(dataset, binding.sessionsRecyclerView)
        val linearLayout = LinearLayoutManager(this)
        binding.sessionsRecyclerView.adapter = itemAdapter
        binding.sessionsRecyclerView.layoutManager = linearLayout
        binding.sessionsRecyclerView.setHasFixedSize(false)

        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(binding.sessionsRecyclerView.context, GoalActivity::class.java)
                intent.putExtra("GOAL_ID", goalID)
                finish()
                startActivity(intent)
            }
        })
    }
}
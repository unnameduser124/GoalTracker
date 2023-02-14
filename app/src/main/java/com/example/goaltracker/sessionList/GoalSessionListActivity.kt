package com.example.goaltracker.sessionList

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.goaltracker.database.SessionDatabaseService
import com.example.goaltracker.databinding.SessionListBinding

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

        val itemAdapter = SessionListItemAdapter(dataset, binding.sessionsRecyclerView)
        val linearLayout = LinearLayoutManager(this)
        binding.sessionsRecyclerView.adapter = itemAdapter
        binding.sessionsRecyclerView.layoutManager = linearLayout
        binding.sessionsRecyclerView.setHasFixedSize(false)
    }
}
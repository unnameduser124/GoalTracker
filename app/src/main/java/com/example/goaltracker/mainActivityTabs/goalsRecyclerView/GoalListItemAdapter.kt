package com.example.goaltracker.mainActivityTabs.goalsRecyclerView

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.goaltracker.R
import com.example.goaltracker.goal.TimeGoal
import com.example.goaltracker.goalActivity.GoalActivity

class GoalListItemAdapter(private val dataset: List<TimeGoal>): RecyclerView.Adapter<GoalListItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val goalName: TextView = view.findViewById(R.id.goal_list_item_goal_name)
        val goalItemLayout: ConstraintLayout = view.findViewById(R.id.goal_list_item_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.goal_list_item,
            parent,
            false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val goal = dataset[position]
        holder.goalName.text = goal.name
        holder.goalItemLayout.setOnClickListener {
            val intent = Intent(holder.goalName.context, GoalActivity::class.java)
            intent.putExtra("GOAL_ID", goal.ID)
            holder.goalName.context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataset.size

}
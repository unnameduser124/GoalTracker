package com.example.goaltracker.sessionList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.goaltracker.R
import com.example.goaltracker.database.SessionDatabaseService
import com.example.goaltracker.doubleHoursToHoursAndMinutes
import com.example.goaltracker.goal.GoalSession
import java.text.SimpleDateFormat
import java.util.*

class SessionListItemAdapter(private val dataset: MutableList<GoalSession>,
                             private val recyclerView: RecyclerView
                             ): RecyclerView.Adapter<SessionListItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val sessionDate: TextView = view.findViewById(R.id.session_date)
        val sessionTimeAmount: TextView = view.findViewById(R.id.session_value)
        val deleteButton: Button = view.findViewById(R.id.session_delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.session_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val session = dataset[position]
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
        val date = sdf.format(session.date.time)
        holder.sessionDate.text = date

        val timeAmount = doubleHoursToHoursAndMinutes(session.timeAmount)
        holder.sessionTimeAmount.text = String.format(
            holder.sessionTimeAmount.context.getString(R.string.hours_and_minutes_placeholder),
            timeAmount.first,
            timeAmount.second
        )
        holder.deleteButton.setOnClickListener {
            val dbService = SessionDatabaseService(holder.deleteButton.context)
            dbService.deleteSessionByID(session.ID)
            dataset.removeAt(position)
            recyclerView.adapter?.notifyItemRemoved(position)
            recyclerView.adapter?.notifyItemRangeChanged(position, dataset.size)
        }
    }

    override fun getItemCount(): Int = dataset.size
}
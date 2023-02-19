package com.example.goaltracker.mainActivityTabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.goaltracker.AddActivity
import com.example.goaltracker.database.TimeGoalDatabaseService
import com.example.goaltracker.databinding.GoalListBinding
import com.example.goaltracker.databinding.SessionListBinding
import com.example.goaltracker.mainActivityTabs.goalsRecyclerView.GoalListItemAdapter

class HomePlaceholderFragment: Fragment() {

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this)[PageViewModel::class.java].apply {
            setIndex(arguments?.getInt(SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = GoalListBinding.inflate(layoutInflater)
        val dbService = TimeGoalDatabaseService(requireContext())
        val dataset = dbService.getAllGoalNames()

        val adapter = GoalListItemAdapter(dataset.toMutableList())
        val layoutManager = LinearLayoutManager(requireContext())
        binding.goalListRecyclerView.layoutManager = layoutManager
        binding.goalListRecyclerView.adapter = adapter
        binding.goalListRecyclerView.setHasFixedSize(true)

        binding.addButtonHome.setOnClickListener {
            val intent = Intent(requireContext(), AddActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    companion object{
        private const val SECTION_NUMBER = "section number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): HomePlaceholderFragment {
            return HomePlaceholderFragment().apply{
                arguments = Bundle().apply{
                    putInt(SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}
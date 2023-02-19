package com.example.goaltracker.mainActivityTabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.goaltracker.HOURS_ROUND_MULTIPLIER
import com.example.goaltracker.R
import com.example.goaltracker.database.GlobalStatsDatabaseService
import com.example.goaltracker.database.TimeGoalDatabaseService
import com.example.goaltracker.databinding.GlobalStatisticsTabBinding
import com.example.goaltracker.roundDouble

class GlobalStatsPlaceholderFragment: Fragment() {
    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(GlobalStatsPlaceholderFragment.SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = GlobalStatisticsTabBinding.inflate(layoutInflater)
        val dbService = GlobalStatsDatabaseService(requireContext())
        val totalTime = roundDouble(dbService.getTotalTime(), HOURS_ROUND_MULTIPLIER)
        binding.globalStatsTotalTime.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            totalTime
        )

        val mostTimeInDay = roundDouble(dbService.getMostTimeInDay(), HOURS_ROUND_MULTIPLIER)
        binding.globalStatsMostTimeInDay.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            mostTimeInDay
        )

        var goalID = dbService.getGoalWithMostTime()
        binding.globalStatsGoalWithMostTime.text = goalName(goalID)

        goalID = dbService.getLongestActiveGoal()
        binding.globalStatsLongestGoal.text = goalName(goalID)

        val averageTimePerGoal = roundDouble(dbService.getAverageTimePerGoal(), HOURS_ROUND_MULTIPLIER)
        binding.globalStatsAvgTimePerGoal.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            averageTimePerGoal
        )

        val averageTimePerDay = roundDouble(dbService.getAverageTimePerDay(), HOURS_ROUND_MULTIPLIER)
        binding.globalStatsAvgTimePerDay.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            averageTimePerDay
        )

        val averageTimePerWeek = roundDouble(dbService.getAverageTimePerWeek(), HOURS_ROUND_MULTIPLIER)
        binding.globalStatsAvgTimePerWeek.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            averageTimePerWeek
        )

        val averageTimePerMonth = roundDouble(dbService.getAverageTimePerMonth(), HOURS_ROUND_MULTIPLIER)
        binding.globalStatsAvgTimePerMonth.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            averageTimePerMonth
        )

        val averageTimePerYear = roundDouble(dbService.getAverageTimePerYear(), HOURS_ROUND_MULTIPLIER)
        binding.globalStatsAvgTimePerYear.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            averageTimePerYear
        )

        val timeWithinLastWeek = roundDouble(dbService.getTimeWithinLastWeek(), HOURS_ROUND_MULTIPLIER)
        binding.globalStatsTimeWithinLastWeek.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            timeWithinLastWeek
        )


        val timeThisMonth = roundDouble(dbService.getTimeThisMonth(), HOURS_ROUND_MULTIPLIER)
        binding.globalStatsTimeThisMonth.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            timeThisMonth
        )

        val timeThisYear = roundDouble(dbService.getTimeThisYear(), HOURS_ROUND_MULTIPLIER)
        binding.globalStatsTimeThisYear.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            timeThisYear
        )
        return binding.root
    }
    companion object{

        private const val SECTION_NUMBER = "section number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): GlobalStatsPlaceholderFragment {
            return GlobalStatsPlaceholderFragment().apply{
                arguments = Bundle().apply{
                    putInt(SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    fun goalName(goalID: Long): String{
        val dbService = TimeGoalDatabaseService(requireContext())
        val goal = dbService.getGoalByID(goalID)
        return if(goal?.goalName!=null){
            goal.goalName
        } else{
            "no data"
        }
    }
}
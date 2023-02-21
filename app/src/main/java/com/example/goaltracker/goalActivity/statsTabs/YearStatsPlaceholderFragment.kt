package com.example.goaltracker.goalActivity.statsTabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.goaltracker.HOURS_ROUND_MULTIPLIER
import com.example.goaltracker.PERCENTAGE_ROUND_MULTIPLIER
import com.example.goaltracker.R
import com.example.goaltracker.database.GoalStatsDatabaseService
import com.example.goaltracker.databinding.GoalStatsTabBinding
import com.example.goaltracker.roundDouble

class YearStatsPlaceholderFragment(val goalID: Long): Fragment() {

    private lateinit var pageViewModel:  GoalActivityPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this)[GoalActivityPageViewModel::class.java].apply {
            setIndex(arguments?.getInt(YearStatsPlaceholderFragment.SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = GoalStatsTabBinding.inflate(layoutInflater)

        val dbService = GoalStatsDatabaseService(requireContext(), goalID)

        val yearTime = roundDouble(dbService.getGoalTimeThisYear(), HOURS_ROUND_MULTIPLIER)
        val avgExpected = roundDouble(dbService.getGoalExpectedYearTime(), HOURS_ROUND_MULTIPLIER)
        binding.goalStatsDuration.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            yearTime
        )
        binding.goalStatsAverageToReachGoal.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            if(avgExpected > 0.0) avgExpected else 0.0
        )
        binding.goalStatsProgress.text = if(yearTime != 0.0) roundDouble((yearTime/avgExpected) * 100, PERCENTAGE_ROUND_MULTIPLIER).toString() else "0.0"

        val yearAverage = dbService.getGoalAverageYearlyTime()
        binding.goalStatsAverage.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            yearAverage
        )

        return binding.root
    }


    companion object{
        private const val SECTION_NUMBER = "section number"

        @JvmStatic
        fun newInstance(sectionNumber: Int, goalID: Long): YearStatsPlaceholderFragment {
            return YearStatsPlaceholderFragment(goalID).apply{
                arguments = Bundle().apply{
                    putInt(SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}
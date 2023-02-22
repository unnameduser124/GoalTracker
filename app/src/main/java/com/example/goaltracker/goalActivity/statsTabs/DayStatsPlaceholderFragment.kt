package com.example.goaltracker.goalActivity.statsTabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.goaltracker.HOURS_ROUND_MULTIPLIER
import com.example.goaltracker.PERCENTAGE_ROUND_MULTIPLIER
import com.example.goaltracker.R
import com.example.goaltracker.database.GlobalStatsDatabaseService
import com.example.goaltracker.database.GoalStatsDatabaseService
import com.example.goaltracker.databinding.GoalStatsTabBinding
import com.example.goaltracker.roundDouble

class DayStatsPlaceholderFragment(val goalID: Long): Fragment() {

    private lateinit var pageViewModel:  GoalActivityPageViewModel
    private lateinit var binding: GoalStatsTabBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this)[GoalActivityPageViewModel::class.java].apply {
            setIndex(arguments?.getInt(DayStatsPlaceholderFragment.SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GoalStatsTabBinding.inflate(layoutInflater)
        binding.goalStatsTimeChart.isGone = true

        updateViews()
        return binding.root
    }

    fun updateViews(){
        val dbService = GoalStatsDatabaseService(requireContext(), goalID)

        val dayTime = roundDouble(dbService.getGoalDayTime(), HOURS_ROUND_MULTIPLIER)
        val avgExpected = roundDouble(dbService.getGoalExpectedDayTime(), HOURS_ROUND_MULTIPLIER)
        binding.goalStatsDuration.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            dayTime
        )
        binding.goalStatsAverageToReachGoal.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            if(avgExpected > 0.0) avgExpected else 0.0
        )
        binding.goalStatsProgress.text = if(dayTime != 0.0) roundDouble((dayTime/avgExpected) * 100, PERCENTAGE_ROUND_MULTIPLIER).toString() else "0.0"

        val dayAverage = roundDouble(dbService.getGoalAverageDailyTime(), HOURS_ROUND_MULTIPLIER)
        binding.goalStatsAverage.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            if(dayAverage.isNaN()) 0.0 else dayAverage
        )

        val timeThisMonth = GlobalStatsDatabaseService(requireContext()).getTimeThisMonth()
        val monthlyAverage = dbService.getGoalAverageMonthlyTime()

    }


    companion object{
        private const val SECTION_NUMBER = "section number"

        @JvmStatic
        fun newInstance(sectionNumber: Int, goalID: Long): DayStatsPlaceholderFragment {
            return DayStatsPlaceholderFragment(goalID).apply{
                arguments = Bundle().apply{
                    putInt(SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}
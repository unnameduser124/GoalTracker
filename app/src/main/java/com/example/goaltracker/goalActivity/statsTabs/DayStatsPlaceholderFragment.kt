package com.example.goaltracker.goalActivity.statsTabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.goaltracker.*
import com.example.goaltracker.database.GlobalStatsDatabaseService
import com.example.goaltracker.database.GoalStatsDatabaseService
import com.example.goaltracker.databinding.GoalStatsTabBinding

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

        val dayTime = doubleHoursToHoursAndMinutes(dbService.getGoalDayTime())
        val avgExpected = doubleHoursToHoursAndMinutes(dbService.getGoalExpectedDayTime())
        binding.goalStatsDuration.text = String.format(
            requireContext().getString(R.string.hours_and_minutes_placeholder),
            dayTime.first,
            dayTime.second
        )
        val avgExpectedValue = if(avgExpected.first > 0 && avgExpected.second > 0) avgExpected else Pair(0,0)
        binding.goalStatsAverageToReachGoal.text = String.format(
            requireContext().getString(R.string.hours_and_minutes_placeholder),
            avgExpectedValue.first,
            avgExpectedValue.second
        )
        val progressValue = dayTime.first + dayTime.second/60.0
        val avgExpectedProgressValue = avgExpected.first + avgExpected.second/60.0
        binding.goalStatsProgress.text = if(progressValue != 0.0) roundDouble((progressValue/avgExpectedProgressValue) * 100, PERCENTAGE_ROUND_MULTIPLIER).toString() else "0.0"

        val dayAverage = dbService.getGoalAverageDailyTime()
        val dayAverageValue = if(dayAverage.isNaN()) Pair(0,0) else doubleHoursToHoursAndMinutes(dayAverage)
        binding.goalStatsAverage.text = String.format(
            requireContext().getString(R.string.hours_and_minutes_placeholder),
            dayAverageValue.first,
            dayAverageValue.second
        )
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
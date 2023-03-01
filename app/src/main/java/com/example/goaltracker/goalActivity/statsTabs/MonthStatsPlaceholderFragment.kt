package com.example.goaltracker.goalActivity.statsTabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.goaltracker.*
import com.example.goaltracker.database.GoalStatsDatabaseService
import com.example.goaltracker.database.SessionDatabaseService
import com.example.goaltracker.databinding.GoalStatsTabBinding
import java.util.*

class MonthStatsPlaceholderFragment(val goalID: Long): Fragment() {

    private lateinit var pageViewModel:  GoalActivityPageViewModel
    private lateinit var binding: GoalStatsTabBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this)[GoalActivityPageViewModel::class.java].apply {
            setIndex(arguments?.getInt(SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GoalStatsTabBinding.inflate(layoutInflater)

        updateViews()

        return binding.root
    }

    fun updateViews(){
        val dbService = GoalStatsDatabaseService(requireContext(), goalID)

        val monthTime = dbService.getGoalTimeThisMonth()
        val monthTimeValue = doubleHoursToHoursAndMinutes(dbService.getGoalTimeThisMonth())
        val avgExpected = roundDouble(dbService.getGoalExpectedMonthTime(), HOURS_ROUND_MULTIPLIER)
        binding.goalStatsDuration.text = String.format(
            requireContext().getString(R.string.hours_and_minutes_placeholder),
            monthTimeValue.first,
            monthTimeValue.second
        )
        val avgExpectedHoursAndMinutes = if(avgExpected > 0.0) doubleHoursToHoursAndMinutes(avgExpected) else Pair(0,0)
        binding.goalStatsAverageToReachGoal.text = String.format(
            requireContext().getString(R.string.hours_and_minutes_placeholder),
            avgExpectedHoursAndMinutes.first,
            avgExpectedHoursAndMinutes.second
        )

        binding.goalStatsProgress.text = if(monthTime != 0.0) roundDouble((monthTime/avgExpected) * 100, PERCENTAGE_ROUND_MULTIPLIER).toString() else "0.0"

        val monthlyAverage = doubleHoursToHoursAndMinutes(dbService.getGoalAverageMonthlyTime())
        binding.goalStatsAverage.text = String.format(
            requireContext().getString(R.string.hours_and_minutes_placeholder),
            monthlyAverage.first,
            monthlyAverage.second
        )

        val monthStartCalendar = setCalendarToDayStart(Calendar.getInstance())
        monthStartCalendar.set(Calendar.DAY_OF_MONTH, monthStartCalendar.getActualMinimum(Calendar.DAY_OF_MONTH))
        val monthEndCalendar = setCalendarToDayStart(Calendar.getInstance())
        monthEndCalendar.set(Calendar.DAY_OF_MONTH, monthEndCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))

        val data = SessionDatabaseService(requireContext()).getDailyDurationList(monthStartCalendar.timeInMillis, monthEndCalendar.timeInMillis, goalID)

        binding.goalStatsChartLabel.text = requireContext().getString(R.string.month_chart_label)

        binding.goalStatsTimeChart.clear()
        makeLineChart(binding.goalStatsTimeChart, data, requireContext(), DurationPeriod.ThisMonth)
    }


    companion object{
        private const val SECTION_NUMBER = "section number"

        @JvmStatic
        fun newInstance(sectionNumber: Int, goalID: Long): MonthStatsPlaceholderFragment {
            return MonthStatsPlaceholderFragment(goalID).apply{
                arguments = Bundle().apply{
                    putInt(SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}
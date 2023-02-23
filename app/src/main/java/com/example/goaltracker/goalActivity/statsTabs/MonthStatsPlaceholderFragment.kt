package com.example.goaltracker.goalActivity.statsTabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.goaltracker.*
import com.example.goaltracker.database.GlobalStatsDatabaseService
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
            setIndex(arguments?.getInt(MonthStatsPlaceholderFragment.SECTION_NUMBER) ?: 1)
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

        val monthTime = roundDouble(dbService.getGoalTimeThisMonth(), HOURS_ROUND_MULTIPLIER)
        val avgExpected = roundDouble(dbService.getGoalExpectedMonthTime(), HOURS_ROUND_MULTIPLIER)
        binding.goalStatsDuration.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            monthTime
        )
        binding.goalStatsAverageToReachGoal.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            if(avgExpected > 0.0) avgExpected else 0.0
        )
        binding.goalStatsProgress.text = if(monthTime != 0.0) roundDouble((monthTime/avgExpected) * 100, PERCENTAGE_ROUND_MULTIPLIER).toString() else "0.0"

        val monthlyAverage = dbService.getGoalAverageMonthlyTime()
        binding.goalStatsAverage.text = String.format(
            requireContext().getString(R.string.hours_placeholder),
            monthlyAverage
        )

        val monthStartCalendar = setCalendarToDayStart(Calendar.getInstance())
        monthStartCalendar.set(Calendar.DAY_OF_MONTH, monthStartCalendar.getActualMinimum(Calendar.DAY_OF_MONTH))
        val monthEndCalendar = setCalendarToDayStart(Calendar.getInstance())
        monthEndCalendar.set(Calendar.DAY_OF_MONTH, monthEndCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))

        val data = SessionDatabaseService(requireContext()).getDailyDurationList(monthStartCalendar.timeInMillis, monthEndCalendar.timeInMillis, goalID)

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
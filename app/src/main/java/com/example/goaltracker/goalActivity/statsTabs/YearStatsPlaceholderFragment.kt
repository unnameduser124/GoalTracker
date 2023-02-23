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

class YearStatsPlaceholderFragment(val goalID: Long): Fragment() {

    private lateinit var pageViewModel:  GoalActivityPageViewModel
    private lateinit var binding: GoalStatsTabBinding

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
        binding = GoalStatsTabBinding.inflate(layoutInflater)

        updateViews()

        return binding.root
    }

     fun updateViews() {

         val dbService = GoalStatsDatabaseService(requireContext(), goalID)

         val yearTime = roundDouble(dbService.getGoalTimeThisYear(), HOURS_ROUND_MULTIPLIER)
         val yearTimeValue = doubleHoursToHoursAndMinutes(yearTime)
         val avgExpected = roundDouble(dbService.getGoalExpectedYearTime(), HOURS_ROUND_MULTIPLIER)
         binding.goalStatsDuration.text = String.format(
             requireContext().getString(R.string.hours_and_minutes_placeholder),
             yearTimeValue.first,
             yearTimeValue.second
         )

         val avgExpectedValue =  if (avgExpected > 0.0) doubleHoursToHoursAndMinutes(avgExpected) else Pair(0,0)
         binding.goalStatsAverageToReachGoal.text = String.format(
             requireContext().getString(R.string.hours_and_minutes_placeholder),
             avgExpectedValue.first,
             avgExpectedValue.second
         )

         binding.goalStatsProgress.text = if (yearTime != 0.0) roundDouble(
             (yearTime / avgExpected) * 100,
             PERCENTAGE_ROUND_MULTIPLIER
         ).toString() else "0.0"

         val yearAverage = doubleHoursToHoursAndMinutes(dbService.getGoalAverageYearlyTime())
         binding.goalStatsAverage.text = String.format(
             requireContext().getString(R.string.hours_and_minutes_placeholder),
             yearAverage.first,
             yearAverage.second
         )


         val yearStartCalendar = setCalendarToDayStart(Calendar.getInstance())
         yearStartCalendar.set(
             Calendar.DAY_OF_YEAR,
             yearStartCalendar.getActualMinimum(Calendar.DAY_OF_YEAR)
         )
         val yearEndCalendar = setCalendarToDayStart(Calendar.getInstance())
         yearEndCalendar.set(
             Calendar.DAY_OF_YEAR,
             yearEndCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)
         )

         val data = SessionDatabaseService(requireContext()).getDailyDurationList(
             yearStartCalendar.timeInMillis,
             yearEndCalendar.timeInMillis,
             goalID
         )
         val weeklyData = getWeeklyAverageList(data)
         weeklyData.forEach {
             println(it.second)
         }

         makeLineChart(
             binding.goalStatsTimeChart,
             weeklyData,
             requireContext(),
             DurationPeriod.ThisYear
         )
    }

    private fun getWeeklyAverageList(data: List<Pair<Calendar, Double>>): MutableList<Pair<Calendar, Double>>{
        val weeksList = mutableListOf<Pair<Calendar, Double>>()

        data.forEach {pair ->
            val week = pair.first.get(Calendar.WEEK_OF_YEAR)
            val existingWeek = weeksList.find { it.first.get(Calendar.WEEK_OF_YEAR) == week }
            if(existingWeek == null){
                weeksList.add(pair)
            }
            else{
                val newPair = existingWeek.copy(second = existingWeek.second+pair.second)
                weeksList[weeksList.indexOf(existingWeek)] = newPair
            }
        }

        return weeksList
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
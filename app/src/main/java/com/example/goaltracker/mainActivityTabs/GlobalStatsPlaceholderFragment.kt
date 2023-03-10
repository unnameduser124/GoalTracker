package com.example.goaltracker.mainActivityTabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.goaltracker.*
import com.example.goaltracker.database.GlobalStatsDatabaseService
import com.example.goaltracker.database.SessionDatabaseService
import com.example.goaltracker.database.TimeGoalDatabaseService
import com.example.goaltracker.databinding.GlobalStatisticsTabBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.util.*

class GlobalStatsPlaceholderFragment: Fragment() {
    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = GlobalStatisticsTabBinding.inflate(layoutInflater)

        val dbService = GlobalStatsDatabaseService(requireContext())

        setUpViews(binding, dbService)
        val startDate = setCalendarToDayStart(Calendar.getInstance())
        startDate.add(Calendar.DAY_OF_YEAR, -30)
        val dailyDurationDataset  = SessionDatabaseService(requireContext()).getDailyDurationList(
            startDate.timeInMillis,
            setCalendarToDayStart(Calendar.getInstance()).timeInMillis
        )
        makeBarChart(
            binding.globalStatsTimeChart,
            dailyDurationDataset,
            requireContext(),
            DurationPeriod.Month
        )

        //show toast message on daily duration value touched, disabled because I don't really like it or find it useful
        /*binding.globalStatsTimeChart.setOnChartValueSelectedListener(object: OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if(e?.y != null){
                    val hoursAndMinutes = doubleHoursToHoursAndMinutes(e.y.toDouble())
                    val timeString = String.format(
                        requireContext().getString(R.string.hours_and_minutes_placeholder),
                        hoursAndMinutes.first,
                        hoursAndMinutes.second
                    )
                    Toast.makeText(context, timeString, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected() {
            }
        })*/

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

    private fun setUpViews(binding: GlobalStatisticsTabBinding, dbService: GlobalStatsDatabaseService){

        val totalTime = roundDouble(dbService.getTotalTime(), HOURS_ROUND_MULTIPLIER)
        setTextViewText(binding.globalStatsTotalTime, doubleHoursToHoursAndMinutes(totalTime))

        val mostTimeInDay = roundDouble(dbService.getMostTimeInDay(), HOURS_ROUND_MULTIPLIER)
        setTextViewText(binding.globalStatsMostTimeInDay, doubleHoursToHoursAndMinutes(mostTimeInDay))

        var goalID = dbService.getGoalWithMostTime()
        binding.globalStatsGoalWithMostTime.text = goalName(goalID)

        goalID = dbService.getLongestActiveGoal()
        binding.globalStatsLongestGoal.text = goalName(goalID)

        val averageTimePerGoal = roundDouble(dbService.getAverageTimePerGoal(), HOURS_ROUND_MULTIPLIER)
        setTextViewText(binding.globalStatsAvgTimePerGoal, doubleHoursToHoursAndMinutes(averageTimePerGoal))

        val averageTimePerDay = roundDouble(dbService.getAverageTimePerDay(), HOURS_ROUND_MULTIPLIER)
        setTextViewText(binding.globalStatsAvgTimePerDay, doubleHoursToHoursAndMinutes(averageTimePerDay))

        val averageTimePerWeek = roundDouble(dbService.getAverageTimePerWeek(), HOURS_ROUND_MULTIPLIER)
        setTextViewText(binding.globalStatsAvgTimePerWeek, doubleHoursToHoursAndMinutes(averageTimePerWeek))

        val averageTimePerMonth = roundDouble(dbService.getAverageTimePerMonth(), HOURS_ROUND_MULTIPLIER)
        setTextViewText(binding.globalStatsAvgTimePerMonth, doubleHoursToHoursAndMinutes(averageTimePerMonth))

        val averageTimePerYear = roundDouble(dbService.getAverageTimePerYear(), HOURS_ROUND_MULTIPLIER)
        setTextViewText(binding.globalStatsAvgTimePerYear, doubleHoursToHoursAndMinutes(averageTimePerYear))

        val timeWithinLastWeek = roundDouble(dbService.getTimeWithinLastWeek(), HOURS_ROUND_MULTIPLIER)
        setTextViewText(binding.globalStatsTimeWithinLastWeek, doubleHoursToHoursAndMinutes(timeWithinLastWeek))

        val timeThisMonth = roundDouble(dbService.getTimeThisMonth(), HOURS_ROUND_MULTIPLIER)
        setTextViewText(binding.globalStatsTimeThisMonth, doubleHoursToHoursAndMinutes(timeThisMonth))

        val timeThisYear = roundDouble(dbService.getTimeThisYear(), HOURS_ROUND_MULTIPLIER)
        setTextViewText(binding.globalStatsTimeThisYear, doubleHoursToHoursAndMinutes(timeThisYear))
    }


    private fun goalName(goalID: Long): String{
        val dbService = TimeGoalDatabaseService(requireContext())
        val goal = dbService.getGoalByID(goalID)
        return if(goal?.goalName!=null){
            goal.goalName
        } else{
            "no data"
        }
    }

    private fun setTextViewText(textView: TextView, hourMinutePair: Pair<Int, Int>){
        textView.text = String.format(
            requireContext().getString(R.string.hours_and_minutes_placeholder),
            hourMinutePair.first,
            hourMinutePair.second
        )
    }
}
package com.example.goaltracker.goalActivity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.DatePicker
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.allViews
import androidx.viewpager.widget.ViewPager
import com.example.goaltracker.*
import com.example.goaltracker.database.SessionDatabaseService
import com.example.goaltracker.database.TimeGoalDatabaseService
import com.example.goaltracker.databinding.AddSessionPopupBinding
import com.example.goaltracker.databinding.GoalActivityBinding
import com.example.goaltracker.goal.GoalSession
import com.example.goaltracker.goal.TimeGoal
import com.example.goaltracker.goal.getTimeDebt
import com.example.goaltracker.goalActivity.statsTabs.DayStatsPlaceholderFragment
import com.example.goaltracker.goalActivity.statsTabs.MonthStatsPlaceholderFragment
import com.example.goaltracker.goalActivity.statsTabs.SectionAdapterGoalActivity
import com.example.goaltracker.goalActivity.statsTabs.YearStatsPlaceholderFragment
import com.example.goaltracker.sessionList.GoalSessionListActivity
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import kotlin.math.floor
import java.util.*

class GoalActivity: AppCompatActivity() {
    private lateinit var binding: GoalActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GoalActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val goalID = intent.getLongExtra("GOAL_ID", 0)
        val dbService = TimeGoalDatabaseService(this)
        val dataGoal = dbService.getGoalByID(goalID)

        lateinit var goal: TimeGoal
        val sessionDatabaseService = SessionDatabaseService(this)
        if(dataGoal!=null){
            goal = TimeGoal(dataGoal)
            goal.goalSessions = sessionDatabaseService.getSessionsForGoal(goal.ID)
        }
        else{
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
            return
        }

        binding.appbarGoalName.text = goal.name
        binding.goalTimeAmount.text = String.format(getString(R.string.goal_time_amount_placeholder), roundDouble(goal.goalTimeAmount, HOURS_ROUND_MULTIPLIER))
        binding.currentTimeAmount.text = String.format(getString(R.string.goal_time_amount_placeholder), roundDouble(goal.getCurrentTimeAmount(this), HOURS_ROUND_MULTIPLIER))
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
        binding.goalStartTime.text = simpleDateFormat.format(goal.startTime.time)
        binding.goalDeadline.text = simpleDateFormat.format(goal.deadline.time)

        setUpViewPager(goalID)

        var timeLeftInSeconds = (setCalendarToDayEnd(goal.deadline).timeInMillis - Calendar.getInstance().timeInMillis)/1000
        val daysLeft = floor(timeLeftInSeconds / SECONDS_IN_DAY).toLong()
        timeLeftInSeconds -= (daysLeft * SECONDS_IN_DAY).toLong()
        val hoursLeft = floor(timeLeftInSeconds/ SECONDS_IN_HOUR).toLong()
        timeLeftInSeconds -= (hoursLeft * SECONDS_IN_HOUR).toLong()
        val minutesLeft = floor(timeLeftInSeconds/ SECONDS_IN_MINUTE).toLong() + 1
        binding.goalTimeLeft.text = String.format(getString(R.string.goal_time_left_placeholder), daysLeft, hoursLeft, minutesLeft)

        binding.goalPercentageCompleted.text = String.format(
            getString(R.string.goal_percentage_completed_placeholder),
            roundDouble((goal.getCurrentTimeAmount(this)/goal.goalTimeAmount)*100, PERCENTAGE_ROUND_MULTIPLIER)
        )

        binding.goalTimeDebt.text = String.format(getString(R.string.hours_placeholder), roundDouble(getTimeDebt(goal, this), HOURS_ROUND_MULTIPLIER))

        binding.addSessionButton.setOnClickListener{
            val popupBinding = AddSessionPopupBinding.inflate(layoutInflater)

            val width = WRAP_CONTENT
            val height = WRAP_CONTENT

            val popupWindow = PopupWindow(popupBinding.root, width, height, true)
            popupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)
            popupWindow.contentView = popupBinding.root

            popupBinding.sessionConfirmButton.setOnClickListener {
                if(popupBinding.valueInput.text.toString()!="" 
                    && popupBinding.valueInput.text.toString().toDouble()!=0.0){
                    val time = popupBinding.valueInput.text.toString().toDouble()
                    val date = setCalendarToDayStart(calendarFromDatePicker(popupBinding.sessionDatePicker))
                    val session = GoalSession(-1, date, time, goal.ID)
                    sessionDatabaseService.addSession(session)
                    updateViews(goal)
                }
                else{
                    Toast.makeText(this, "Duration cannot be 0!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.sessionsButton.setOnClickListener {
            val intent = Intent(this, GoalSessionListActivity::class.java)
            intent.putExtra("GOAL_ID", goal.ID)
            finish()
            startActivity(intent)
        }

        binding.editButton.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("GOAL_ID", goal.ID)
            finish()
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(binding.sessionsButton.context, MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
        })

    }


    private fun calendarFromDatePicker(datePicker: DatePicker): Calendar {
        val calendar = Calendar.getInstance()
        val year = datePicker.year
        val month = datePicker.month
        val day = datePicker.dayOfMonth
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        return calendar
    }

    private fun updateViews(goal: TimeGoal){
        binding.currentTimeAmount.text = String.format(getString(R.string.goal_time_amount_placeholder), roundDouble(goal.getCurrentTimeAmount(this), HOURS_ROUND_MULTIPLIER))
        var timeLeftInSeconds = (goal.deadline.timeInMillis - Calendar.getInstance().timeInMillis)/1000
        val daysLeft = floor(timeLeftInSeconds / SECONDS_IN_DAY).toLong()
        timeLeftInSeconds -= (daysLeft * com.example.goaltracker.SECONDS_IN_DAY).toLong()
        val hoursLeft = floor(timeLeftInSeconds/ SECONDS_IN_HOUR).toLong()
        timeLeftInSeconds -= (hoursLeft * com.example.goaltracker.SECONDS_IN_HOUR).toLong()
        val minutesLeft = floor(timeLeftInSeconds/ SECONDS_IN_MINUTE).toLong() + 1
        binding.goalTimeLeft.text = String.format(getString(R.string.goal_time_left_placeholder), daysLeft, hoursLeft, minutesLeft)

        binding.goalPercentageCompleted.text = String.format(
            getString(R.string.goal_percentage_completed_placeholder),
            roundDouble((goal.getCurrentTimeAmount(this)/goal.goalTimeAmount)*100, PERCENTAGE_ROUND_MULTIPLIER)
        )

        binding.goalTimeDebt.text = String.format(getString(R.string.hours_placeholder), roundDouble(getTimeDebt(goal, this), HOURS_ROUND_MULTIPLIER))

        try{
            val dayStatsTab = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + 0) as DayStatsPlaceholderFragment
            dayStatsTab.updateViews()
        }
        catch (error: java.lang.NullPointerException){
            println(error.message)
        }
        try{
            val monthStatsTab = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + 1) as MonthStatsPlaceholderFragment
            monthStatsTab.updateViews()
        }
        catch (error: java.lang.NullPointerException){
            println(error.message)
        }
        try{
            val yearStatsTab = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + 2) as YearStatsPlaceholderFragment
            yearStatsTab.updateViews()
        }
        catch (error: java.lang.NullPointerException){
            println(error.message)
        }
    }

    private fun setUpViewPager(goalID: Long){
        val sectionPageAdapter = SectionAdapterGoalActivity(supportFragmentManager, goalID)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionPageAdapter
        val tabs: TabLayout = binding.goalStatsTabLayout
        tabs.setupWithViewPager(viewPager)

    }



}
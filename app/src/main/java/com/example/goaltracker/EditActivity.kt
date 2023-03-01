package com.example.goaltracker

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.goaltracker.database.TimeGoalDatabaseService
import com.example.goaltracker.databinding.ConfirmPopupBinding
import com.example.goaltracker.databinding.GoalEditLayoutBinding
import com.example.goaltracker.goal.TimeGoal
import com.example.goaltracker.goalActivity.GoalActivity
import java.util.Calendar

class EditActivity: AppCompatActivity() {
    private lateinit var binding: GoalEditLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GoalEditLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val goalID = intent.getLongExtra("GOAL_ID", 0)
        val dataGoal = TimeGoalDatabaseService(this).getGoalByID(goalID)
        val goal: TimeGoal
        if(dataGoal!=null){
            goal = TimeGoal(dataGoal)
        }
        else{
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
            return
        }

        updateViews(goal)

        binding.editGoalButton.setOnClickListener {
            if(checkFieldInput()){
                val updatedGoal = createGoalFromFields()

                val dbService = TimeGoalDatabaseService(this)
                dbService.updateGoalByID(goalID, updatedGoal)
                val intent = Intent(this, GoalActivity::class.java)
                intent.putExtra("GOAL_ID", goalID)
                finish()
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Fill in the fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.deleteGoalButton.setOnClickListener {
            val popupBinding = ConfirmPopupBinding.inflate(layoutInflater)

            val popupWindow = createPopupWindow(popupBinding.root)
            popupWindow.showAtLocation(binding.deleteGoalButton, Gravity.CENTER, 0, 0)

            popupBinding.confirmPopupYesButton.setOnClickListener {
                val dbService = TimeGoalDatabaseService(this)
                dbService.deleteGoalByID(goalID)
                val intent = Intent(this, MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }

            popupBinding.confirmPopupNoButton.setOnClickListener{
                popupWindow.dismiss()
            }

        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val intent = Intent(this@EditActivity, GoalActivity::class.java)
                intent.putExtra("GOAL_ID", goalID)
                finish()
                startActivity(intent)
            }
        })

    }

    private fun updateViews(goal: TimeGoal){
        binding.appbarTextView.text = String.format(
            getString(R.string.edit_goal_appbar_label),
            goal.name
        )

        binding.editGoalNameInput.setText(goal.name)

        val goalTimeAmountHoursAndMinutes = doubleHoursToHoursAndMinutes(goal.goalTimeAmount)
        prepareNumberPicker(binding.editGoalHourPicker, 0, 100000, false, goalTimeAmountHoursAndMinutes.first)
        prepareNumberPicker(binding.editGoalMinutePicker, 0, 59, true, goalTimeAmountHoursAndMinutes.second)

        setDatePickerFromCalendar(goal.startTime, binding.editGoalStartDate)
        setDatePickerFromCalendar(goal.deadline, binding.editGoalDeadlineDate)

        setDeadlineMinDate()
        binding.editGoalDeadlineDate.setOnDateChangedListener { _, _, _, _ ->
            setDeadlineMinDate()
        }
    }

    private fun setDatePickerFromCalendar(calendar: Calendar, datePicker: DatePicker){
        datePicker.init(calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null)
    }

    private fun createGoalFromFields(): TimeGoal{
        val name = binding.editGoalNameInput.text.toString()
        val timeAmount = binding.editGoalHourPicker.value.toDouble() + binding.editGoalMinutePicker.value.toDouble()/60.0
        var startTime = com.example.goaltracker.calendarFromDatePicker(binding.editGoalStartDate)
        startTime = setCalendarToDayStart(startTime)
        var deadline = com.example.goaltracker.calendarFromDatePicker(binding.editGoalDeadlineDate)
        deadline = setCalendarToDayEnd(deadline)

        return TimeGoal(-1, name, timeAmount, startTime, deadline)
    }

    private fun checkFieldInput(): Boolean{
        return if(binding.editGoalNameInput.text.toString() == "" || binding.editGoalNameInput.text.toString() == " "){
            false
        }
        else {
            binding.editGoalHourPicker.value.toDouble() + binding.editGoalMinutePicker.value.toDouble()/60.0 != 0.0
        }
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


    private fun setDeadlineMinDate(){
        val minDateCalendar = calendarFromDatePicker(binding.editGoalStartDate)
        binding.editGoalDeadlineDate.minDate = minDateCalendar.timeInMillis
    }
}
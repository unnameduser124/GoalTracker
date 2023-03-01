package com.example.goaltracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goaltracker.database.TimeGoalDatabaseService
import com.example.goaltracker.databinding.GoalAddLayoutBinding
import com.example.goaltracker.goal.TimeGoal

class AddActivity: AppCompatActivity() {
    private lateinit var binding: GoalAddLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GoalAddLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setDeadlineMinDate()

        binding.addGoalStartDate.setOnDateChangedListener { _, _, _, _ ->
            setDeadlineMinDate()
        }

        prepareNumberPicker(binding.addGoalHourPicker, 0, 100000, false, 0)
        prepareNumberPicker(binding.addGoalMinutePicker, 0, 59, true, 0)

        binding.addGoalButton.setOnClickListener {
            if(checkFieldInput()){
                val goal = createGoalFromFields()
                val dbService = TimeGoalDatabaseService(this)
                dbService.addGoal(goal)
                val intent = Intent(this, MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Fill in the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createGoalFromFields(): TimeGoal{
        val name = binding.addGoalNameInput.text.toString()
        val timeAmount = binding.addGoalHourPicker.value.toDouble() + binding.addGoalMinutePicker.value.toDouble()/60.0
        var startTime = calendarFromDatePicker(binding.addGoalStartDate)
        startTime = setCalendarToDayStart(startTime)
        var deadline = calendarFromDatePicker(binding.addGoalDeadlineDate)
        deadline = setCalendarToDayEnd(deadline)

        return TimeGoal(-1, name, timeAmount, startTime, deadline)
    }
    
    private fun checkFieldInput(): Boolean{
        return if(binding.addGoalNameInput.text.toString() == "" || binding.addGoalNameInput.text.toString() == " "){
            false
        }
        else {
            binding.addGoalHourPicker.value.toDouble() + binding.addGoalMinutePicker.value.toDouble()/60.0 != 0.0
        }
    }

    private fun setDeadlineMinDate(){
        val minDateCalendar = calendarFromDatePicker(binding.addGoalStartDate)
        binding.addGoalDeadlineDate.minDate = minDateCalendar.timeInMillis
    }
}
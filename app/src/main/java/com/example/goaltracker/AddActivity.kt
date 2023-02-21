package com.example.goaltracker

import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goaltracker.database.TimeGoalDatabaseService
import com.example.goaltracker.databinding.TimeGoalAddLayoutBinding
import com.example.goaltracker.goal.TimeGoal
import java.util.*

class AddActivity: AppCompatActivity() {
    private lateinit var binding: TimeGoalAddLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TimeGoalAddLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setDeadlineMinDate()

        binding.addGoalStartDate.setOnDateChangedListener { _, _, _, _ ->
            setDeadlineMinDate()
        }

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
        val timeAmount = binding.addGoalTimeAmountInput.text.toString().toDouble()
        var startTime = calendarFromDatePicker(binding.addGoalStartDate)
        startTime = setCalendarToDayStart(startTime)
        var deadline = calendarFromDatePicker(binding.addGoalDeadlineDate)
        deadline = setCalendarToDayStart(deadline)

        return TimeGoal(-1, name, timeAmount, startTime, deadline)
    }
    
    private fun checkFieldInput(): Boolean{
        return if(binding.addGoalNameInput.text.toString() == "" || binding.addGoalNameInput.text.toString() == " "){
            false
        }
        else binding.addGoalTimeAmountInput.text.toString() != " " && binding.addGoalTimeAmountInput.text.toString() != "" && binding.addGoalTimeAmountInput.text.toString().toDouble() != 0.0
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
        val minDateCalendar = calendarFromDatePicker(binding.addGoalStartDate)
        binding.addGoalDeadlineDate.minDate = minDateCalendar.timeInMillis
    }
}
package com.example.goaltracker

import android.app.ActionBar.LayoutParams
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.DatePicker
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.goaltracker.database.TimeGoalDatabaseService
import com.example.goaltracker.databinding.ConfirmPopupBinding
import com.example.goaltracker.databinding.TimeGoalEditLayoutBinding
import com.example.goaltracker.goal.TimeGoal
import com.example.goaltracker.goalActivity.GoalActivity
import java.util.Calendar

class EditActivity: AppCompatActivity() {
    private lateinit var binding: TimeGoalEditLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TimeGoalEditLayoutBinding.inflate(layoutInflater)
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

        binding.appbarTextView.text = String.format(
            getString(R.string.edit_goal_appbar_label),
            goal.name
        )

        binding.editGoalNameInput.setText(goal.name)
        binding.editGoalTimeAmountInput.setText(goal.goalTimeAmount.toString())
        setDatePickerFromCalendar(goal.startTime, binding.editGoalStartDate)
        setDatePickerFromCalendar(goal.deadline, binding.editGoalDeadlineDate)

        setDeadlineMinDate()
        binding.editGoalDeadlineDate.setOnDateChangedListener { _, _, _, _ ->
            setDeadlineMinDate()
        }

        binding.editGoalButton.setOnClickListener {
            if(checkFieldInput()){
                val newName = binding.editGoalNameInput.text.toString()
                val newTimeAmount = binding.editGoalTimeAmountInput.text.toString()

                val newStartDate = calendarFromDatePicker(binding.editGoalStartDate)
                val newDeadlineDate = calendarFromDatePicker(binding.editGoalDeadlineDate)

                goal.apply {
                    name = newName
                    goalTimeAmount = newTimeAmount.toDouble()
                    startTime = newStartDate
                    deadline = newDeadlineDate
                }

                val dbService = TimeGoalDatabaseService(this)
                dbService.updateGoalByID(goalID, goal)
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
            val width = LayoutParams.WRAP_CONTENT
            val height = LayoutParams.WRAP_CONTENT
            val focusable = true

            val popupWindow = PopupWindow(popupBinding.root, width, height, focusable)
            popupWindow.showAtLocation(binding.deleteGoalButton, Gravity.CENTER, 0, 0)
            popupWindow.contentView = popupBinding.root

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

    }

    private fun setDatePickerFromCalendar(calendar: Calendar, datePicker: DatePicker){
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null)
    }

    private fun checkFieldInput(): Boolean{
        return if(binding.editGoalNameInput.text.toString() == "" || binding.editGoalNameInput.text.toString() == " "){
            false
        }
        else binding.editGoalTimeAmountInput.text.toString() != " " && binding.editGoalTimeAmountInput.text.toString() != "" && binding.editGoalTimeAmountInput.text.toString().toDouble() != 0.0
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
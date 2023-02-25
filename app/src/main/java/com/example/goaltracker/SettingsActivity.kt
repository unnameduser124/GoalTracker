package com.example.goaltracker

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Constraints
import com.example.goaltracker.database.GoalDatabase
import com.example.goaltracker.databinding.ConfirmPopupBinding
import com.example.goaltracker.databinding.SettingsLayoutBinding

class SettingsActivity: AppCompatActivity() {

    private lateinit var binding: SettingsLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = SettingsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clearDataButton.setOnClickListener {
            val popupBinding = ConfirmPopupBinding.inflate(layoutInflater)
            val width = Constraints.LayoutParams.WRAP_CONTENT
            val height = Constraints.LayoutParams.WRAP_CONTENT
            val focusable = true

            val popupWindow = PopupWindow(popupBinding.root, width, height, focusable)

            popupWindow.showAtLocation(binding.clearDataButton, Gravity.CENTER, 0, 0)
            popupWindow.contentView = popupBinding.root

            popupBinding.confirmPopupYesButton.setOnClickListener {
                val dbService = GoalDatabase(this)
                dbService.clearData()
                popupWindow.dismiss()
            }

            popupBinding.confirmPopupNoButton.setOnClickListener {
                popupWindow.dismiss()
            }
        }

        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(binding.clearDataButton.context, MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
        })
    }


}
package com.example.goaltracker

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
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

            val popupWindow = createPopupWindow(popupBinding.root)

            popupWindow.showAtLocation(binding.clearDataButton, Gravity.CENTER, 0, 0)

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
                val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
        })
    }


}
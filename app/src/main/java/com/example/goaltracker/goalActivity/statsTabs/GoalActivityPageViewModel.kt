package com.example.goaltracker.goalActivity.statsTabs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GoalActivityPageViewModel: ViewModel() {
    private val index = MutableLiveData<Int>()

    fun setIndex(newValue: Int){
        index.value = newValue
    }
}
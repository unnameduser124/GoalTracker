package com.example.goaltracker.mainActivityTabs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PageViewModel: ViewModel() {
    private val index = MutableLiveData<Int>()

    fun setIndex(newValue: Int){
        index.value = newValue
    }
}

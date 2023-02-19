package com.example.goaltracker.goalActivity.statsTabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.goaltracker.mainActivityTabs.GlobalStatsPlaceholderFragment
import com.example.goaltracker.mainActivityTabs.HomePlaceholderFragment

private val TAB_TITLES = arrayOf(
    "Day",
    "Month",
    "Year"
)

class SectionAdapterGoalActivity(fragmentManager: FragmentManager, val goalID: Long): FragmentPagerAdapter(fragmentManager){
    override fun getItem(position: Int): Fragment {
        return if(position==0){
            DayStatsPlaceholderFragment.newInstance(position, goalID)
        } else if (position==1){
            MonthStatsPlaceholderFragment.newInstance(position, goalID)
        }
        else{
            YearStatsPlaceholderFragment.newInstance(position, goalID)
        }
    }

    override fun getCount() = TAB_TITLES.size

    override fun getPageTitle(position: Int): CharSequence {
        return TAB_TITLES[position]
    }

}

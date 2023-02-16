package com.example.goaltracker.mainActivityTabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

private val TAB_TITLES = arrayOf(
    "Home",
    "Stats"
)

class SectionAdapterMain(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager){
    override fun getItem(position: Int): Fragment {
        return if(position==0){
            HomePlaceholderFragment.newInstance(position)
        } else{
            GlobalStatsPlaceholderFragment.newInstance(position)
        }
    }

    override fun getCount() = TAB_TITLES.size

    override fun getPageTitle(position: Int): CharSequence {
        return TAB_TITLES[position]
    }

}
package com.example.goaltracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.goaltracker.databinding.ActivityMainBinding
import com.example.goaltracker.mainActivityTabs.SectionAdapterMain
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionPageAdapter= SectionAdapterMain(supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionPageAdapter
        val tabs: TabLayout =findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

    }
}
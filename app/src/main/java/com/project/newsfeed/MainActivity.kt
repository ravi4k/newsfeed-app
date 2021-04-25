package com.project.newsfeed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import com.project.newsfeed.databinding.ActivityMainBinding
import com.project.newsfeed.ui.main.SectionsPagerAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        val viewPager = activityMainBinding.viewPager
        viewPager.adapter = sectionsPagerAdapter

        val tabs = activityMainBinding.tabs
        tabs.setupWithViewPager(viewPager)
    }
}
package com.project.newsfeed

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import com.project.newsfeed.databinding.ActivityMainBinding
import com.project.newsfeed.ui.main.SectionsPagerAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(activityMainBinding.titleBar)

        setupViewPager()
        setContentView(activityMainBinding.root)
    }

    private fun setupViewPager() {
        val viewPager = activityMainBinding.viewPager
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewPager.adapter = sectionsPagerAdapter

        val tabs = activityMainBinding.tabs
        tabs.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        else if(item.itemId == R.id.about) {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        return true
    }
}
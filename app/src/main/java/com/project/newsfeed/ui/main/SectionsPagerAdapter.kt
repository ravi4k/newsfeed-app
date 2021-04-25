package com.project.newsfeed.ui.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.project.newsfeed.R

class SectionsPagerAdapter(mContext: Context, fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {

    private val tabTitles: Array<String> = mContext.resources.getStringArray(R.array.tab_titles)
    private val paths: Array<String> = mContext.resources.getStringArray(R.array.paths)
    private val noPages = 3

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putInt("position", position)
        bundle.putString("path", paths[position])
        return NewsFragment.newInstance(bundle)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitles[position]
    }

    override fun getCount(): Int {
        return noPages
    }

}
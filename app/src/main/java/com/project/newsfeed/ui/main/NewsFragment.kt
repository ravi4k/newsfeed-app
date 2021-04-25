package com.project.newsfeed.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.newsfeed.databinding.FragmentMainBinding

class NewsFragment : Fragment() {

    private val newsListAdapter: NewsListAdapter = NewsListAdapter(ArrayList())
    private lateinit var fragmentMainBinding: FragmentMainBinding
    private val viewModel: NewsFragmentViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        fragmentMainBinding = FragmentMainBinding.inflate(inflater, container, false)
        return fragmentMainBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = fragmentMainBinding.newsRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = newsListAdapter

        fragmentMainBinding.swipeToRefresh.setOnRefreshListener {
            updateNews()
            fragmentMainBinding.swipeToRefresh.isRefreshing = false
        }

        viewModel.newsItems.observe(viewLifecycleOwner, { newsItems ->
            newsListAdapter.clear()
            fragmentMainBinding.loadingSpinner.visibility = View.GONE
            if(newsItems.isNotEmpty()) {
                fragmentMainBinding.noDataText.visibility = View.GONE
                newsListAdapter.updateDataSet(newsItems)
            }
            else
                fragmentMainBinding.noDataText.visibility = View.VISIBLE
        })

        updateNews()
    }

    private fun updateNews() {
        fragmentMainBinding.loadingSpinner.visibility = View.VISIBLE
        viewModel.loadNews(arguments?.getString("path"))
    }

    companion object {
        fun newInstance(bundle: Bundle): NewsFragment {
            val fragment = NewsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
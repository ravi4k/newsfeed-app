package com.project.newsfeed.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.newsfeed.databinding.FragmentMainBinding
import kotlinx.coroutines.launch

class NewsFragment : Fragment() {

    private val newsListAdapter: NewsListAdapter = NewsListAdapter(ArrayList())
    private lateinit var fragmentMainBinding: FragmentMainBinding
    private lateinit var viewModel: NewsFragmentViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {

        viewModel = ViewModelProvider(this, NewsViewModelFactory(arguments?.getString("path"))).get(NewsFragmentViewModel::class.java)
        fragmentMainBinding = FragmentMainBinding.inflate(inflater, container, false)
        return fragmentMainBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = fragmentMainBinding.newsRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = newsListAdapter

        fragmentMainBinding.swipeToRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                updateNews()
                fragmentMainBinding.swipeToRefresh.isRefreshing = false
            }
        }

        viewModel.newsItems.observe(viewLifecycleOwner, { newsItems ->
            if(newsItems.isNotEmpty()) {
                fragmentMainBinding.noDataText.visibility = View.GONE
                newsListAdapter.clear()
                newsListAdapter.updateDataSet(newsItems)
            }
            else
                fragmentMainBinding.noDataText.visibility = View.VISIBLE
        })
    }

    suspend private fun updateNews() {
        newsListAdapter.clear()
        viewModel.loadNews()
    }

    companion object {
        fun newInstance(bundle: Bundle): NewsFragment {
            val fragment = NewsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
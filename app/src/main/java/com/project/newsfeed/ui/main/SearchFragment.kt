package com.project.newsfeed.ui.main

import SearchFragmentViewModel
import SearchViewModelFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.newsfeed.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private val newsListAdapter: NewsListAdapter = NewsListAdapter(ArrayList())
    private lateinit var fragmentSearchBinding: FragmentSearchBinding
    private lateinit var viewModel: SearchFragmentViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        viewModel = ViewModelProvider(this, SearchViewModelFactory("search")).get(SearchFragmentViewModel::class.java)
        fragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return fragmentSearchBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = fragmentSearchBinding.newsRecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = newsListAdapter

        fragmentSearchBinding.searchBar.isSubmitButtonEnabled = true
        fragmentSearchBinding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                updateNews(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.isNullOrEmpty())
                    newsListAdapter.clear()
                return true
            }
        })

        viewModel.newsItems.observe(viewLifecycleOwner, { newsItems ->
            newsListAdapter.clear()
            newsListAdapter.updateDataSet(newsItems)
        })
    }

    private fun updateNews(query: String?) {
        newsListAdapter.clear()
        viewModel.loadNews(query)
    }
}
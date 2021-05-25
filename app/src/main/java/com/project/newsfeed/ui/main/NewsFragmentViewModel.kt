package com.project.newsfeed.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.newsfeed.data.NewsModel
import com.project.newsfeed.utils.NewsQueryUtils
import kotlinx.coroutines.launch

class NewsViewModelFactory(private val path: String?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsFragmentViewModel(path) as T
    }
}

class NewsFragmentViewModel(private val path : String?) : ViewModel() {

    val newsItems = MutableLiveData<ArrayList<NewsModel>>()

    init {
        viewModelScope.launch {
            loadNews()
        }
    }

    suspend fun loadNews() {
        newsItems.postValue(NewsQueryUtils.fetchNewsDataFromPath(path))
    }
}
package com.project.newsfeed.ui.main

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.newsfeed.data.NewsModel
import com.project.newsfeed.utils.NewsQueryUtils
import kotlinx.coroutines.launch

class NewsFragmentViewModel : ViewModel() {

    val newsItems = MutableLiveData<ArrayList<NewsModel>>()

    fun loadNews(path: String?) {
        viewModelScope.launch {
            val uriBuilder = Uri.parse(GUARDIAN_API_REQUEST_URL).buildUpon()
            uriBuilder.appendPath(path)
            uriBuilder.appendQueryParameter("show-fields", "thumbnail")
            uriBuilder.appendQueryParameter("page-size", "20")
            uriBuilder.appendQueryParameter("show-tags", "contributor")
            uriBuilder.appendQueryParameter("api-key", GUARDIAN_API_KEY)
            newsItems.postValue(NewsQueryUtils.fetchNewsData(uriBuilder.toString()))
        }
    }

    companion object {
        private const val GUARDIAN_API_REQUEST_URL: String = "https://content.guardianapis.com/"
        private const val GUARDIAN_API_KEY: String = "42b74d66-8cdf-453f-a2c5-32ec213f21f3"
    }
}
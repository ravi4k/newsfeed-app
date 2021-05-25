import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.project.newsfeed.data.NewsModel
import com.project.newsfeed.utils.NewsQueryUtils
import kotlinx.coroutines.launch

class SearchViewModelFactory(private val path: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchFragmentViewModel(path) as T
    }
}

class SearchFragmentViewModel(private val path : String) : ViewModel() {

    val newsItems = MutableLiveData<ArrayList<NewsModel>>()

    init {
        viewModelScope.launch {
            newsItems.postValue(NewsQueryUtils.fetchNewsDataFromPath("world"))
        }
    }

    fun loadNews(query: String?) = viewModelScope.launch {
        newsItems.postValue(NewsQueryUtils.fetchNewsDataFromPath(path, query))
    }
}
package com.project.newsfeed.utils

import android.util.Log
import com.project.newsfeed.data.NewsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.ParseException

object NewsQueryUtils {
    private val LOG_TAG: String = NewsQueryUtils::class.java.simpleName

    suspend fun fetchNewsData(requestUrl: String): ArrayList<NewsModel> = withContext(Dispatchers.IO) {
            val url = createUrl(requestUrl)
            var jsonResponse = ""
            try {
                jsonResponse = makeHttpRequest(url)
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Problem making HTTP request.", e)
            }
            extractDataFromJson(jsonResponse)
        }

    private fun createUrl(urlString: String): URL? {
        var url: URL? = null
        try {
            url = URL(urlString)
        } catch (e: MalformedURLException) {
            Log.e(LOG_TAG, "Problem building the URL.", e)
        }
        return url
    }

    private fun makeHttpRequest(url: URL?): String {
        var jsonResponse = ""
        if (url == null)
            return jsonResponse

        var urlConnection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        try {
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.readTimeout = 10000
            urlConnection.connectTimeout = 15000
            urlConnection.requestMethod = "GET"
            urlConnection.connect()
            if (urlConnection.responseCode == 200) {
                inputStream = urlConnection.inputStream
                jsonResponse = readFromStream(inputStream)
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.responseCode)
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e)
        } finally {
            urlConnection?.disconnect()
            inputStream?.close()
        }
        return jsonResponse
    }

    private fun readFromStream(inputStream: InputStream?): String {
        val output = StringBuilder()
        if (inputStream != null) {
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var line = bufferedReader.readLine()
            while (line != null) {
                output.append(line)
                line = bufferedReader.readLine()
            }
        }
        return output.toString()
    }

    private fun extractDataFromJson(newsResponseJson: String): ArrayList<NewsModel> {
        val newsItems = ArrayList<NewsModel>()
        if (newsResponseJson.isEmpty())
            return newsItems

        try {
            val data = JSONObject(newsResponseJson).getJSONObject("response").getJSONArray("results")
            for(i in 0 until data.length()) {
                val currNews = data[i] as JSONObject
                val title = currNews.getString("webTitle")
                var author = ""
                if(currNews.getJSONArray("tags").length() != 0) {
                    author = (currNews.getJSONArray("tags")[0] as JSONObject).getString("webTitle")
                }
                val webUrl = currNews.getString("webUrl")
                val thumbUrl = currNews.getJSONObject("fields").getString("thumbnail")
                val date = currNews.getString("webPublicationDate")
                newsItems.add(NewsModel(title, author, webUrl, thumbUrl, date))
            }
        } catch (e: JSONException) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results.", e)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return newsItems
    }
}
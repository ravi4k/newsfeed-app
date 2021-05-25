package com.project.newsfeed.utils

import android.net.Uri
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
import java.text.DateFormat
import java.text.MessageFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object NewsQueryUtils {
    private object API {
        const val GUARDIAN_API_REQUEST_URL: String = "https://content.guardianapis.com/"
        const val GUARDIAN_API_KEY: String = "42b74d66-8cdf-453f-a2c5-32ec213f21f3"
    }

    private val LOG_TAG: String = NewsQueryUtils::class.java.simpleName

    suspend fun fetchNewsDataFromPath(path: String?, query: String? = null): ArrayList<NewsModel> = withContext(Dispatchers.IO) {
        val uriBuilder = Uri.parse(API.GUARDIAN_API_REQUEST_URL).buildUpon()
        uriBuilder.appendPath(path)
        if(query != null)
            uriBuilder.appendQueryParameter("q", query)
        uriBuilder.appendQueryParameter("show-fields", "thumbnail")
        uriBuilder.appendQueryParameter("page-size", "20")
        uriBuilder.appendQueryParameter("show-tags", "contributor")
        uriBuilder.appendQueryParameter("api-key", API.GUARDIAN_API_KEY)

        val url = createUrl(uriBuilder.toString())
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
                val section = currNews.getString("sectionName")
                val title = currNews.getString("webTitle")
                var author = ""
                if(currNews.getJSONArray("tags").length() != 0) {
                    author = (currNews.getJSONArray("tags")[0] as JSONObject).getString("webTitle")
                }
                val webUrl = currNews.getString("webUrl")
                val thumbUrl = currNews.getJSONObject("fields").getString("thumbnail")

                var date = currNews.getString("webPublicationDate")
                date = formatDate(date)

                newsItems.add(NewsModel(section, title, author, webUrl, thumbUrl, date))
            }
        } catch (e: JSONException) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results.", e)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return newsItems
    }

    private fun formatDate(date: String) : String {
        var gmtDate = ""
        if(date.endsWith("Z"))
            gmtDate = date.substring(0, date.indexOf("Z"))

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")

        var parsedDate: Date? = null
        try{
            parsedDate = dateFormat.parse(gmtDate)
        } catch(e : ParseException) {
            e.printStackTrace()
        }

        val parsedDateStr = DateFormat.getDateInstance(DateFormat.MEDIUM).format(parsedDate)
        val parsedTimeStr = DateFormat.getTimeInstance(DateFormat.SHORT).format(parsedDate)

        return MessageFormat.format("on {0} at {1}", parsedDateStr, parsedTimeStr)
    }
}
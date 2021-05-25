package com.project.newsfeed.ui.main

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.newsfeed.R
import com.project.newsfeed.data.NewsModel
import com.project.newsfeed.databinding.NewsArticleItemBinding
import com.project.newsfeed.ui.main.NewsListAdapter.NewsViewHolder
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NewsListAdapter(private val newsItems: ArrayList<NewsModel>) : RecyclerView.Adapter<NewsViewHolder?>() {

    inner class NewsViewHolder(private val itemBinding: NewsArticleItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bindTo(newsModel: NewsModel) {
            itemBinding.newsSectionText.text = newsModel.mSection
            itemBinding.newsTitleText.text = newsModel.mTitle
            itemBinding.authorText.text = newsModel.mAuthorName
            itemBinding.dateText.text = newsModel.mPublicationDate
            Picasso.get()
                    .load(newsModel.mThumbnail)
                    .placeholder(R.drawable.default_thumbnail)
                    .into(itemBinding.thumbnailImage)
            itemBinding.newsTextLayout.setOnClickListener { v: View ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsModel.mWebURL))
                v.context.startActivity(intent)
            }
            itemBinding.shareBtn.setOnClickListener { v: View ->
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, newsModel.mWebURL)
                v.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemNewsBinding = NewsArticleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(itemNewsBinding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bindTo(newsItems[position])
    }

    override fun getItemCount(): Int = newsItems.size

    override fun getItemId(position: Int): Long = position.toLong()

    fun updateDataSet(data: ArrayList<NewsModel>) {
        newsItems.addAll(data)
        notifyDataSetChanged()
    }

    fun clear() {
        newsItems.clear()
        notifyDataSetChanged()
    }
}
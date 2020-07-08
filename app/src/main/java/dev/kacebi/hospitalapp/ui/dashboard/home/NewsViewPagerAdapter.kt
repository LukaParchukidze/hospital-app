package dev.kacebi.hospitalapp.ui.dashboard.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.ui.dashboard.home.NewsModel
import kotlinx.android.synthetic.main.item_news_layout.view.*

class NewsViewPagerAdapter(private val news: MutableList<NewsModel>) : PagerAdapter() {

    override fun getCount() = news.size

    override fun isViewFromObject(view: View, itemView: Any): Boolean {
        return view == itemView
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(container.context)
            .inflate(R.layout.item_news_layout, container, false)
        val model = news[position]

        Glide.with(itemView.context)
            .load(model.drawable)
            .into(itemView!!.newsImageView)

        itemView.newsTitleTextView.text = model.title
        itemView.newsDescriptionTextView.text = model.description

        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, itemView: Any) {
        container.removeView(itemView as View)
    }
}
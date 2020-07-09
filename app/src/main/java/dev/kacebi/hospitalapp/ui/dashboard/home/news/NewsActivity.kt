package dev.kacebi.hospitalapp.ui.dashboard.home.news

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.extensions.toggleVisibility
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NewsActivity : AppCompatActivity() {

    private var news: NewsModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar!!.title = "Hospital News"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        init()
    }

    private fun init() {

//        loader.toggleVisibility()

        val position = intent.getIntExtra("position", 0)
        d("position", "$position")

        CoroutineScope(Dispatchers.IO).launch {
            news = App.dbNews.document("news${position + 1}").get().await().toObject(
                NewsModel::class.java
            )
//            val byteArray = App.storage.child(news!!.image_uri).getBytes(1024 * 1024L).await()
//            val bitmapDrawable = BitmapDrawable(
//                resources,
//                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//            )
//            news!!.drawable = bitmapDrawable


            withContext(Dispatchers.Main) {
//                Glide.with(this@NewsActivity)
//                    .load(news!!.drawable)
//                    .into(newsImageView)

                newsTitleTextView.text = news!!.title
                newsDescriptionTextView.text = news!!.description.replace("\\n", "\n")
                newsAuthorTextView.append("Author: ${news!!.author}")
                newsDateTextView.append("${news!!.date} ")

                loader.visibility = View.GONE
                newsScrollView.visibility = View.VISIBLE
            }
        }
    }
}

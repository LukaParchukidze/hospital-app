package dev.kacebi.hospitalapp.ui.patients_dashboard.home.news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.view.View
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.tools.Tools
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.toolbar_layout.*
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

        init()
        setUpToolbar()
    }

    private fun init() {

//        loader.toggleVisibility()

        val position = intent.getIntExtra("position", 0)
        d("position", "$position")

        CoroutineScope(Dispatchers.IO).launch {
            news = App.dbNews.document("news${position + 1}").get().await().toObject(
                NewsModel::class.java
            )
//            val byteArray = App.storage.child(news!!.image_uri).getBytes(FileSizeConstants.THREE_MEGABYTES).await()
//            val bitmap = Utils.byteArrayToBitmap(byteArray)
//            news!!.bitmap = bitmap


            withContext(Dispatchers.Main) {
//                Glide.with(this@NewsActivity)
//                    .load(news!!.bitmap)
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

    private fun setUpToolbar(){
        Tools.setSupportActionBar(this,getString(R.string.hospital_news), isLastName = false, backEnabled = true)

        toolbar!!.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}

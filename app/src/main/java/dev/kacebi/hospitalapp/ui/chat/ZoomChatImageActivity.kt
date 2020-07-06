package dev.kacebi.hospitalapp.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.github.chrisbanes.photoview.PhotoViewAttacher
import dev.kacebi.hospitalapp.R
import kotlinx.android.synthetic.main.activity_zoom_chat_image.*

class ZoomChatImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_chat_image)

        //toolbar
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar!!.title = "Dr. Yle"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val adapterPosition = intent.getIntExtra("adapterPosition", 0)
        zoomChatImagePhotoView.setImageDrawable(ChatMessageAdapter.drawableMap[adapterPosition])
    }
}
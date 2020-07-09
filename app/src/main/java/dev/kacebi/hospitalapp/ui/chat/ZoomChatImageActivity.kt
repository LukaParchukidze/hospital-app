package dev.kacebi.hospitalapp.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.tools.Tools
import kotlinx.android.synthetic.main.activity_zoom_chat_image.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class ZoomChatImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_chat_image)

        val adapterPosition = intent.extras!!.getInt("adapterPosition", 0)
        val lastName = intent.extras!!.getString("lastname", "")
        zoomChatImagePhotoView.setImageDrawable(ChatMessageAdapter.drawableMap[adapterPosition])

        Tools.setSupportActionBar(this,lastName)
    }
}
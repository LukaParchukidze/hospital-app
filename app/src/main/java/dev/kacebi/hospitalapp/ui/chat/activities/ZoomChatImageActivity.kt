package dev.kacebi.hospitalapp.ui.chat.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.ui.chat.adapters.ChatMessageAdapter
import kotlinx.android.synthetic.main.activity_zoom_chat_image.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class ZoomChatImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_chat_image)

        val adapterPosition = intent.extras!!.getInt("adapterPosition", 0)
        val name = intent.extras!!.getString("name", "")
        zoomChatImagePhotoView.setImageBitmap(ChatMessageAdapter.bitmapMap[adapterPosition])

        Tools.setSupportActionBar(this, name, isLastName = !name.contains(" "), backEnabled = true)
        toolbar!!.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}
package dev.kacebi.hospitalapp.ui.chat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.ui.authentication.RegisterActivity
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChatActivity : AppCompatActivity() {

    private lateinit var messages: MutableList<ChatMessageModel>
    private lateinit var adapter: ChatMessageAdapter

    private val fromId = FirebaseAuth.getInstance().currentUser!!.uid
    private val toId = "khqI5bCWxyYjJm1iTxgtTop8skA2" //intentidan unda amovigot ID

    private val fromUserMessagesDatabase =
        FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId")
    private val toUserMessagesDatabase =
        FirebaseDatabase.getInstance().getReference("/user_messages/$toId/$fromId")

    private val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setUpToolbar("Dr. Yle")
        setUpAdapter()
        setUpListeners()
    }

    private fun setUpToolbar(title: String) {
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpAdapter() {
        rvChat.layoutManager = LinearLayoutManager(this)
        messages = mutableListOf()

        CoroutineScope(Dispatchers.IO).launch {
            val byteArray = storageRef.child("$toId.png").getBytes(1024 * 1024L).await()
            val toIdProfileImage: Bitmap =
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            adapter = ChatMessageAdapter(messages, fromId, toIdProfileImage, object: ChatImageOnClick {
                override fun onClick(adapterPosition: Int) {
                    val intent = Intent(this@ChatActivity, ZoomChatImageActivity::class.java).apply {
                        putExtra("adapterPosition", adapterPosition)
                    }
                    startActivity(intent)
                }

            })
            withContext(Dispatchers.Main) {
                rvChat.adapter = adapter
            }
        }
    }

    private fun setUpListeners() {
        btnChat.setOnClickListener {
            val message = etChat.text.toString()
            if (message.isNotEmpty()) {
                it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.click))
                val messageModel =
                    ChatMessageModel(fromId = fromId, toId = toId, message = message)
                CoroutineScope(Dispatchers.IO).launch {
                    fromUserMessagesDatabase.push().setValue(messageModel)
                    toUserMessagesDatabase.push().setValue(messageModel)
                }
            }
        }

        btnChatImage.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.click))
            openGallery()
        }

        fromUserMessagesDatabase.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                messages.add(snapshot.getValue(ChatMessageModel::class.java)!!)
                etChat.text.clear()
                adapter.notifyItemInserted(messages.size - 1)
                rvChat.scrollToPosition(messages.size - 1)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                messages.clear()
                adapter.notifyDataSetChanged()
            }

        })
    }

    private fun openGallery() {
        val gallery = Intent();
        gallery.type = "image/*";
        gallery.action = Intent.ACTION_GET_CONTENT;

        startActivityForResult(
            Intent.createChooser(gallery, "Select Picture"),
            RegisterActivity.REQUEST_CODE_GALLERY
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RegisterActivity.REQUEST_CODE_GALLERY && data != null) {
            val imageUri = data.data
            val push = fromUserMessagesDatabase.push()
            val path = "/sent_photos/${push.key}/${imageUri!!.lastPathSegment}"

            CoroutineScope(Dispatchers.IO).launch {
                storageRef.child(path).putFile(imageUri).await()
                push.setValue(ChatMessageModel(fromId = fromId, toId = toId, imageUri = path))
                toUserMessagesDatabase.push()
                    .setValue(ChatMessageModel(fromId = fromId, toId = toId, imageUri = path))
            }

        }
    }

}
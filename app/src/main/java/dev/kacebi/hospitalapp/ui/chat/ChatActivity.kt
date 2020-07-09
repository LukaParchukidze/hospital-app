package dev.kacebi.hospitalapp.ui.chat

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.ui.authentication.RegisterActivity
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class ChatActivity : AppCompatActivity() {

    companion object {
        const val BUFFER_SIZE = 1024 * 1024 * 5L
    }

    private lateinit var lastName: String

    private lateinit var messages: MutableList<ChatMessageModel>
    private lateinit var adapter: ChatMessageAdapter

    private val fromId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var toId: String

    private lateinit var fromUserMessagesDatabase: DatabaseReference
    private lateinit var toUserMessagesDatabase: DatabaseReference

    private val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        toId = intent.extras!!.getString("doctorId")!!
        fromUserMessagesDatabase =
            FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId")
        toUserMessagesDatabase =
            FirebaseDatabase.getInstance().getReference("/user_messages/$toId/$fromId")

        lastName = intent.extras!!.getString("lastName")!!

        Tools.setSupportActionBar(this,lastName)

        setUpAdapter()
        setUpListeners()
    }

    private fun setUpAdapter() {
        rvChat.layoutManager = LinearLayoutManager(this)
        messages = mutableListOf()

        CoroutineScope(Dispatchers.IO).launch {
            val byteArray = storageRef.child("$toId.png").getBytes(1024 * 1024L).await()
            val toIdProfileImage: Bitmap =
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            adapter =
                ChatMessageAdapter(messages, fromId, toIdProfileImage, object : ChatImageOnClick {
                    override fun onClick(adapterPosition: Int) {
                        val intent =
                            Intent(this@ChatActivity, ZoomChatImageActivity::class.java).apply {
                                putExtra("adapterPosition", adapterPosition)
                                putExtra("lastname", lastName)
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
                etChat.text.clear()
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
            val imageUri = data.data!!
            val imageSize = File(imageUri.path!!).length()

            if (imageSize <= BUFFER_SIZE) {
                val push = fromUserMessagesDatabase.push()
                val path = "/sent_photos/${push.key}/${imageUri.lastPathSegment}"
                CoroutineScope(Dispatchers.IO).launch {
                    storageRef.child(path).putFile(imageUri).await()
                    push.setValue(ChatMessageModel(fromId = fromId, toId = toId, imageUri = path))
                    toUserMessagesDatabase.push()
                        .setValue(ChatMessageModel(fromId = fromId, toId = toId, imageUri = path))
                }
            } else {
                Toast.makeText(this, "The file limit is 5 MB", Toast.LENGTH_LONG).show()
            }
        }
    }

}
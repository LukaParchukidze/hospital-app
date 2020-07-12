package dev.kacebi.hospitalapp.ui.chat.activities

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
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.ui.ItemOnClickListener
import dev.kacebi.hospitalapp.ui.authentication.RegisterActivity
import dev.kacebi.hospitalapp.ui.chat.adapters.ChatMessageAdapter
import dev.kacebi.hospitalapp.ui.chat.models.ChatMessageModel
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class ChatActivity : AppCompatActivity() {

    private lateinit var name: String

    private lateinit var messages: MutableList<ChatMessageModel>
    private lateinit var adapter: ChatMessageAdapter

    private val fromId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var toId: String

    private lateinit var fromUserMessagesDatabase: DatabaseReference
    private lateinit var fromLatestMessagesDatabase: DatabaseReference

    private lateinit var toUserMessagesDatabase: DatabaseReference
    private lateinit var toLatestMessagesDatabase: DatabaseReference

    private val storageRef = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        toId = intent.extras!!.getString("id")!!
        fromUserMessagesDatabase =
            FirebaseDatabase.getInstance().getReference("/user_messages/$fromId/$toId")
        fromLatestMessagesDatabase =
            FirebaseDatabase.getInstance().getReference("/latest_messages/$fromId/$toId")

        toUserMessagesDatabase =
            FirebaseDatabase.getInstance().getReference("/user_messages/$toId/$fromId")
        toLatestMessagesDatabase =
            FirebaseDatabase.getInstance().getReference("/latest_messages/$toId/$fromId")

        name = intent.extras!!.getString("name")!!

        Tools.setSupportActionBar(this, name, isLastName = !name.contains(" "), backEnabled = true)

        setUpAdapter()
        toolbar!!.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpAdapter() {
        rvChat.layoutManager = LinearLayoutManager(this)
        messages = mutableListOf()

        CoroutineScope(Dispatchers.IO).launch {
            val byteArray =
                storageRef.child("$toId.png").getBytes(FileSizeConstants.ONE_MEGABYTE).await()
            val toIdProfileImage: Bitmap =
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            withContext(Dispatchers.Main) {
                adapter =
                    ChatMessageAdapter(
                        messages,
                        fromId,
                        toIdProfileImage,
                        object : ItemOnClickListener {
                            override fun onClick(adapterPosition: Int) {
                                val intent =
                                    Intent(
                                        this@ChatActivity,
                                        ZoomChatImageActivity::class.java
                                    ).apply {
                                        putExtra("adapterPosition", adapterPosition)
                                        putExtra("name", name)
                                    }
                                startActivity(intent)
                            }

                        })
                rvChat.adapter = adapter
                setUpListeners()
            }
        }


    }

    private fun setUpListeners() {

        //Send Text Message Listener
        btnChat.setOnClickListener {
            val message = etChat.text.toString()
            if (message.isNotEmpty()) {
                it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.click))
                val messageModel =
                    ChatMessageModel(
                        fromId = fromId,
                        toId = toId,
                        message = message
                    )
                etChat.text.clear()
                CoroutineScope(Dispatchers.IO).launch {
                    fromUserMessagesDatabase.push().setValue(messageModel)
                    fromLatestMessagesDatabase.setValue(messageModel)

                    toUserMessagesDatabase.push().setValue(messageModel)
                    toLatestMessagesDatabase.setValue(messageModel)
                }
            }
        }

        //Send Image Listener
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

            if (imageSize <= FileSizeConstants.FIVE_MEGABYTES) {
                val push = fromUserMessagesDatabase.push()
                val path = "/sent_photos/${push.key}/${imageUri.lastPathSegment}"
                CoroutineScope(Dispatchers.IO).launch {
                    storageRef.child(path).putFile(imageUri).await()
                    val chatMessage = ChatMessageModel(
                        fromId = fromId,
                        toId = toId,
                        imageUri = path
                    )
                    push.setValue(
                        chatMessage
                    )
                    fromLatestMessagesDatabase.setValue(chatMessage)

                    toUserMessagesDatabase.push()
                        .setValue(
                            chatMessage
                        )
                    toLatestMessagesDatabase.setValue(chatMessage)
                }
            } else {
                Toast.makeText(this, "The file limit is 5 MB", Toast.LENGTH_LONG).show()
            }
        }
    }

}
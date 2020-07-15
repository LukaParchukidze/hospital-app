package dev.kacebi.hospitalapp.ui.chat.activities

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import dev.kacebi.hospitalapp.App
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.tools.Tools
import dev.kacebi.hospitalapp.utils.Utils
import dev.kacebi.hospitalapp.ui.chat.adapters.ChatsListAdapter
import dev.kacebi.hospitalapp.ui.chat.models.ChatsListItemModel
import dev.kacebi.hospitalapp.ui.chat.models.LatestMessageModel
import kotlinx.android.synthetic.main.activity_chats_list.*
import kotlinx.android.synthetic.main.spinkit_loader_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChatsListActivity : AppCompatActivity() {

    private lateinit var adapter: ChatsListAdapter
    private val chatsList = mutableListOf<ChatsListItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats_list)

        init()
        Tools.setSupportActionBar(this, "Messages", isLastName = false, backEnabled = true)
        toolbar!!.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun init() {
        chatsListRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ChatsListAdapter(chatsList)
        chatsListRecyclerView.adapter = adapter

        FirebaseDatabase.getInstance().getReference("/latest_messages/${App.auth.uid!!}")
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val id = snapshot.key!!
                    val latestMessage = snapshot.getValue(LatestMessageModel::class.java)!!.message

                    for (i in 0 until chatsList.size) {
                        if (chatsList[i].id == id) {
                            chatsList[i].latestMessage = latestMessage
                            adapter.notifyItemChanged(i)
                            break
                        }
                    }

                }

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val id = snapshot.key!!
                        var name = App.dbUsers.document(id).get().await()["full_name"]
                        var byteArray: ByteArray? = null
                        if (name == null) {
                            name = App.dbDoctors.document(id).get().await()["last_name"] as String
                            byteArray = App.storage.child("/doctor_photos/$id.png")
                                .getBytes(FileSizeConstants.THREE_MEGABYTES).await()
                        } else {
                            name = name as String
                            byteArray = App.storage.child("/patient_photos/$id.png")
                                .getBytes(FileSizeConstants.THREE_MEGABYTES).await()
                        }
                        val bitmap = Utils.byteArrayToBitmap(byteArray)
                        val latestMessage =
                            snapshot.getValue(LatestMessageModel::class.java)!!.message
                        val chat = ChatsListItemModel(
                            id = id,
                            bitmap = bitmap,
                            name = name,
                            latestMessage = latestMessage
                        )
                        chatsList.add(chat)
                        withContext(Dispatchers.Main) {
                            adapter.notifyItemInserted(chatsList.size - 1)
                            spinKitContainerView.visibility = View.GONE
                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

            })
    }
}
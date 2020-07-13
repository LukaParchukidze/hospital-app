package dev.kacebi.hospitalapp.ui.chat.adapters

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.firebase.storage.FirebaseStorage
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.file_size_constants.FileSizeConstants
import dev.kacebi.hospitalapp.ui.ItemOnClickListener
import dev.kacebi.hospitalapp.ui.chat.models.ChatMessageModel
import kotlinx.android.synthetic.main.item_message_from_layout.view.*
import kotlinx.android.synthetic.main.item_message_image_from_layout.view.*
import kotlinx.android.synthetic.main.item_message_image_to_layout.view.*
import kotlinx.android.synthetic.main.item_message_to_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChatMessageAdapter(
    private val messages: MutableList<ChatMessageModel>,
    private val fromId: String,
    private val toIdProfileImage: Bitmap,
    private val itemClick: ItemOnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val storageRef = FirebaseStorage.getInstance().reference


    companion object {
        const val FROM_LAYOUT_MESSAGE = 0
        const val FROM_LAYOUT_IMAGE = 1
        const val TO_LAYOUT_MESSAGE = 2
        const val TO_LAYOUT_IMAGE = 3

        val drawableMap = mutableMapOf<Int, Drawable>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            FROM_LAYOUT_MESSAGE -> {
                return FromMessageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_message_from_layout, parent, false)
                )
            }
            FROM_LAYOUT_IMAGE -> {
                return FromImageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_message_image_from_layout, parent, false)
                )
            }
            TO_LAYOUT_MESSAGE -> {
                return ToMessageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_message_to_layout, parent, false)
                )
            }
            else -> {
                return ToImageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_message_image_to_layout, parent, false)
                )
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FromMessageViewHolder -> holder.onBind()
            is FromImageViewHolder -> holder.onBind()
            is ToMessageViewHolder -> holder.onBind()
            is ToImageViewHolder -> holder.onBind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].fromId == fromId) {
            if (messages[position].imageUri.isEmpty())
                FROM_LAYOUT_MESSAGE
            else
                FROM_LAYOUT_IMAGE
        } else {
            if (messages[position].imageUri.isEmpty())
                TO_LAYOUT_MESSAGE
            else
                TO_LAYOUT_IMAGE
        }
    }

    inner class FromImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var message: ChatMessageModel

        fun onBind() {
            message = messages[adapterPosition]

            if (drawableMap.containsKey(adapterPosition)) {
                setData(drawableMap[adapterPosition]!!)
                return
            }

            itemView.ivChatImageFrom.setImageResource(R.drawable.ic_launcher_background)
            CoroutineScope(Dispatchers.IO).launch {
                val byteArray =
                    storageRef.child(message.imageUri).getBytes(FileSizeConstants.TWO_MEGABYTES)
                        .await()
                val bitmapDrawable = BitmapDrawable(
                    itemView.context.resources,
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                )
                drawableMap[adapterPosition] = bitmapDrawable
                withContext(Dispatchers.Main) {
                    setData(bitmapDrawable)
                }
            }
        }

        private fun setData(drawable: Drawable) {
            itemView.ivChatImageFrom.setImageDrawable(drawable)
            itemView.ivChatImageFrom.setOnClickListener {
                itemClick.onClick(adapterPosition)
            }
            itemView.tvImageDateFrom.text = message.date
        }

    }

    inner class ToImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var message: ChatMessageModel

        fun onBind() {
            message = messages[adapterPosition]


            if (drawableMap.containsKey(adapterPosition)) {
                setData(drawableMap[adapterPosition]!!)
                return
            }

            itemView.ivChatImageTo.setImageResource(R.drawable.ic_launcher_background)
            CoroutineScope(Dispatchers.IO).launch {
                val byteArray =
                    storageRef.child(message.imageUri).getBytes(FileSizeConstants.TWO_MEGABYTES)
                        .await()
                val bitmapDrawable = BitmapDrawable(
                    itemView.context.resources,
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                )
                drawableMap[adapterPosition] = bitmapDrawable
                withContext(Dispatchers.Main) {
                    setData(bitmapDrawable)
                }
            }
        }

        private fun setData(drawable: Drawable) {
            itemView.ivChatImageTo.setImageDrawable(drawable)
            itemView.ivChatImageTo.setOnClickListener {
                itemClick.onClick(adapterPosition)
            }
            itemView.ivImageProfilePicture.setImageBitmap(toIdProfileImage)
            itemView.tvImageDateTo.text = message.date
        }
    }

    inner class FromMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var message: ChatMessageModel

        fun onBind() {
            message = messages[adapterPosition]

            itemView.tvMessageFrom.text = message.message
            itemView.tvDateFrom.text = message.date

        }
    }

    inner class ToMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var message: ChatMessageModel

        fun onBind() {
            message = messages[adapterPosition]

            itemView.tvMessageTo.text = message.message
            itemView.tvDateTo.text = message.date
            itemView.ivProfilePicture.setImageBitmap(toIdProfileImage)

        }
    }
}
package dev.kacebi.hospitalapp.ui.chat.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.kacebi.hospitalapp.R
import dev.kacebi.hospitalapp.ui.chat.activities.ChatActivity
import dev.kacebi.hospitalapp.ui.chat.models.ChatsListItemModel
import kotlinx.android.synthetic.main.item_chats_list_layout.view.*

class ChatsListAdapter(private val chats: MutableList<ChatsListItemModel>) :
    RecyclerView.Adapter<ChatsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chats_list_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var chat: ChatsListItemModel

        fun onBind() {
            chat = chats[adapterPosition]

            itemView.profileImageView.setImageDrawable(chat.drawable)
            if (chat.name.contains(" "))
                itemView.nameTextView.text = chat.name
            else
                itemView.nameTextView.text = "Dr. ${chat.name}"
            if (chat.latestMessage.isEmpty())
                itemView.latestMessageTextView.text = itemView.context.getString(R.string.photo)
            else
                itemView.latestMessageTextView.text = chat.latestMessage

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ChatActivity::class.java).apply {
                    putExtra("id", chat.id)
                    putExtra("name", chat.name)
                }
                itemView.context.startActivity(intent)
            }
        }
    }
}
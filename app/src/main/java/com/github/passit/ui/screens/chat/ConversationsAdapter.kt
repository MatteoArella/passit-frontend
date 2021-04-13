package com.github.passit.ui.screens.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.passit.R
import com.github.passit.databinding.ConversationRowItemBinding
import com.github.passit.ui.models.conversations.ConversationView
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

class ConversationsAdapter @Inject constructor(private val onClick: (ConversationView) -> Unit): PagingDataAdapter<ConversationView, ConversationsAdapter.ViewHolder>(ConversationDiffCallback) {
    class ViewHolder(itemView: View, val onClick: (ConversationView) -> Unit): RecyclerView.ViewHolder(itemView) {
        val binding = ConversationRowItemBinding.bind(itemView)

        fun bind(conversationView: ConversationView) {
            conversationView.updatedAt?.let { binding.conversationUpdateDate.text = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(it) }
            binding.conversationName.text = itemView.context.getString(
                    R.string.profile_username,
                    conversationView.associated?.givenName?.capitalize(Locale.getDefault()),
                    conversationView.associated?.familyName?.capitalize(Locale.getDefault())
            )
            Picasso.get().load(conversationView.associated?.picture?.toURI().toString()).placeholder(R.drawable.ic_person).into(binding.userPicture)
            conversationView.unread?.let {
                binding.unreadMessages.text = "$it"
                binding.unreadMessages.visibility = View.VISIBLE
            }
            itemView.setOnClickListener {
                onClick(conversationView)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.conversation_row_item, viewGroup, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        getItem(position)?.let { viewHolder.bind(it) }
    }
}

object ConversationDiffCallback : DiffUtil.ItemCallback<ConversationView>() {
    override fun areItemsTheSame(oldItem: ConversationView, newItem: ConversationView): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ConversationView, newItem: ConversationView): Boolean {
        return oldItem.id == newItem.id
    }
}
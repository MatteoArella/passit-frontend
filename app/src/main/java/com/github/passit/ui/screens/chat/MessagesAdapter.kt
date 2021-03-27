package com.github.passit.ui.screens.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.passit.R
import com.github.passit.databinding.MessageSeparatorRowItemBinding
import com.github.passit.databinding.ReceivedMessageRowItemBinding
import com.github.passit.databinding.SentMessageRowItemBinding
import com.github.passit.ui.models.conversations.MessageView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MessagesAdapter @Inject constructor(): PagingDataAdapter<MessageView, MessagesAdapter.ViewHolder>(MessageDiffCallback) {

    sealed class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        class ReceivedMessageHolder(itemView: View): ViewHolder(itemView) {
            val binding = ReceivedMessageRowItemBinding.bind(itemView)
            override fun bind(messageView: MessageView) {
                val message = (messageView as MessageView.Message.ReceivedMessage).message
                message.createdAt?.let {
                    val df = SimpleDateFormat("HH:mm", Locale.getDefault())
                    binding.messageTimestamp.text = df.format(it)
                }
                binding.messageContent.text = message.content
            }
            companion object {
                fun create(parent: ViewGroup): ReceivedMessageHolder {
                    val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.received_message_row_item, parent, false)
                    return ReceivedMessageHolder(view)
                }
            }
        }
        class SentMessageHolder(itemView: View): ViewHolder(itemView) {
            val binding = SentMessageRowItemBinding.bind(itemView)
            override fun bind(messageView: MessageView) {
                val message = (messageView as MessageView.Message.SentMessage).message
                message.createdAt?.let {
                    val df = SimpleDateFormat("HH:mm", Locale.getDefault())
                    binding.messageTimestamp.text = df.format(it)
                }
                binding.messageContent.text = message.content
            }
            companion object {
                fun create(parent: ViewGroup): SentMessageHolder {
                    val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.sent_message_row_item, parent, false)
                    return SentMessageHolder(view)
                }
            }
        }
        class SeparatorHolder(itemView: View): ViewHolder(itemView) {
            val binding = MessageSeparatorRowItemBinding.bind(itemView)
            override fun bind(messageView: MessageView) {
                val separator = messageView as MessageView.SeparatorItem
                binding.messageDateGroup.text = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(separator.date)
            }
            companion object {
                fun create(parent: ViewGroup): SeparatorHolder {
                    val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.message_separator_row_item, parent, false)
                    return SeparatorHolder(view)
                }
            }
        }

        abstract fun bind(messageView: MessageView)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MessageView.Message.ReceivedMessage -> R.layout.received_message_row_item
            is MessageView.Message.SentMessage -> R.layout.sent_message_row_item
            is MessageView.SeparatorItem -> R.layout.message_separator_row_item
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            R.layout.received_message_row_item -> ViewHolder.ReceivedMessageHolder.create(parent)
            R.layout.sent_message_row_item -> ViewHolder.SentMessageHolder.create(parent)
            else -> ViewHolder.SeparatorHolder.create(parent)
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        getItem(position)?.let { viewHolder.bind(it) }
    }
}

object MessageDiffCallback : DiffUtil.ItemCallback<MessageView>() {
    override fun areItemsTheSame(oldItem: MessageView, newItem: MessageView): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MessageView, newItem: MessageView): Boolean {
        return (oldItem is MessageView.Message && newItem is MessageView.Message && oldItem.message.id == newItem.message.id) ||
                (oldItem is MessageView.SeparatorItem && newItem is MessageView.SeparatorItem && oldItem.date == newItem.date)
    }
}
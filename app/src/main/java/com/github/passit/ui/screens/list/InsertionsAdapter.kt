package com.github.passit.ui.screens.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.passit.R
import com.github.passit.databinding.InsertionRowItemBinding
import com.github.passit.ui.models.insertions.InsertionView
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

class InsertionsAdapter @Inject constructor(private val onClick: (InsertionView) -> Unit): PagingDataAdapter<InsertionView, InsertionsAdapter.ViewHolder>(InsertionDiffCallback) {
    class ViewHolder(itemView: View, val onClick: (InsertionView) -> Unit): RecyclerView.ViewHolder(itemView) {
        val binding = InsertionRowItemBinding.bind(itemView)

        fun bind(insertionView: InsertionView) {
            binding.insertionTitle.text = insertionView.title
            binding.insertionSubject.text = insertionView.subject
            insertionView.createdAt?.let { binding.insertionDate.text = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(it) }
            binding.insertionTutor.text = itemView.context.getString(
                    R.string.profile_username,
                    insertionView.tutor?.givenName?.capitalize(Locale.getDefault()),
                    insertionView.tutor?.familyName?.capitalize(Locale.getDefault())
            )
            Picasso.get().load(insertionView.tutor?.picture?.toURI().toString()).placeholder(R.drawable.ic_person).into(binding.tutorPicture)
            itemView.setOnClickListener {
                onClick(insertionView)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.insertion_row_item, viewGroup, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        getItem(position)?.let { viewHolder.bind(it) }
    }
}

object InsertionDiffCallback : DiffUtil.ItemCallback<InsertionView>() {
    override fun areItemsTheSame(oldItem: InsertionView, newItem: InsertionView): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: InsertionView, newItem: InsertionView): Boolean {
        return oldItem.id == newItem.id
    }
}
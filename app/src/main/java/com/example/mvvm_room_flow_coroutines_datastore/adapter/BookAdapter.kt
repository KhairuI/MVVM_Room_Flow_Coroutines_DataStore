package com.example.mvvm_room_flow_coroutines_datastore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm_room_flow_coroutines_datastore.databinding.RowBookBinding
import com.example.mvvm_room_flow_coroutines_datastore.model.ModelBook

class BookAdapter(private val listener:OnItemClickListener): ListAdapter<ModelBook,BookAdapter.BookViewHolder>(DiffCallBack()) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(RowBookBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
      val book= getItem(position)
        holder.binding.apply {
            txtName.text= book.name
            txtName.paint.isStrikeThruText= book.completed
            checkComplete.isChecked= book.completed
            imgBookmark.isVisible= book.important

            rowLayout.setOnClickListener {
                listener.onItemClick(book,book.important)
            }
            checkComplete.setOnClickListener {
                listener.onCheckBoxClick(book,checkComplete.isChecked)
            }
        }

    }

    class BookViewHolder(val binding: RowBookBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener{
        fun onItemClick(book:ModelBook,isImportant:Boolean)
        fun onCheckBoxClick(book:ModelBook,isChecked:Boolean)
    }

    class DiffCallBack: DiffUtil.ItemCallback<ModelBook>(){
        override fun areItemsTheSame(oldItem: ModelBook, newItem: ModelBook) = oldItem.id== newItem.id
        override fun areContentsTheSame(oldItem: ModelBook, newItem: ModelBook) = oldItem == newItem

    }
}
package com.pandey.shubham.ocrdemoapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pandey.shubham.ocrdemoapp.databinding.ItemCollectionViewBinding

/**
 * Created by shubhampandey
 */
class CollectionAdapter : RecyclerView.Adapter<CollectionAdapter.CollectionItemAViewHolder>() {

    private val itemList = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionItemAViewHolder {
        val binding = ItemCollectionViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionItemAViewHolder(binding)
    }

    override fun getItemCount() = itemList.count()

    override fun onBindViewHolder(holder: CollectionItemAViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    fun updateItems(newList: List<String>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyItemRangeChanged(0, itemList.size)
    }

    class CollectionItemAViewHolder(private val binding: ItemCollectionViewBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String) {
            binding.tvTitle.text = title
        }
    }
}
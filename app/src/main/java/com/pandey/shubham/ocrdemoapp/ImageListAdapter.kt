package com.pandey.shubham.ocrdemoapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pandey.shubham.ocrdemoapp.databinding.ItemImageViewBinding

/**
 * Created by shubhampandey
 */
class ImageListAdapter(private val itemList: List<Uri>, private val callback: ImageAdapterCallback): RecyclerView.Adapter<ImageItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemViewHolder {
        val binding = ItemImageViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageItemViewHolder(binding)
    }

    override fun getItemCount() = itemList.count()

    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
        holder.itemView.setOnClickListener { callback.onImageClicked(itemList[position]) }
        holder.bindData(itemList[position])
    }
}
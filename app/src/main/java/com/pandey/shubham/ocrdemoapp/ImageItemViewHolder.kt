package com.pandey.shubham.ocrdemoapp

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.pandey.shubham.ocrdemoapp.databinding.ItemImageViewBinding

/**
 * Created by shubhampandey
 */
class ImageItemViewHolder(private val binding: ItemImageViewBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindData(imageUri: Uri) {
        binding.ivImage.setImageURI(imageUri)
    }
}
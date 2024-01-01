package com.pandey.shubham.ocrdemoapp

import android.net.Uri

/**
 * Created by shubhampandey
 */
interface ImageAdapterCallback {

    fun onImageClicked(imageUri: Uri)
}
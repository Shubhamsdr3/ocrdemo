package com.pandey.shubham.ocrdemoapp.callbacks

import com.pandey.shubham.data.ImageInfo

/**
 * Created by shubhampandey
 */
interface ImageDetailCallback {
    fun onShowDetailClicked(imageInfo: ImageInfo?)
}
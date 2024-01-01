package com.pandey.shubham.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by shubhampandey
 */

@Parcelize
data class ImageInfo(
    val collections: List<String>?,
    val description: String?
): Parcelable
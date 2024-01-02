package com.pandey.shubham.ocrdemoapp.viewstate

import android.media.Image
import android.net.Uri
import com.pandey.shubham.data.ImageInfo

/**
 * Created by shubhampandey
 */
sealed class ImageListViewState {

    object ShowLoader : ImageListViewState()

    object HideLoader: ImageListViewState()

    data class ShowImage(val imagePaths: List<Uri>): ImageListViewState()

    data class ShowError(val error: Throwable?): ImageListViewState()
    data class UpdateCollection(val newCollection: List<String>):  ImageListViewState()

    data class ShowDetailScreen(val imageInfo: ImageInfo): ImageListViewState()

}

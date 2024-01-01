package com.pandey.shubham.ocrdemoapp

/**
 * Created by shubhampandey
 */
sealed class ImageListViewState {

    data class UpdateCollection(val newCollection: List<String>):  ImageListViewState()

}

package com.pandey.shubham.ocrdemoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

/**
 * Created by shubhampandey
 */
class ImageListViewModelFactory: ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageListViewModel::class.java)) {
            return ImageListViewModel() as T
        } else {
            throw IllegalArgumentException("Wrong type of viewmodel")
        }
    }
}
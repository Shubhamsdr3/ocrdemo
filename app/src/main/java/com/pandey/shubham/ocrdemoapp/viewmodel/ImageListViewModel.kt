package com.pandey.shubham.ocrdemoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pandey.shubham.ocrdemoapp.ImageListViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by shubhampandey
 */
class ImageListViewModel: ViewModel() {

    private val _collectionsMutableLiveData = MutableLiveData<ImageListViewState>()

    val collectionLiveData: LiveData<ImageListViewState> = _collectionsMutableLiveData

    fun addCollections(newCollections: List<String>) {
        
        _collectionsMutableLiveData.value = ImageListViewState.UpdateCollection(newCollections)
    }

    fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = flow {
        var lastEmissionTime = 0L
        collect { upstream ->
            val currentTime = System.currentTimeMillis()
            val mayEmit = currentTime - lastEmissionTime > windowDuration
            if (mayEmit)
            {
                lastEmissionTime = currentTime
                emit(upstream)
            }
        }
    }
}
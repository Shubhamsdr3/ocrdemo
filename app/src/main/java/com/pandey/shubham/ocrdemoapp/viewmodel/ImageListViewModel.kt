package com.pandey.shubham.ocrdemoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.pandey.shubham.data.ImageInfo
import com.pandey.shubham.ocrdemoapp.viewstate.ImageListViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * Created by shubhampandey
 */
class ImageListViewModel: ViewModel() {

    private val _collectionsMutableLiveData = MutableLiveData<ImageListViewState>()

    val collectionLiveData: LiveData<ImageListViewState> = _collectionsMutableLiveData

    fun addCollections(newCollections: List<String>) {
        _collectionsMutableLiveData.value = ImageListViewState.UpdateCollection(newCollections)
    }

    fun onImageProcess(result: FirebaseVisionText?) {
        viewModelScope.launch {
            val paragraph = StringBuffer()
            processResultText(result?.text)
            if (result?.textBlocks.isNullOrEmpty()) {
                for (block in result?.textBlocks!!) {
                    for (line in block.lines) {
                        val lineText = line.text
                        paragraph.append(lineText)
                    }
                }
            }
            val imageInfo = ImageInfo(
                collections = processResultText(result?.text),
                description = paragraph.toString()
            )
            _collectionsMutableLiveData.value = ImageListViewState.ShowDetailScreen(imageInfo)
        }
    }

    private fun processResultText(text: String?): List<String> {
        val collections = ArrayList<String>(10)
        if (text.isNullOrEmpty()) return collections
        val stringArray = text.split('\n')
        stringArray.forEach {
            collections.add(it)
        }
        return collections
    }

    fun showLoader() {
        _collectionsMutableLiveData.value = ImageListViewState.ShowLoader
    }

    fun hideLoader() {
        _collectionsMutableLiveData.value = ImageListViewState.HideLoader
    }
    fun showError(error: Throwable?) {
        _collectionsMutableLiveData.value = ImageListViewState.ShowError(error)
    }
}
package com.pandey.shubham.ocrdemoapp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.pandey.shubham.data.ImageInfo
import com.pandey.shubham.ocrdemoapp.callbacks.ImageAdapterCallback
import com.pandey.shubham.ocrdemoapp.callbacks.ImageDetailCallback
import com.pandey.shubham.ocrdemoapp.databinding.FragmentImageListBinding
import com.pandey.shubham.ocrdemoapp.viewmodel.ImageListViewModel
import com.pandey.shubham.ocrdemoapp.viewmodel.ImageListViewModelFactory
import com.pandey.shubham.ocrdemoapp.viewstate.ImageListViewState


/**
 * Created by shubhampandey
 */
class ImageListFragment: Fragment(), ImageAdapterCallback, ImageDetailCallback {

    private lateinit var binding: FragmentImageListBinding

    private val ORIENTATIONS = SparseIntArray()

    private lateinit var viewModel: ImageListViewModel

    private lateinit var callback: ImageDetailCallback

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.getFromSdcard()
            } else {
                Toast.makeText(context, "Please provide the permission", Toast.LENGTH_SHORT).show()
            }
        }

    companion object {

        const val TAG = "ImageListFragment"

        fun newInstance() = ImageListFragment()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment != null && parentFragment is ImageDetailCallback) {
            this.callback = parentFragment as ImageDetailCallback
        } else if (context is ImageDetailCallback) {
            this.callback = context
        } else {
            throw IllegalStateException("$context must implement fragment")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentImageListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(viewModelStore, ImageListViewModelFactory())[ImageListViewModel::class.java]
        viewModel.collectionLiveData.observe(viewLifecycleOwner) {state -> onViewStateChanged(state) }
        if (isPermissionGranted()) {
            viewModel.getFromSdcard()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun onViewStateChanged(state: ImageListViewState?) {
        when(state) {
            is ImageListViewState.ShowLoader -> showLoaderDialog()
            is ImageListViewState.HideLoader -> hideLoader()
            is ImageListViewState.ShowError -> showError(state.error)
            is ImageListViewState.ShowDetailScreen -> showDetailScreen(state.imageInfo)
            is ImageListViewState.ShowImage -> showImages(state.imagePaths)
            else -> {
                // do nothing
            }
        }
    }

    private fun showImages(imagePaths: List<Uri>) {
        binding.rvImage.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = ImageListAdapter(imagePaths, this@ImageListFragment)
        }
        onImageClicked(imagePaths[0])
    }

    private fun showDetailScreen(imageInfo: ImageInfo) {
        openDetailBottomSheet(imageInfo)
    }

    private fun showError(error: Throwable?) {
        binding.ivLoader.visibility = View.GONE
    }

    private fun hideLoader() {
        binding.ivLoader.visibility = View.GONE
    }

    private fun showLoaderDialog() {
        binding.ivLoader.visibility = View.VISIBLE
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun onImageClicked(imageUri: Uri) {
        viewModel.showLoader()
        binding.ivPreview.setImageURI(imageUri)
        val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
        val mediaImage = FirebaseVisionImage.fromBitmap(bitmap)
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(mediaImage)
            .addOnSuccessListener { result ->
                viewModel.hideLoader()
                viewModel.onImageProcess(result)
            }
            .addOnFailureListener { error ->
                viewModel.hideLoader()
                viewModel.showError(error)
            }
    }

    private fun openDetailBottomSheet(imageInfo: ImageInfo) {
        ImageDescriptionBottomSheet.newInstance(imageInfo).show(childFragmentManager, ImageDescriptionBottomSheet.TAG)
    }

    override fun onShowDetailClicked(imageInfo: ImageInfo?) {
        callback.onShowDetailClicked(imageInfo)
    }
}
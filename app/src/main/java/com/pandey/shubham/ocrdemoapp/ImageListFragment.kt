package com.pandey.shubham.ocrdemoapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.pandey.shubham.data.ImageInfo
import com.pandey.shubham.ocrdemoapp.databinding.FragmentImageListBinding


/**
 * Created by shubhampandey
 */
class ImageListFragment: Fragment(), ImageAdapterCallback, ImageDetailCallback , EditBottomSheetCallback {

    private lateinit var binding: FragmentImageListBinding

    private val imageList = mutableListOf<Uri>()

    private val ORIENTATIONS = SparseIntArray()

    private val collectionList = mutableListOf<String>()

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }

    private val chosePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (null != result.data) {
                if (null != result.data?.clipData) {
                    val count = result.data?.clipData?.itemCount ?: 0
                    for (i in 0 until Math.min(count, 10)) {
                        val uri = result.data?.clipData?.getItemAt(i)?.uri
                        uri?.let { imageList.add(it) }
                    }
                } else {
                    val uri = result?.data?.data
                    uri?.let { imageList.add(it) }
                }
            }
            setAdapter()
        }
    };

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(context, "Please provide the permission", Toast.LENGTH_SHORT).show()
            }
        }

    companion object {

        const val TAG = "ImageListFragment"

        fun newInstance() = ImageListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentImageListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btOpenGallery.setOnClickListener {
            if (isPermissionGranted()) {
                openGallery()
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun setAdapter() {
        binding.rvImage.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = ImageListAdapter(imageList, this@ImageListFragment)
        }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            action = Intent.ACTION_OPEN_DOCUMENT
            addCategory(Intent.CATEGORY_OPENABLE)
        }.also {
            chosePictureLauncher.launch(it)
        }
    }

    private fun scanFile(path: String) {
        MediaScannerConnection.scanFile(requireContext(), arrayOf(path), null) { path, uri -> run {
                Log.i("TAG", "Finished scanning $path")
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun onImageClicked(imageUri: Uri) {
        binding.ivPreview.setImageURI(imageUri)
        val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
        val mediaImage = FirebaseVisionImage.fromBitmap(bitmap)
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(mediaImage)
            .addOnSuccessListener { result ->
                val resultText = result.text
                openDetailBottomSheet(result)
            }
            .addOnFailureListener { e ->
                print(e)
            }
    }

    private fun openDetailBottomSheet(result: FirebaseVisionText?) {
        val imageInfo = ImageInfo(
            collections = listOf("Animal", "Rabbit", "Elephant", "Horse"),
            description = "This is description.."
        )
        ImageDescriptionBottomSheet.newInstance(imageInfo).show(childFragmentManager, ImageDescriptionBottomSheet.TAG)
    }

    override fun onEditClicked(collection: List<String>?) {
        EditCollectionBottomSheet.newInstance(collection).show(childFragmentManager, EditCollectionBottomSheet.TAG)
    }

    override fun onCollectionSelected(collections: List<String>) {
        collectionList.addAll(collections)
    }
}
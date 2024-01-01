package com.pandey.shubham.ocrdemoapp

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pandey.shubham.data.ImageInfo
import com.pandey.shubham.ocrdemoapp.databinding.BottomSheetImageDescriptionBinding
import com.pandey.shubham.ocrdemoapp.viewmodel.ImageListViewModel
import com.pandey.shubham.ocrdemoapp.viewmodel.ImageListViewModelFactory

/**
 * Created by shubhampandey
 */
class ImageDescriptionBottomSheet: BottomSheetDialogFragment(), EditBottomSheetCallback {

    private lateinit var binding: BottomSheetImageDescriptionBinding

    private val imageInfo: ImageInfo? by lazy { arguments?.getParcelable(IMAGE_INFO) }

    private lateinit var callback: ImageDetailCallback

    private lateinit var viewModel: ImageListViewModel

    private val adapter: CollectionAdapter by lazy { CollectionAdapter() }

    private val collectionList = mutableListOf<String>()

    companion object {
        const val TAG = "ImageDescriptionBottomSheet"
        private const val IMAGE_INFO = "image_info"

        fun newInstance(imageInfo: ImageInfo) = ImageDescriptionBottomSheet().apply {
            arguments = bundleOf(Pair(IMAGE_INFO, imageInfo))
        }
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
        binding = BottomSheetImageDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(viewModelStore, ImageListViewModelFactory())[ImageListViewModel::class.java]
        setupView()
        setupListener()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.collectionLiveData.observe(viewLifecycleOwner) { state ->
            onImageListViewStateChanged(state)
        }
    }

    private fun onImageListViewStateChanged(state: ImageListViewState?) {
        when(state) {
            is ImageListViewState.UpdateCollection -> updateCollection(state.newCollection)
            else -> {

            }
        }
    }

    private fun updateCollection(newCollection: List<String>) {
        adapter.updateItems(newCollection)
    }

    private fun setupListener() {
        binding.tvEdit.setOnClickListener {
            callback.onEditClicked(imageInfo?.collections)
        }
    }

    private fun setupView() {
        imageInfo?.run {
            configureCollections(collections)
            configureDescription(description)
        }
    }

    private fun configureDescription(description: String?) {
        if(!description.isNullOrEmpty()) {
            binding.tvDetail.text = description
            binding.tvDescription.visibility = View.VISIBLE
            binding.tvDetail.visibility = View.VISIBLE
        } else {
            binding.tvDetail.visibility = View.GONE
        }
    }

    private fun configureCollections(collections: List<String>?) {
        if (collections.isNullOrEmpty()) return
        binding.rvCollections.visibility = View.VISIBLE
        binding.tvCollections.visibility = View.VISIBLE
        val layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }
        binding.rvCollections.layoutManager = layoutManager
        binding.rvCollections.adapter = adapter
        adapter.updateItems(collections);
        binding.rvCollections.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.left = requireContext().resources.getDimensionPixelOffset(R.dimen.dimen_10)
                outRect.right = requireContext().resources.getDimensionPixelOffset(R.dimen.dimen_10)
            }
        })
    }

    override fun onCollectionSelected(collections: List<String>) {
        collectionList.addAll(collections)
        adapter.updateItems(collectionList)
    }
}
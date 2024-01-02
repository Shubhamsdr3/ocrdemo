package com.pandey.shubham.ocrdemoapp.ui

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.pandey.shubham.data.ImageInfo
import com.pandey.shubham.ocrdemoapp.R
import com.pandey.shubham.ocrdemoapp.callbacks.EditBottomSheetCallback
import com.pandey.shubham.ocrdemoapp.callbacks.ImageDetailCallback
import com.pandey.shubham.ocrdemoapp.databinding.FragmentImageInfoBinding

/**
 * Created by shubhampandey
 */
class ImageInfoDetailFragment : Fragment(), EditBottomSheetCallback {

    private lateinit var binding: FragmentImageInfoBinding

    private lateinit var callback: ImageDetailCallback

    private var imageInfo: ImageInfo? = null

    private val updatedCollections = mutableListOf<String>()

    private val adapter: CollectionAdapter by lazy { CollectionAdapter() }

    companion object {
        const val TAG = "ImageInfoDetailFragment"

        private const val IMAGE_INFO = "image_info"
        fun newInstance(imageInfo: ImageInfo) = ImageInfoDetailFragment().apply {
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
        binding = FragmentImageInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageInfo = arguments?.getParcelable(IMAGE_INFO) as ImageInfo?
        setupDetailView(imageInfo)
        setUpListener()
    }

    private fun setupDetailView(imageInfo: ImageInfo?) {
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
        updatedCollections.addAll(collections)
        binding.rvCollections.visibility = View.VISIBLE
        binding.tvCollections.visibility = View.VISIBLE
        val layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }
        binding.rvCollections.layoutManager = layoutManager
        binding.rvCollections.adapter = adapter
        adapter.updateItems(collections)
        binding.rvCollections.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.left = requireContext().resources.getDimensionPixelOffset(R.dimen.dimen_10)
                outRect.right = requireContext().resources.getDimensionPixelOffset(R.dimen.dimen_10)
            }
        })
    }

    private fun setUpListener() {
        binding.tvEdit.setOnClickListener {
            EditCollectionBottomSheet.newInstance(imageInfo?.collections).show(childFragmentManager, EditCollectionBottomSheet.TAG)
        }
    }

    override fun onCollectionSelected(collections: List<String>) {
        updatedCollections.addAll(collections)
        adapter.updateItems(updatedCollections)
    }
}
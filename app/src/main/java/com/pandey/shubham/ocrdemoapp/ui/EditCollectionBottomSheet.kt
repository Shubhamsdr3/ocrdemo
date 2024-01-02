package com.pandey.shubham.ocrdemoapp.ui

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pandey.shubham.ocrdemoapp.callbacks.EditBottomSheetCallback
import com.pandey.shubham.ocrdemoapp.R
import com.pandey.shubham.ocrdemoapp.databinding.BottomSheetEditCollectionBinding
import com.pandey.shubham.ocrdemoapp.viewmodel.ImageListViewModel
import com.pandey.shubham.ocrdemoapp.viewmodel.ImageListViewModelFactory
import com.pandey.shubham.textChanges
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.IllegalArgumentException
import kotlin.coroutines.CoroutineContext

/**
 * Created by shubhampandey
 */
class EditCollectionBottomSheet: BottomSheetDialogFragment(), CoroutineScope {

    private lateinit var binding: BottomSheetEditCollectionBinding

    private val collectionMutable = mutableListOf<String>()

    private lateinit var callback: EditBottomSheetCallback

    private lateinit var viewModel: ImageListViewModel

    private val adapter by lazy { CollectionAdapter() }

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    companion object {
        const val TAG = "EditCollectionBottomSheet"
        private const val COLLECTIONS = "collections"

        fun newInstance(collections: List<String>?) = EditCollectionBottomSheet().apply {
            arguments = bundleOf(Pair(COLLECTIONS, ArrayList(collections)))
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment != null && parentFragment is EditBottomSheetCallback) {
            this.callback = parentFragment as EditBottomSheetCallback
        } else if (context is EditBottomSheetCallback) {
            this.callback = context
        } else {
            throw IllegalArgumentException("$context must implement fragment callback")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomSheetEditCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val collections = arguments?.getStringArrayList(COLLECTIONS) as ArrayList<String>?
        collections?.let { collectionMutable.addAll(it) }
        viewModel = ViewModelProvider(viewModelStore, ImageListViewModelFactory())[ImageListViewModel::class.java]
        setupView()
        setupListener()
    }

    @OptIn(FlowPreview::class)
    private fun setupListener() {
        binding.etCollections.textChanges().debounce(500)
            .onEach {
                val searchText = it.toString().trim()
                if (searchText.isNotEmpty()) {
                    collectionMutable.add(searchText)
                    adapter.updateItems(collectionMutable)
                }
            }.launchIn(lifecycleScope)
        binding.btnDone.setOnClickListener {
            viewModel.addCollections(collectionMutable)
            callback.onCollectionSelected(collectionMutable)
            dismissAllowingStateLoss()
        }
    }

    private fun setupView() {
        if (collectionMutable.isEmpty()) return
        val layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }
        binding.rvItems.layoutManager = layoutManager
        binding.rvItems.adapter = adapter
        adapter.updateItems(collectionMutable)
        binding.rvItems.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.left = requireContext().resources.getDimensionPixelOffset(R.dimen.dimen_10)
                outRect.right = requireContext().resources.getDimensionPixelOffset(R.dimen.dimen_10)
            }
        })
    }
}
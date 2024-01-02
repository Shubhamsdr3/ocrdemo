package com.pandey.shubham.ocrdemoapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.window.OnBackInvokedDispatcher
import com.pandey.shubham.data.ImageInfo
import com.pandey.shubham.ocrdemoapp.R
import com.pandey.shubham.ocrdemoapp.callbacks.ImageDetailCallback
import com.pandey.shubham.ocrdemoapp.databinding.ActivityMainBinding

class HomeActivity : AppCompatActivity(), ImageDetailCallback {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btOpenGallery.setOnClickListener {
            loadFragment(savedInstanceState, true)
        }

        binding.btFile.setOnClickListener {
            loadFragment(savedInstanceState, false)
        }
    }

    private fun loadFragment(savedInstanceState: Bundle?, fromGallery: Boolean) {
        binding.btOpenGallery.visibility = View.GONE
        binding.btFile.visibility = View.GONE
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, ImageListFragment.newInstance(fromGallery), ImageListFragment.TAG)
                .commitAllowingStateLoss()
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count > 0) {
            supportFragmentManager.popBackStackImmediate()
        } else {
            super.onBackPressed()
        }
    }

    override fun onShowDetailClicked(imageInfo: ImageInfo?) {
        imageInfo?.let {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, ImageInfoDetailFragment.newInstance(it), ImageInfoDetailFragment.TAG)
                .commitAllowingStateLoss()
        }
    }
}
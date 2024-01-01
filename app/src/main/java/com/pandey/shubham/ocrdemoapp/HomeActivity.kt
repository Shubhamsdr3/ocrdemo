package com.pandey.shubham.ocrdemoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pandey.shubham.ocrdemoapp.databinding.ActivityMainBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadFragment(savedInstanceState)
    }

    private fun loadFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, ImageListFragment.newInstance(), ImageListFragment.TAG)
                .commitAllowingStateLoss()
        }
    }
}
package com.example.dermai.ui.landing

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.dermai.databinding.ActivityLandingBinding
import com.google.android.material.tabs.TabLayoutMediator

class LandingActivity : AppCompatActivity() {
    lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = LandingPagerAdapter(this)
        binding.viewPager.adapter = adapter

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.viewPager.currentItem == 1) {
                    binding.viewPager.currentItem = 0
                } else {
                    finish() // or super.onBackPressed()
                }
            }
        })
    }
}

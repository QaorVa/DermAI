package com.example.dermai.ui.collection

import android.content.Intent
import com.example.dermai.R
import com.example.dermai.databinding.ActivityCollectionBinding
import com.example.dermai.ui.base.BaseActivity
import com.example.dermai.ui.camera.CameraActivity
import com.example.dermai.ui.home.HomeActivity
import com.example.dermai.ui.wishlist.WishlistActivity

class CollectionActivity : BaseActivity<ActivityCollectionBinding>() {

    override fun getViewBinding(): ActivityCollectionBinding {
        return ActivityCollectionBinding.inflate(layoutInflater)
    }

    override fun setUI() {
        setupBottomNavigationView()
    }

    override fun setProcess() {

    }

    override fun setActions() {
        binding.apply {
            cvMakeup.setOnClickListener {
                intent = Intent(this@CollectionActivity, WishlistActivity::class.java)
                intent.putExtra(WishlistActivity.EXTRA_CATEGORY, "makeup")
                startActivity(intent)
            }
            cvSkincare.setOnClickListener {
                intent = Intent(this@CollectionActivity, WishlistActivity::class.java)
                intent.putExtra(WishlistActivity.EXTRA_CATEGORY, "skincare")
                startActivity(intent)
            }
        }
    }

    override fun setObservers() {

    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigationView.selectedItemId = R.id.collection

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    // Handle home click
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.camera -> {
                    // Handle camera click
                    val intent = Intent(this, CameraActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.collection -> {
                    // Handle collection click
                    true
                }
                else -> false
            }
        }
    }
}
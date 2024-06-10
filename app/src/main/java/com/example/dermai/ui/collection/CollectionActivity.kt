package com.example.dermai.ui.collection

import android.content.Intent
import com.example.dermai.databinding.ActivityCollectionBinding
import com.example.dermai.ui.base.BaseActivity
import com.example.dermai.ui.wishlist.WishlistActivity

class CollectionActivity : BaseActivity<ActivityCollectionBinding>() {

    override fun getViewBinding(): ActivityCollectionBinding {
        return ActivityCollectionBinding.inflate(layoutInflater)
    }

    override fun setUI() {

    }

    override fun setProcess() {

    }

    override fun setActions() {
        binding.apply {
            cvMakeup.setOnClickListener {
                intent = Intent(this@CollectionActivity, WishlistActivity::class.java)
                startActivity(intent)
            }
            cvSkincare.setOnClickListener {
                intent = Intent(this@CollectionActivity, WishlistActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun setObservers() {

    }
}
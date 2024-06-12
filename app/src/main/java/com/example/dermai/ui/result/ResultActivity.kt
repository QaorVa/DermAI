package com.example.dermai.ui.result

import android.content.Intent
import android.net.Uri
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dermai.R
import com.example.dermai.data.model.Product
import com.example.dermai.databinding.ActivityResultBinding
import com.example.dermai.ui.adapter.WishlistAdapter
import com.example.dermai.ui.base.BaseActivity
import com.example.dermai.ui.wishlist.WishlistActivity

class ResultActivity : BaseActivity<ActivityResultBinding>() {
    private lateinit var adapterSkincare: WishlistAdapter
    private lateinit var adapterMakeup: WishlistAdapter

    override fun getViewBinding(): ActivityResultBinding {
        return ActivityResultBinding.inflate(layoutInflater)
    }

    override fun setUI() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.chevron_left)
        }
    }

    override fun setProcess() {
        setAdapterSkincare()
        setAdapterMakeup()

    }

    override fun setObservers() {

    }

    private fun getInitialResultData(): List<Product> {
        return listOf(
            Product(1, "Skincare A", 4.5f, 150000, false, "Dry, Medium To Dark", Uri.parse("https://res.cloudinary.com/dowzkjtns/image/fetch/f_auto,c_limit,w_3840,q_auto/https://assets.thebodyshop.co.id/products/101011120-NEW%20VITAMIN%20E%20MOISTURE%20CREAM%20100ML-2.jpg"), "https://example.com/product1", "skincare"),
            Product(2, "Skincare B", 4.0f, 200000, false, "Combination, Low", Uri.parse("https://example.com/image2.jpg"), "https://example.com/product2", "skincare"),
            Product(3, "Skincare C", 4.8f, 250000, false, "Oily, Fair To Light", Uri.parse("https://example.com/image3.jpg"), "https://example.com/product3", "skincare"),
            Product(4, "Makeup A", 4.5f, 150000, false, "Dry, Medium To Dark", Uri.parse("https://res.cloudinary.com/dowzkjtns/image/fetch/f_auto,c_limit,w_3840,q_auto/https://assets.thebodyshop.co.id/products/101011120-NEW%20VITAMIN%20E%20MOISTURE%20CREAM%20100ML-2.jpg"), "https://example.com/product1", "makeup"),
            Product(5, "Makeup B", 4.0f, 200000, false, "Combination, Low", Uri.parse("https://example.com/image2.jpg"), "https://example.com/product2", "makeup"),
            Product(6, "Makeup C", 4.8f, 250000, false, "Oily, Fair To Light", Uri.parse("https://example.com/image3.jpg"), "https://example.com/product3", "makeup")
        )
    }

    private fun setAdapterSkincare() {
        adapterSkincare = WishlistAdapter(object : WishlistAdapter.OnFavoriteClickCallback {
            override fun onFavoriteClicked(data: Product) {
                val position = adapterSkincare.currentList.indexOf(data)
                if (position != -1) {
                    // Remove the item from the adapter
                    adapterSkincare.removeItem(position)
                }
            }

        }, object : WishlistAdapter.OnLinkClickCallback {
            override fun onLinkClicked(data: Product) {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, data.link)
                    type = "text/plain"
                }
                val chooser = Intent.createChooser(sendIntent, null)
                startActivity(chooser)
            }
        }, 2)

        adapterSkincare.submitList(getInitialResultData().filter { it.category == "skincare" })

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvResultSkincare.layoutManager = layoutManager

        binding.rvResultSkincare.adapter = adapterSkincare
    }

    private fun setAdapterMakeup() {
        adapterMakeup = WishlistAdapter(object : WishlistAdapter.OnFavoriteClickCallback {
            override fun onFavoriteClicked(data: Product) {
                val position = adapterMakeup.currentList.indexOf(data)
                if (position != -1) {
                    // Remove the item from the adapter
                    adapterMakeup.removeItem(position)
                }
            }

        }, object : WishlistAdapter.OnLinkClickCallback {
            override fun onLinkClicked(data: Product) {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, data.link)
                    type = "text/plain"
                }
                val chooser = Intent.createChooser(sendIntent, null)
                startActivity(chooser)
            }
        }, 2)

        adapterMakeup.submitList(getInitialResultData().filter { it.category == "makeup" })

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvResultMakeup.layoutManager = layoutManager

        binding.rvResultMakeup.adapter = adapterMakeup
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
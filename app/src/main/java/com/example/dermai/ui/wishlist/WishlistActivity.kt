package com.example.dermai.ui.wishlist

import android.content.Intent
import android.net.Uri
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dermai.R
import com.example.dermai.data.model.Product
import com.example.dermai.databinding.ActivityWishlistBinding
import com.example.dermai.ui.adapter.WishlistAdapter
import com.example.dermai.ui.base.BaseActivity

class WishlistActivity : BaseActivity<ActivityWishlistBinding>() {
        private lateinit var adapter: WishlistAdapter

        override fun getViewBinding(): ActivityWishlistBinding {
            return ActivityWishlistBinding.inflate(layoutInflater)
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
                adapter = WishlistAdapter(object : WishlistAdapter.OnFavoriteClickCallback {
                        override fun onFavoriteClicked(data: Product) {
                                val position = adapter.currentList.indexOf(data)
                                if (position != -1) {
                                        // Remove the item from the adapter
                                        adapter.removeItem(position)
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
                }, 1)

                submitListCategory()

                binding.rvWishlist.adapter = adapter

                binding.apply {
                        val layoutManager = LinearLayoutManager(this@WishlistActivity)
                        rvWishlist.layoutManager = layoutManager
                        rvWishlist.adapter = adapter
                }
        }

        override fun setObservers() {

        }

        private fun getInitialWishlistData(): List<Product> {
                return listOf(
                        Product(1, "Skincare A", 4.5f, 150000, false, "Dry, Medium To Dark", Uri.parse("https://res.cloudinary.com/dowzkjtns/image/fetch/f_auto,c_limit,w_3840,q_auto/https://assets.thebodyshop.co.id/products/101011120-NEW%20VITAMIN%20E%20MOISTURE%20CREAM%20100ML-2.jpg"), "https://example.com/product1", "skincare"),
                        Product(2, "Skincare B", 4.0f, 200000, false, "Combination, Low", Uri.parse("https://example.com/image2.jpg"), "https://example.com/product2", "skincare"),
                        Product(3, "Skincare C", 4.8f, 250000, false, "Oily, Fair To Light", Uri.parse("https://example.com/image3.jpg"), "https://example.com/product3", "skincare"),
                        Product(4, "Makeup A", 4.5f, 150000, false, "Dry, Medium To Dark", Uri.parse("https://res.cloudinary.com/dowzkjtns/image/fetch/f_auto,c_limit,w_3840,q_auto/https://assets.thebodyshop.co.id/products/101011120-NEW%20VITAMIN%20E%20MOISTURE%20CREAM%20100ML-2.jpg"), "https://example.com/product1", "makeup"),
                        Product(5, "Makeup B", 4.0f, 200000, false, "Combination, Low", Uri.parse("https://example.com/image2.jpg"), "https://example.com/product2", "makeup"),
                        Product(6, "Makeup C", 4.8f, 250000, false, "Oily, Fair To Light", Uri.parse("https://example.com/image3.jpg"), "https://example.com/product3", "makeup")
                )
        }

        private fun submitListCategory() {
                when(intent.getStringExtra(EXTRA_CATEGORY)) {
                        "skincare" -> {
                                adapter.submitList(getInitialWishlistData().filter { it.category == "skincare" })
                        }
                        "makeup" -> {
                                adapter.submitList(getInitialWishlistData().filter { it.category == "makeup" })
                        }
                }
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
                if (item.itemId == android.R.id.home) {
                        onBackPressed()
                        return true
                }
                return super.onOptionsItemSelected(item)
        }

        companion object {
                const val EXTRA_CATEGORY = "extra_category"
        }
}
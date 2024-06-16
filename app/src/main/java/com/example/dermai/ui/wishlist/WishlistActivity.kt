package com.example.dermai.ui.wishlist

import android.content.Intent
import android.net.Uri
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dermai.R
import com.example.dermai.data.model.Product
import com.example.dermai.databinding.ActivityWishlistBinding
import com.example.dermai.ui.adapter.ProductAdapter
import com.example.dermai.ui.base.BaseActivity
import com.example.dermai.ui.camera.CameraActivity
import com.example.dermai.ui.home.HomeActivity

class WishlistActivity : BaseActivity<ActivityWishlistBinding>() {
        private lateinit var adapter: ProductAdapter
        private val wishlistRepository = WishlistRepository()

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
                setupBottomNavigationView()
        }

        override fun setProcess() {
                adapter = ProductAdapter(object : ProductAdapter.OnFavoriteClickCallback {
                        override fun onFavoriteClicked(data: Product) {
                                val position = adapter.currentList.indexOf(data)
                                if (position != -1) {
                                        adapter.removeItem(position)
                                        wishlistRepository.removeProductFromWishlist(data.id)
                                }
                        }

                }, object : ProductAdapter.OnLinkClickCallback {
                        override fun onLinkClicked(data: Product) {
                                val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, data.link)
                                        type = "text/plain"
                                }
                                val chooser = Intent.createChooser(sendIntent, null)
                                startActivity(chooser)
                        }
                }, 1, wishlistRepository)

                binding.rvWishlist.adapter = adapter
                binding.rvWishlist.layoutManager = LinearLayoutManager(this)

                loadWishlistData()
        }

        override fun setObservers() {

        }

        private fun loadWishlistData() {
                wishlistRepository.getWishlist { products ->
                        adapter.submitList(products)
                }
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
                if (item.itemId == android.R.id.home) {
                        onBackPressed()
                        return true
                }
                return super.onOptionsItemSelected(item)
        }

        private fun setupBottomNavigationView() {
                binding.bottomNavigationView.selectedItemId = R.id.wishlist

                binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
                        when (menuItem.itemId) {
                                R.id.home -> {
                                        val intent = Intent(this, HomeActivity::class.java)
                                        startActivity(intent)
                                        true
                                }
                                R.id.camera -> {
                                        val intent = Intent(this, CameraActivity::class.java)
                                        startActivity(intent)
                                        true
                                }
                                R.id.wishlist -> {
                                        true
                                }
                                else -> false
                        }
                }
        }
}
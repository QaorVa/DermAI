package com.example.dermai.ui.wishlist

import android.content.Intent
import android.net.Uri
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
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
        private lateinit var viewModel: WishlistViewModel

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
                viewModel = ViewModelProvider(this)[WishlistViewModel::class.java]

                adapter = ProductAdapter(object : ProductAdapter.OnFavoriteClickCallback {
                        override fun onFavoriteClicked(data: Product) {
                                val position = adapter.currentList.indexOf(data)
                                if (position != -1) {
                                        viewModel.deleteWishlist(data.url)
                                }
                        }

                }, object : ProductAdapter.OnLinkClickCallback {
                        override fun onLinkClicked(data: Product) {
                                val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, data.url)
                                        type = "text/plain"
                                }
                                val chooser = Intent.createChooser(sendIntent, null)
                                startActivity(chooser)
                        }
                }, 1)

                binding.rvWishlist.adapter = adapter

                binding.apply {
                        val layoutManager = LinearLayoutManager(this@WishlistActivity)
                        rvWishlist.layoutManager = layoutManager
                        rvWishlist.adapter = adapter
                }
        }

        override fun setObservers() {
                viewModel.getAllWishlist().observe(this) {
                        adapter.submitList(it)
                }
        }

        /*private fun getInitialWishlistData(): List<Product> {
                return listOf(
                        Product("neutrogena", "hydro boost emulsion face moisturisers 50 g", "Rp 214500", "https://www.myntra.com/face-moisturisers/neutrogena/neutrogena-hydro-boost-emulsion-face-moisturisers-50-g/10337731/buy", "https://assets.myntassets.com/h_1136,q_90,w_852/v1/assets/images/10337731/2020/8/21/64516939-6ad3-4db2-8477-a71064dcbe211598008967441-Neutrogena-Unisex-Hydro-Boost-Emulsion-Face-Moisturisers-50--1.jpg", "dry", listOf("general care", "", "")),
                        Product("neutrogena", "hydro boost emulsion face moisturisers 50 g", "Rp 214500", "https://www.myntra.com/face-moisturisers/neutrogena/neutrogena-hydro-boost-emulsion-face-moisturisers-50-g/10337731/buy", "https://assets.myntassets.com/h_1136,q_90,w_852/v1/assets/images/10337731/2020/8/21/64516939-6ad3-4db2-8477-a71064dcbe211598008967441-Neutrogena-Unisex-Hydro-Boost-Emulsion-Face-Moisturisers-50--1.jpg", "dry", listOf("general care", "dryness", "deep nourishment")),
                        Product("neutrogena", "hydro boost emulsion face moisturisers 50 g", "Rp 214500", "https://www.myntra.com/face-moisturisers/neutrogena/neutrogena-hydro-boost-emulsion-face-moisturisers-50-g/10337731/buy", "https://assets.myntassets.com/h_1136,q_90,w_852/v1/assets/images/10337731/2020/8/21/64516939-6ad3-4db2-8477-a71064dcbe211598008967441-Neutrogena-Unisex-Hydro-Boost-Emulsion-Face-Moisturisers-50--1.jpg", "dry", listOf("hydration", "dryness", "softening", "smoothening")),
                        Product("neutrogena", "hydro boost emulsion face moisturisers 50 g", "Rp 214500", "https://www.myntra.com/face-moisturisers/neutrogena/neutrogena-hydro-boost-emulsion-face-moisturisers-50-g/10337731/buy", "https://assets.myntassets.com/h_1136,q_90,w_852/v1/assets/images/10337731/2020/8/21/64516939-6ad3-4db2-8477-a71064dcbe211598008967441-Neutrogena-Unisex-Hydro-Boost-Emulsion-Face-Moisturisers-50--1.jpg", "dry", listOf("general care", "", "")),
                )
        }

        private fun submitListCategory() {
                adapter.submitList(getInitialWishlistData())
        }*/

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
                                R.id.wishlist -> {
                                        // Handle collection click
                                        true
                                }
                                else -> false
                        }
                }
        }

        companion object {
                const val EXTRA_CATEGORY = "extra_category"
        }
}
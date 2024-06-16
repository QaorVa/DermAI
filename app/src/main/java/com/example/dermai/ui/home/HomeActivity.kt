package com.example.dermai.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dermai.R
import com.example.dermai.data.model.Product
import com.example.dermai.data.model.SkinProfile
import com.example.dermai.databinding.ActivityHomeBinding
import com.example.dermai.ui.adapter.ProductAdapter
import com.example.dermai.ui.adapter.SkinProfileAdapter
import com.example.dermai.ui.base.BaseActivity
import com.example.dermai.ui.camera.CameraActivity
import com.example.dermai.ui.collection.CollectionActivity
import com.example.dermai.ui.details.DetailsActivity
import com.example.dermai.ui.login.LoginActivity
import com.example.dermai.ui.result.ResultActivity
import com.example.dermai.ui.wishlist.WishlistActivity
import com.example.dermai.ui.wishlist.WishlistRepository
import com.example.dermai.utils.AuthViewModel
import com.example.dermai.utils.PreferenceManager
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var skinProfileAdapter: SkinProfileAdapter
    private lateinit var productAdapter: ProductAdapter
    private val wishlistRepository = WishlistRepository()

    override fun getViewBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUI()
        setActions()
        setProcess()
    }

    override fun setUI() {
        val user = FirebaseAuth.getInstance().currentUser
        binding.welcomeTextView.text = "Hello, ${user?.displayName ?: user?.email}"
        binding.greetingTextView.text = getGreetingMessage()
        setupBottomNavigationView()
    }

    override fun setActions() {
        binding.settingsIcon.setOnClickListener {
            logout()
        }

        binding.skinResultButton.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
        }
    }

    override fun setProcess() {
        setSkinProfile()
        setProduct()
    }

    private fun setSkinProfile() {
        skinProfileAdapter = SkinProfileAdapter(object : SkinProfileAdapter.OnDetailsClickCallback {
            override fun onDetailsClicked(data: SkinProfile) {
                when(data.title) {
                    "Skin Type" -> {
                        val intent = Intent(this@HomeActivity, DetailsActivity::class.java)
                        intent.putExtra(DetailsActivity.EXTRA_SELECT, "skin_type")
                        startActivity(intent)
                    }
                    "Acne Level" -> {
                        val intent = Intent(this@HomeActivity, DetailsActivity::class.java)
                        intent.putExtra(DetailsActivity.EXTRA_SELECT, "acne_level")
                        startActivity(intent)
                    }
                    "Skin Tone" -> {
                        val intent = Intent(this@HomeActivity, DetailsActivity::class.java)
                        intent.putExtra(DetailsActivity.EXTRA_SELECT, "skin_tone")
                        startActivity(intent)
                    }
                }
            }
        })
        val skinProfiles = listOf(
            SkinProfile(1, "Skin Type", "Combination"),
            SkinProfile(2, "Acne Level", "Low"),
            SkinProfile(3, "Skin Tone", "Light")
        )
        skinProfileAdapter.submitList(skinProfiles)

        binding.skinProfileRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.skinProfileRecyclerView.adapter = skinProfileAdapter
    }

    private fun getGreetingMessage(): String {
        return when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            in 18..21 -> "Good Evening"
            else -> "Good Night"
        }
    }

    private fun setProduct() {
        productAdapter = ProductAdapter(object : ProductAdapter.OnFavoriteClickCallback {
            override fun onFavoriteClicked(data: Product) {
                wishlistRepository.addProductToWishlist(data)
                productAdapter.notifyDataSetChanged()
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
        }, 2, wishlistRepository)

        val products = listOf(
            Product(1, "Skincare A", 4.5f, 150000, false, "Dry, Medium To Dark", "https://res.cloudinary.com/dowzkjtns/image/fetch/f_auto,c_limit,w_3840,q_auto/https://assets.thebodyshop.co.id/products/101011120-NEW%20VITAMIN%20E%20MOISTURE%20CREAM%20100ML-2.jpg", "https://example.com/product1", "skincare"),
            Product(2, "Skincare B", 4.0f, 200000, false, "Combination, Low", "https://example.com/image2.jpg", "https://example.com/product2", "skincare"),
            Product(3, "Skincare C", 4.8f, 250000, false, "Oily, Fair To Light", "https://example.com/image3.jpg", "https://example.com/product3", "skincare"),
            Product(4, "Makeup A", 4.5f, 150000, false, "Dry, Medium To Dark", "https://res.cloudinary.com/dowzkjtns/image/fetch/f_auto,c_limit,w_3840,q_auto/https://assets.thebodyshop.co.id/products/101011120-NEW%20VITAMIN%20E%20MOISTURE%20CREAM%20100ML-2.jpg", "https://example.com/product1", "makeup"),
            Product(5, "Makeup B", 4.0f, 200000, false, "Combination, Low", "https://example.com/image2.jpg", "https://example.com/product2", "makeup"),
            Product(6, "Makeup C", 4.8f, 250000, false, "Oily, Fair To Light", "https://example.com/image3.jpg", "https://example.com/product3", "makeup")
        )

        productAdapter.submitList(products)

        binding.recommendedRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recommendedRecyclerView.adapter = productAdapter
    }


    private fun setupBottomNavigationView() {
        binding.bottomNavigationView.selectedItemId = R.id.home

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> true
                R.id.camera -> {
                    val intent = Intent(this, CameraActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.wishlist -> {
                    val intent = Intent(this, WishlistActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun setObservers() {}

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishAffinity()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun logout() {
        authViewModel.logout()
        PreferenceManager.getInstance(this).clearPreferences()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

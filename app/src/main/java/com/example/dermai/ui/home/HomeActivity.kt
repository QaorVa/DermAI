package com.example.dermai.ui.home

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.dermai.R
import com.example.dermai.data.model.Product
import com.example.dermai.data.model.ResultResponse
import com.example.dermai.data.model.SkinProfile
import com.example.dermai.data.model.Skincare
import com.example.dermai.databinding.ActivityHomeBinding
import com.example.dermai.ui.adapter.ProductAdapter
import com.example.dermai.ui.adapter.SkinProfileAdapter
import com.example.dermai.ui.base.BaseActivity
import com.example.dermai.ui.camera.CameraActivity
import com.example.dermai.ui.details.DetailsActivity
import com.example.dermai.ui.login.LoginActivity
import com.example.dermai.ui.result.ResultActivity
import com.example.dermai.ui.wishlist.WishlistActivity
import com.example.dermai.utils.AuthViewModel
import com.example.dermai.utils.PreferenceManager
import com.example.dermai.utils.capitalizeFirstLetter
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var skinProfileAdapter: SkinProfileAdapter
    private lateinit var productAdapter: ProductAdapter

    private var result: ResultResponse? = null

    override fun getViewBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun setUI() {
        val user = FirebaseAuth.getInstance().currentUser
        binding.welcomeTextView.text = "Hello, ${user?.displayName ?: user?.email}"
        binding.greetingTextView.text = getGreetingMessage()

        user?.photoUrl?.toString()?.let { loadProfileImage(it) }

        setupBottomNavigationView()
    }

    override fun setActions() {
        binding.settingsIcon.setOnClickListener {
            logout()
        }

        binding.skinResultButton.setOnClickListener {
            if(result == null) {
                showAlertDialog()
                return@setOnClickListener
            } else {
                val intent = Intent(this, ResultActivity::class.java)
                startActivity(intent)
            }
        }

        binding.cameraButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    override fun setProcess() {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        setSkinProfile()
        setProduct()
    }

    override fun setObservers() {

        homeViewModel.getResult()?.observe(this) {
            if(it != null) {
                result = it

                val toneNumber = it.tone.toInt()
                val toneText = if(toneNumber <= 2) {
                    getString(R.string.skin_tone_fairtolight)
                } else if(toneNumber < 4) {
                    getString(R.string.skin_tone_lighttomedium)
                } else {
                    getString(R.string.skin_tone_mediumtodark)
                }


                val skinProfiles = listOf(
                    SkinProfile(1, "Skin Type", it.type.capitalizeFirstLetter()),
                    SkinProfile(2, "Acne Level", it.acne),
                    SkinProfile(3, "Skin Tone", toneText)
                )
                skinProfileAdapter.submitList(skinProfiles)

                productAdapter.submitList(it.recommendedProducts.faceMoisturisers)

                homeViewModel.getAllWishlist().observe(this) { wishlist ->
                    wishlist.forEach { wishlistItem ->
                        val index = productAdapter.currentList.indexOfFirst { it.url == wishlistItem.url }
                        if (index != -1) {
                            productAdapter.currentList[index].isFavorited = true
                            productAdapter.notifyItemChanged(index)
                        }
                    }
                }
            } else {
                showAlertDialog()

            }
        }

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
                Log.d("HomeActivity", "Current favorite state: ${data.isFavorited}")

                // Perform the appropriate operation based on the current state
                if (data.isFavorited) {
                    homeViewModel.deleteWishlist(data.url)
                    Log.d("HomeActivity", "onFavoriteClicked: delete wishlist")
                } else {
                    homeViewModel.insertWishlist(data)
                    Log.d("HomeActivity", "onFavoriteClicked: insert wishlist")
                }

                // Toggle the favorite state
                data.isFavorited = !data.isFavorited

                // Log the new state
                Log.d("HomeActivity", "New favorite state: ${data.isFavorited}")

                // Update the UI
                productAdapter.notifyItemChanged(productAdapter.currentList.indexOf(data))
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
        }, 2)

        binding.recommendedRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recommendedRecyclerView.adapter = productAdapter
    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigationView.selectedItemId = R.id.home
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    // Handle home click
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
                    val intent = Intent(this, WishlistActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

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

    private fun loadProfileImage(photoUrl: String) {
        Glide.with(this)
            .load(photoUrl)
            .placeholder(R.drawable.profile_default)
            .error(R.drawable.profile_default)
            .into(binding.profileImageView)
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("No Result Found")
            .setMessage("Oops! It looks like You haven't taken a photo yet. Please take a photo first.")
            .setPositiveButton("OK") { dialog, _ ->
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)

                finish()
            }
            .setNegativeButton("Later") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

}

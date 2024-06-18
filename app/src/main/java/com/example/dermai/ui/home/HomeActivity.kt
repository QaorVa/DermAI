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
    private lateinit var result: ResultResponse

    private val mockResultResponse = ResultResponse(
        type = "dry",
        tone = "2",
        acne = "Moderate",
        recommendedProducts = Skincare(
            faceMoisturisers = listOf(
                Product(
                    brand = "neutrogena",
                    name = "hydro boost emulsion face moisturisers 50 g",
                    price = "Rp 214500",
                    url = "https://www.myntra.com/face-moisturisers/neutrogena/neutrogena-hydro-boost-emulsion-face-moisturisers-50-g/10337731/buy",
                    img = "https://assets.myntassets.com/h_1136,q_90,w_852/v1/assets/images/10337731/2020/8/21/64516939-6ad3-4db2-8477-a71064dcbe211598008967441-Neutrogena-Unisex-Hydro-Boost-Emulsion-Face-Moisturisers-50--1.jpg",
                    skinType = "dry",
                    concerns = listOf("general care", "", "")
                ),
                Product(
                    brand = "the body shop",
                    name = "the body shop",
                    price = "Rp 206310",
                    url = "https://www.myntra.com/face-moisturisers/the-body-shop/the-body-shop-vitamin-e-intense-moisture-cream-for-dry-skin-50-ml/7576908/buy",
                    img = "https://assets.myntassets.com/h_720,q_90,w_540/v1/assets/images/7576908/2018/10/8/585c7726-315f-477a-aaed-8ea286b4b5b01538985334249-The-Body-Shop-Vitamin-E-Intense-Moisture-Cream-50ml-2201538985334149-1.jpg",
                    skinType = "dry",
                    concerns = listOf("general care", "", "")
                ),
                // Add more products as needed
            ),
            cleansers = listOf(
                Product(
                    brand = "skinkraft",
                    name = "women moisturizing cleanser 60ml",
                    price = "Rp 77805",
                    url = "https://www.myntra.com/face-wash-and-cleanser/skinkraft/skinkraft-women-moisturizing-cleanser-60ml/15268060/buy",
                    img = "https://assets.myntassets.com/h_720,q_90,w_540/v1/assets/images/15268060/2021/8/26/c5a28087-da11-43cf-b035-5df0f1fa520b1629971224026SKINKRAFT1.jpg",
                    skinType = "dry",
                    concerns = listOf("hydration", "", "")
                ),
                // Add more cleansers as needed
            ),
            masksAndPeels = listOf(
                Product(
                    brand = "ustraa",
                    name = "men de tan face mask for dry skin",
                    price = "Rp 41925",
                    url = "https://www.myntra.com/mask-and-peel/ustraa/ustraa-men-de-tan-face-mask-for-dry-skin-/13236716/buy",
                    img = "https://assets.myntassets.com/h_720,q_90,w_540/v1/assets/images/13236716/2021/2/8/c244ef91-4421-4a9d-bf24-d1a83d843dc71612761404073UstraaMenDeTanFaceMaskForDrySkin1.jpg",
                    skinType = "dry",
                    concerns = listOf("tan removal", "uneven skin tone", "softening", "smoothening")
                ),
                // Add more masks and peels as needed
            ),
            eyeCreams = listOf(
                Product(
                    brand = "estee lauder",
                    name = "skincare saviours kit",
                    price = "Rp 389025",
                    url = "https://www.myntra.com/eye-cream/estee-lauder/estee-lauder-skincare-saviours-kit/15062164/buy",
                    img = "https://assets.myntassets.com/h_720,q_90,w_540/v1/assets/images/15062164/2021/10/11/f529fe4f-ee6c-420c-b843-ff74a3136b8d1633932670158-Estee-Lauder-Skincare-Saviours-Kit-8281633932670089-1.jpg",
                    skinType = "all",
                    concerns = listOf("dryness", "", "")
                ),
                // Add more eye creams as needed
            )
        )
    )

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
            val intent = Intent(this, ResultActivity::class.java)
            startActivity(intent)
        }
    }

    override fun setProcess() {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

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

        /*val products = listOf(
            Product("neutrogena", "hydro boost emulsion face moisturisers 50 g", "Rp 214500", "https://www.myntra.com/face-moisturisers/neutrogena/neutrogena-hydro-boost-emulsion-face-moisturisers-50-g/10337731/buy", "https://assets.myntassets.com/h_1136,q_90,w_852/v1/assets/images/10337731/2020/8/21/64516939-6ad3-4db2-8477-a71064dcbe211598008967441-Neutrogena-Unisex-Hydro-Boost-Emulsion-Face-Moisturisers-50--1.jpg", "dry", listOf("general care", "", "")),
            Product("the body shop", "the body shop", "Rp 214500", "https://www.myntra.com/face-moisturisers/the-body-shop/the-body-shop-vitamin-e-intense-moisture-cream-for-dry-skin-50-ml/7576908/buy", "https://assets.myntassets.com/h_1136,q_90,w_852/v1/assets/images/10337731/2020/8/21/64516939-6ad3-4db2-8477-a71064dcbe211598008967441-Neutrogena-Unisex-Hydro-Boost-Emulsion-Face-Moisturisers-50--1.jpg", "dry", listOf("general care", "dryness", "deep nourishment")),
            Product("just herbs", "unisex herbal nourishing facial massage cream 100 g", "Rp 214500", "https://www.myntra.com/face-moisturisers/just-herbs/just-herbs-unisex-herbal-nourishing-facial-massage-cream-100-g/11449220/buy", "https://assets.myntassets.com/h_1136,q_90,w_852/v1/assets/images/10337731/2020/8/21/64516939-6ad3-4db2-8477-a71064dcbe211598008967441-Neutrogena-Unisex-Hydro-Boost-Emulsion-Face-Moisturisers-50--1.jpg", "dry", listOf("hydration", "dryness", "softening", "smoothening")),
            Product("skinkraft", "customized moisturizer - ultra rich moisturizer for dry skin - 45 ml", "Rp 214500", "https://www.myntra.com/face-moisturisers/skinkraft/skinkraft-customized-moisturizer---ultra-rich-moisturizer-for-dry-skin---45-ml/15345050/buy", "https://assets.myntassets.com/h_720,q_90,w_540/v1/assets/images/15345050/2021/9/2/ba997201-d526-4a1c-af9c-c74ab06fa8901630587934717SKINKRAFT1.jpg", "dry", listOf("general care", "", "")),
        )*/

        binding.recommendedRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recommendedRecyclerView.adapter = productAdapter
    }

    private fun setupBottomNavigationView() {
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
                result = mockResultResponse

                AlertDialog.Builder(this)
                    .setTitle("No Result Found")
                    .setMessage("No result found. Please move to the camera to scan.")
                    .setPositiveButton("OK") { dialog, _ ->
                        val intent = Intent(this, CameraActivity::class.java)
                        startActivity(intent)

                        finish()
                    }
                    .setCancelable(false)
                    .show()
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

}

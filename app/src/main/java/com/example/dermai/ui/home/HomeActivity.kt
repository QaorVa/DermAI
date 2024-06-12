package com.example.dermai.ui.home

import android.content.Intent
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dermai.R
import com.example.dermai.databinding.ActivityHomeBinding
import com.example.dermai.ui.adapter.RecommendedProductAdapter
import com.example.dermai.ui.adapter.RecommendedProductItem
import com.example.dermai.ui.adapter.SkinProfileAdapter
import com.example.dermai.ui.adapter.SkinProfileItem
import com.example.dermai.ui.base.BaseActivity
import com.example.dermai.ui.camera.CameraActivity
import com.example.dermai.ui.collection.CollectionActivity
import com.example.dermai.ui.login.LoginActivity
import com.example.dermai.ui.result.ResultActivity
import com.example.dermai.utils.AuthViewModel
import com.example.dermai.utils.PreferenceManager
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun getViewBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun setUI() {
        val user = FirebaseAuth.getInstance().currentUser
        binding.welcomeTextView.text = "Hello, ${user?.displayName ?: user?.email}"
        binding.greetingTextView.text = getGreetingMessage()

        setupSkinProfileSection()
        setupRecommendedSection()
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

    private fun getGreetingMessage(): String {
        return when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            in 18..21 -> "Good Evening"
            else -> "Good Night"
        }
    }

    private fun setupSkinProfileSection() {
        val skinProfiles = listOf(
            SkinProfileItem("Skin Type", "Combination"),
            SkinProfileItem("Acne Level", "Low"),
            SkinProfileItem("Skin Tone", "Light")
        )

        val adapter = SkinProfileAdapter(skinProfiles)
        binding.skinProfileRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.skinProfileRecyclerView.adapter = adapter
    }

    private fun setupRecommendedSection() {
        val recommendedProducts = listOf(
            RecommendedProductItem("Moisturizer A", "$25", R.drawable.moisturizer_a),
            RecommendedProductItem("Cleanser B", "$20", R.drawable.cleanser_b),
            RecommendedProductItem("Toner A", "$18", R.drawable.moisturizer_a)
        )

        val adapter = RecommendedProductAdapter(recommendedProducts)
        binding.recommendedRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recommendedRecyclerView.adapter = adapter
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
                R.id.collection -> {
                    // Handle collection click
                    val intent = Intent(this, CollectionActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun setProcess() {}

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

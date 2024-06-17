package com.example.dermai.ui.result

import android.content.Intent
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dermai.R
import com.example.dermai.data.model.Product
import com.example.dermai.data.model.ResultResponse
import com.example.dermai.databinding.ActivityResultBinding
import com.example.dermai.ui.adapter.ProductAdapter
import com.example.dermai.ui.base.BaseActivity
import com.example.dermai.utils.capitalizeFirstLetter

class ResultActivity : BaseActivity<ActivityResultBinding>() {
    private lateinit var adapterMoisturizer: ProductAdapter
    private lateinit var adapterCleanser: ProductAdapter
    private lateinit var adapterMask: ProductAdapter
    private lateinit var adapterEyecream: ProductAdapter

    private lateinit var result: ResultResponse

    private lateinit var viewModel: ResultViewModel

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

        result = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_RESULT, ResultResponse::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_RESULT)!!
        }

        binding.apply {
            tvResultSkinTypeValue.text = result.type.capitalizeFirstLetter()
            resultSkinTypeDescription.text = when (result.type) {
                "Oily" -> {
                    getString(R.string.skin_type_oily_description_short)
                }
                "Dry" -> {
                    getString(R.string.skin_type_dry_description_short)
                }
                else -> {
                    getString(R.string.skin_type_combination_description_short)
                }
            }

            tvResultAcneLevelValue.text = result.acne
            resultAcneLevelDescription.text = when (result.acne) {
                "Low" -> {
                    getString(R.string.acne_level_low_description_short)
                }
                "Moderate" -> {
                    getString(R.string.acne_level_moderate_description_short)
                }
                else -> {
                    getString(R.string.acne_level_severe_description_short)
                }
            }

            tvResultSkinToneValue.text = result.tone

            val toneNumber = result.tone.toInt()
            tvResultSkinToneValue.text = if(toneNumber <= 2) {
                getString(R.string.skin_tone_fairtolight)
            } else if(toneNumber < 4) {
                getString(R.string.skin_tone_lighttomedium)
            } else {
                getString(R.string.skin_tone_mediumtodark)
            }
            resultSkinToneDescription.text = when (tvResultSkinToneValue.text) {
                "Fair to Light" -> {
                    getString(R.string.skin_tone_fairtolight_description_short)
                }
                "Light to Medium" -> {
                    getString(R.string.skin_tone_lighttomedium_description_short)
                }
                else -> {
                    getString(R.string.skin_tone_mediumtodark_description_short)
                }
            }
        }
    }

    override fun setProcess() {
        viewModel = ViewModelProvider(this)[ResultViewModel::class.java]

        setAdapterMoisturizer()
        setAdapterCleanser()
        setAdapterMask()
        setAdapterEyecream()
    }

    override fun setObservers() {
        viewModel.getAllWishlist().observe(this) { wishlist ->
            wishlist.forEach { wishlistItem ->
                val index = adapterMoisturizer.currentList.indexOfFirst { it.url == wishlistItem.url }
                if (index != -1) {
                    adapterMoisturizer.currentList[index].isFavorited = true
                    adapterMoisturizer.notifyItemChanged(index)
                }
            }
        }

        viewModel.getAllWishlist().observe(this) { wishlist ->
            wishlist.forEach { wishlistItem ->
                val index = adapterCleanser.currentList.indexOfFirst { it.url == wishlistItem.url }
                if (index != -1) {
                    adapterCleanser.currentList[index].isFavorited = true
                    adapterCleanser.notifyItemChanged(index)
                }
            }
        }

        viewModel.getAllWishlist().observe(this) { wishlist ->
            wishlist.forEach { wishlistItem ->
                val index = adapterMask.currentList.indexOfFirst { it.url == wishlistItem.url }
                if (index != -1) {
                    adapterMask.currentList[index].isFavorited = true
                    adapterMask.notifyItemChanged(index)
                }
            }
        }

        viewModel.getAllWishlist().observe(this) { wishlist ->
            wishlist.forEach { wishlistItem ->
                val index = adapterEyecream.currentList.indexOfFirst { it.url == wishlistItem.url }
                if (index != -1) {
                    adapterEyecream.currentList[index].isFavorited = true
                    adapterEyecream.notifyItemChanged(index)
                }
            }
        }

    }

    private fun setAdapterMoisturizer() {
        adapterMoisturizer = ProductAdapter(object : ProductAdapter.OnFavoriteClickCallback {
            override fun onFavoriteClicked(data: Product) {
                if (data.isFavorited) {
                    viewModel.deleteWishlist(data.url)
                    Log.d("ResultActivity", "onFavoriteClicked: delete wishlist")
                } else {
                    viewModel.insertWishlist(data)
                    Log.d("ResultActivity", "onFavoriteClicked: insert wishlist")
                }

                // Toggle the favorite state
                data.isFavorited = !data.isFavorited

                // Log the new state
                Log.d("ResultActivity", "New favorite state: ${data.isFavorited}")

                // Update the UI
                adapterMoisturizer.notifyItemChanged(adapterMoisturizer.currentList.indexOf(data))
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

        adapterMoisturizer.submitList(result.recommendedProducts.faceMoisturisers)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvResultMoisturizer.layoutManager = layoutManager

        binding.rvResultMoisturizer.adapter = adapterMoisturizer
    }

    private fun setAdapterCleanser() {
        adapterCleanser = ProductAdapter(object : ProductAdapter.OnFavoriteClickCallback {
            override fun onFavoriteClicked(data: Product) {
                if (data.isFavorited) {
                    viewModel.deleteWishlist(data.url)
                    Log.d("ResultActivity", "onFavoriteClicked: delete wishlist")
                } else {
                    viewModel.insertWishlist(data)
                    Log.d("ResultActivity", "onFavoriteClicked: insert wishlist")
                }

                // Toggle the favorite state
                data.isFavorited = !data.isFavorited

                // Log the new state
                Log.d("ResultActivity", "New favorite state: ${data.isFavorited}")

                // Update the UI
                adapterCleanser.notifyItemChanged(adapterCleanser.currentList.indexOf(data))
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

        adapterCleanser.submitList(result.recommendedProducts.cleansers)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvResultCleanser.layoutManager = layoutManager

        binding.rvResultCleanser.adapter = adapterCleanser
    }

    private fun setAdapterMask() {
        adapterMask = ProductAdapter(object : ProductAdapter.OnFavoriteClickCallback {
            override fun onFavoriteClicked(data: Product) {
                if (data.isFavorited) {
                    viewModel.deleteWishlist(data.url)
                    Log.d("ResultActivity", "onFavoriteClicked: delete wishlist")
                } else {
                    viewModel.insertWishlist(data)
                    Log.d("ResultActivity", "onFavoriteClicked: insert wishlist")
                }

                // Toggle the favorite state
                data.isFavorited = !data.isFavorited

                // Log the new state
                Log.d("ResultActivity", "New favorite state: ${data.isFavorited}")

                // Update the UI
                adapterMask.notifyItemChanged(adapterMask.currentList.indexOf(data))
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

        adapterMask.submitList(result.recommendedProducts.masksAndPeels)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvResultMask.layoutManager = layoutManager

        binding.rvResultMask.adapter = adapterMask
    }

    private fun setAdapterEyecream() {
        adapterEyecream = ProductAdapter(object : ProductAdapter.OnFavoriteClickCallback {
            override fun onFavoriteClicked(data: Product) {
                if (data.isFavorited) {
                    viewModel.deleteWishlist(data.url)
                    Log.d("ResultActivity", "onFavoriteClicked: delete wishlist")
                } else {
                    viewModel.insertWishlist(data)
                    Log.d("ResultActivity", "onFavoriteClicked: insert wishlist")
                }

                // Toggle the favorite state
                data.isFavorited = !data.isFavorited

                // Log the new state
                Log.d("ResultActivity", "New favorite state: ${data.isFavorited}")

                // Update the UI
                adapterEyecream.notifyItemChanged(adapterEyecream.currentList.indexOf(data))
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

        adapterEyecream.submitList(result.recommendedProducts.eyeCreams)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvResultEyecream.layoutManager = layoutManager

        binding.rvResultEyecream.adapter = adapterEyecream
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_RESULT = "extra_result"
    }

}
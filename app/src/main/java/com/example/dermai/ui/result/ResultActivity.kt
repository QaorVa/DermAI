package com.example.dermai.ui.result

import android.content.Intent
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    }

    override fun setProcess() {
        viewModel = ViewModelProvider(this)[ResultViewModel::class.java]


    }

    override fun setObservers() {
        viewModel.getResults()?.observe(this) {
            result = it

            binding.apply {
                tvResultSkinTypeValue.text = result.type.capitalizeFirstLetter()
                resultSkinTypeDescription.text = when (result.type.lowercase()) {
                    "oily" -> getString(R.string.skin_type_oily_description_short)
                    "dry" -> getString(R.string.skin_type_dry_description_short)
                    else -> getString(R.string.skin_type_combination_description_short)
                }

                tvResultAcneLevelValue.text = result.acne
                resultAcneLevelDescription.text = when (result.acne) {
                    "Low" -> getString(R.string.acne_level_low_description_short)
                    "Moderate" -> getString(R.string.acne_level_moderate_description_short)
                    else -> getString(R.string.acne_level_severe_description_short)
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
                    getString(R.string.skin_tone_fairtolight) -> getString(R.string.skin_tone_fairtolight_description_short)
                    getString(R.string.skin_tone_lighttomedium) -> getString(R.string.skin_tone_lighttomedium_description_short)
                    else -> getString(R.string.skin_tone_mediumtodark_description_short)
                }
            }

            setAdapterMoisturizer()
            setAdapterCleanser()
            setAdapterMask()
            setAdapterEyecream()

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



    }

    private fun updateWishlist(wishlist: List<Product>?) {
        wishlist?.forEach { wishlistItem ->
            updateProductInAdapter(adapterMoisturizer, wishlistItem)
            updateProductInAdapter(adapterCleanser, wishlistItem)
            updateProductInAdapter(adapterMask, wishlistItem)
            updateProductInAdapter(adapterEyecream, wishlistItem)
        }
    }

    private fun updateProductInAdapter(adapter: ProductAdapter, product: Product) {
        val index = adapter.currentList.indexOfFirst { it.url == product.url }
        if (index != -1) {
            adapter.currentList[index].isFavorited = true
            adapter.notifyItemChanged(index)
        }
    }

    private fun setAdapterMoisturizer() {
        adapterMoisturizer = createAdapter()
        adapterMoisturizer.submitList(result.recommendedProducts.faceMoisturisers)
        binding.rvResultMoisturizer.setupRecyclerView(adapterMoisturizer)

        adapterMoisturizer.submitList(result.recommendedProducts.faceMoisturisers)
    }

    private fun setAdapterCleanser() {
        adapterCleanser = createAdapter()
        adapterCleanser.submitList(result.recommendedProducts.cleansers)
        binding.rvResultCleanser.setupRecyclerView(adapterCleanser)
    }

    private fun setAdapterMask() {
        adapterMask = createAdapter()
        adapterMask.submitList(result.recommendedProducts.masksAndPeels)
        binding.rvResultMask.setupRecyclerView(adapterMask)
    }

    private fun setAdapterEyecream() {
        adapterEyecream = createAdapter()
        adapterEyecream.submitList(result.recommendedProducts.eyeCreams)
        binding.rvResultEyecream.setupRecyclerView(adapterEyecream)

    }

    private fun createAdapter() = ProductAdapter(
        onFavoriteClickCallback = object : ProductAdapter.OnFavoriteClickCallback {
            override fun onFavoriteClicked(data: Product) {
                if (data.isFavorited) {
                    viewModel.deleteWishlist(data.url)
                } else {
                    viewModel.insertWishlist(data)
                }
                data.isFavorited = !data.isFavorited
                adapterMoisturizer.notifyItemChanged(adapterMoisturizer.currentList.indexOf(data))
            }
        },
        onLinkClickCallback = object : ProductAdapter.OnLinkClickCallback {
            override fun onLinkClicked(data: Product) {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, data.url)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(sendIntent, null))
            }
        },
        2
    )

    private fun RecyclerView.setupRecyclerView(adapter: ProductAdapter) {
        layoutManager = LinearLayoutManager(this@ResultActivity, LinearLayoutManager.HORIZONTAL, false)
        this.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
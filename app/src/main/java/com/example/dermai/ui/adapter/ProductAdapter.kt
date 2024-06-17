package com.example.dermai.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dermai.R
import com.example.dermai.data.model.Product
import com.example.dermai.databinding.ItemRecommendedProfileBinding
import com.example.dermai.databinding.ItemWishlistBinding

class ProductAdapter(private val onFavoriteClickCallback: OnFavoriteClickCallback,
                     private val onLinkClickCallback: OnLinkClickCallback,
                     private val viewTypeAdapter: Int
) : ListAdapter<Product, RecyclerView.ViewHolder>(WishlistDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewTypeAdapter) {
            VIEW_TYPE_WISHLIST -> {
                val binding = ItemWishlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                WishlistViewHolder(binding)
            }
            VIEW_TYPE_RECOMMEND -> {
                val recommendBinding = ItemRecommendedProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                RecommendViewHolder(recommendBinding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    inner class WishlistViewHolder(private val binding: ItemWishlistBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            binding.apply {
                tvItemName.text = product.name
                tvItemPrice.text = product.price
                ivSkincare.setImage(product.img)
                tvItemType.text = product.skinType
                val concerns = product.concerns.filter { it.isNotBlank() }
                tvItemConcern.text = concerns.joinToString(", ")

                ivFavorite.setImageResource(if (product.isFavorited) R.drawable.favorite_filled else R.drawable.favorite_hollow)

                ivFavorite.setOnClickListener {
                    ivFavorite.setImageResource(if (product.isFavorited) R.drawable.favorite_filled else R.drawable.favorite_hollow)
                    onFavoriteClickCallback.onFavoriteClicked(product)
                }

                ivLink.setOnClickListener {
                    onLinkClickCallback.onLinkClicked(product)
                }
            }
        }

    }

    inner class RecommendViewHolder(private val binding: ItemRecommendedProfileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                productImageView.setImage(product.img)
                productNameTextView.text = product.name
                productPriceTextView.text = product.price
                productTypeTextView.text = product.skinType
                val concerns = product.concerns.filter { it.isNotBlank() }
                productConcernTextView.text = concerns.joinToString(", ")

                ivFavorite.setImageResource(if (product.isFavorited) R.drawable.favorite_filled else R.drawable.favorite_hollow)

                ivFavorite.setOnClickListener {
                    ivFavorite.setImageResource(if (product.isFavorited) R.drawable.favorite_filled else R.drawable.favorite_hollow)
                    onFavoriteClickCallback.onFavoriteClicked(product)
                }

                ivLink.setOnClickListener {
                    onLinkClickCallback.onLinkClicked(product)
                }
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is WishlistViewHolder -> holder.bind(item)
            is RecommendViewHolder -> holder.bind(item)
        }
    }

    class WishlistDiffCallback : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.url == newItem.url
        }
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    interface OnFavoriteClickCallback {
        fun onFavoriteClicked(data: Product)
    }

    interface OnLinkClickCallback {
        fun onLinkClicked(data: Product)
    }

    private fun ImageView.setImage(url: String) {
        Glide.with(this).load(url).apply(RequestOptions()).into(this)
    }

    fun removeItem(position: Int) {
        val newList = currentList.toMutableList()
        newList.removeAt(position)
        submitList(newList)
    }

    companion object {
        private const val VIEW_TYPE_WISHLIST = 1
        private const val VIEW_TYPE_RECOMMEND = 2
    }

}


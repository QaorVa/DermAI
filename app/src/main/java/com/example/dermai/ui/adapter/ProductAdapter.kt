package com.example.dermai.ui.adapter

import android.net.Uri
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
import com.example.dermai.ui.wishlist.WishlistRepository
import java.text.NumberFormat
import java.util.Currency

class ProductAdapter(private val onFavoriteClickCallback: OnFavoriteClickCallback,
                     private val onLinkClickCallback: OnLinkClickCallback,
                     private val viewTypeAdapter: Int, private val wishlistRepository: WishlistRepository
) : ListAdapter<Product, RecyclerView.ViewHolder>(WishlistDiffCallback()) {

    inner class WishlistViewHolder(private val binding: ItemWishlistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                tvItemName.text = product.name
                tvItemPrice.text = product.price.toString().currencyFormat()
                tvItemRating.text = product.rating.toString()
                ivSkincare.setImage(product.imageUri)
                tvItemTags.text = product.tags

                val isFavorited = wishlistRepository.isProductInWishlist(product.id)
                ivFavorite.setImageResource(if (isFavorited) R.drawable.favorite_filled else R.drawable.favorite_hollow)

                ivFavorite.setOnClickListener {
                    if (isFavorited) {
                        wishlistRepository.removeProductFromWishlist(product.id)
                        ivFavorite.setImageResource(R.drawable.favorite_hollow)
                    } else {
                        wishlistRepository.addProductToWishlist(product)
                        ivFavorite.setImageResource(R.drawable.favorite_filled)
                    }
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
                productImageView.setImage(product.imageUri)
                productNameTextView.text = product.name
                productPriceTextView.text = product.price.toString().currencyFormat()

                ivFavorite.setOnClickListener {
                    onFavoriteClickCallback.onFavoriteClicked(product)
                    wishlistRepository.addProductToWishlist(product)
                    ivFavorite.setImageResource(R.drawable.favorite_filled)
                }

                ivLink.setOnClickListener {
                    onLinkClickCallback.onLinkClicked(product)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is WishlistViewHolder -> holder.bind(item)
            is RecommendViewHolder -> holder.bind(item)
        }
    }

    class WishlistDiffCallback : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
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

    private fun ImageView.setImage(uri: String) {
        Glide.with(this).load(uri).apply(RequestOptions()).into(this)
    }

    fun removeItem(position: Int) {
        val newList = currentList.toMutableList()
        newList.removeAt(position)
        submitList(newList)
    }

    private fun String.currencyFormat(): String {
        val formatter = NumberFormat.getCurrencyInstance()
        formatter.maximumFractionDigits = 0
        formatter.currency = Currency.getInstance("IDR")
        return formatter.format(this.toDouble())
    }

    companion object {
        private const val VIEW_TYPE_WISHLIST = 1
        private const val VIEW_TYPE_RECOMMEND = 2
    }

}


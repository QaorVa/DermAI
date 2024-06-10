package com.example.dermai.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dermai.databinding.ItemRecommendedProfileBinding

data class RecommendedProductItem(val name: String, val price: String, val imageResId: Int)

class RecommendedProductAdapter(private val items: List<RecommendedProductItem>) :
    RecyclerView.Adapter<RecommendedProductAdapter.RecommendedProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendedProductViewHolder {
        val binding = ItemRecommendedProfileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecommendedProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendedProductViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class RecommendedProductViewHolder(private val binding: ItemRecommendedProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecommendedProductItem) {
            binding.productNameTextView.text = item.name
            binding.productPriceTextView.text = item.price
            binding.productImageView.setImageResource(item.imageResId)
        }
    }
}

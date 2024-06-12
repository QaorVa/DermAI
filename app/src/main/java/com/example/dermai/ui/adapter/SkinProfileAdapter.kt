package com.example.dermai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dermai.databinding.ItemSkinProfileBinding

data class SkinProfileItem(val title: String, val detail: String)

class SkinProfileAdapter(private val items: List<SkinProfileItem>) :
    RecyclerView.Adapter<SkinProfileAdapter.SkinProfileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkinProfileViewHolder {
        val binding = ItemSkinProfileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SkinProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkinProfileViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class SkinProfileViewHolder(private val binding: ItemSkinProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SkinProfileItem) {
            binding.titleTextView.text = item.title
            binding.detailTextView.text = item.detail
        }
    }
}

package com.example.dermai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dermai.R
import com.example.dermai.data.model.SkinProfile
import com.example.dermai.databinding.ItemSkinProfileBinding

class SkinProfileAdapter(private val onDetailsClickCallback: OnDetailsClickCallback) : ListAdapter<SkinProfile, SkinProfileAdapter.SkinProfileViewHolder>(SkinProfileDiffCallback())  {
    interface OnDetailsClickCallback {
        fun onDetailsClicked(data: SkinProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkinProfileViewHolder {
        val binding = ItemSkinProfileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SkinProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkinProfileViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class SkinProfileViewHolder(private val binding: ItemSkinProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SkinProfile) {
            binding.titleTextView.text = item.title
            binding.detailTextView.text = item.detail
            binding.seeDetailsButton.setOnClickListener {
                onDetailsClickCallback.onDetailsClicked(item)
            }
            when(item.title){
                "Skin Type" -> binding.skinProfileImageView.setImageResource(R.drawable.skin_type_full)
                "Skin Tone" -> binding.skinProfileImageView.setImageResource(R.drawable.skin_tone_full)
                "Acne Level" -> binding.skinProfileImageView.setImageResource(R.drawable.acne_level_full)
            }
        }
    }

    class SkinProfileDiffCallback : DiffUtil.ItemCallback<SkinProfile>(){
        override fun areItemsTheSame(oldItem: SkinProfile, newItem: SkinProfile): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: SkinProfile, newItem: SkinProfile): Boolean {
            return oldItem == newItem
        }
    }
}
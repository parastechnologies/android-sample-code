package com.highenergymind.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.data.AffDay
import com.highenergymind.databinding.ItemFavAffirmationBinding
import com.highenergymind.di.ApplicationClass
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.intentComponent
import com.highenergymind.view.activity.reel.AffirmationReelActivity

class FavAffirmationAdapter :
    ListAdapter<AffDay, FavAffirmationAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<AffDay>() {
        override fun areItemsTheSame(oldItem: AffDay, newItem: AffDay): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AffDay, newItem: AffDay): Boolean {
            return oldItem == newItem
        }
    }) {
    var backImage: String? = null

    class ViewHolder(val binding: ItemFavAffirmationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                it.context.intentComponent(AffirmationReelActivity::class.java)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavAffirmationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            ivBackground.glideImage(backImage)
            tvAffirmationText.text =
                if (ApplicationClass.isEnglishSelected) item.affirmationTextEnglish else item.affirmationTextGerman
            tvCategory.text = item.categoryName
        }
    }
}
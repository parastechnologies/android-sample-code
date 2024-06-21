package com.mindbyromanzanoni.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mindbyromanzanoni.databinding.OnboardUpperviewBinding
import com.mindbyromanzanoni.view.activity.onboarding.OnBoardingActivity

class ViewPagerAdapter : androidx.recyclerview.widget.ListAdapter<OnBoardingActivity.OnBoardData, ViewPagerAdapter.ViewHolder>(diffUtil) {
    class ViewHolder(val binding: OnboardUpperviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(onBoardData: OnBoardingActivity.OnBoardData) {
            binding.title.text = onBoardData.title
            binding.description.text = onBoardData.content
            binding.image.setImageResource(onBoardData.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            OnboardUpperviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        var diffUtil = object :
            DiffUtil.ItemCallback<OnBoardingActivity.OnBoardData>() {
            override fun areItemsTheSame(
                oldItem: OnBoardingActivity.OnBoardData,
                newItem: OnBoardingActivity.OnBoardData
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: OnBoardingActivity.OnBoardData,
                newItem: OnBoardingActivity.OnBoardData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

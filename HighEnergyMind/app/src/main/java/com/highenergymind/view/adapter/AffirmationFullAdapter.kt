package com.highenergymind.view.adapter

import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.data.Affirmation
import com.highenergymind.databinding.ItemAffirmationFullBinding
import com.highenergymind.di.ApplicationClass
import com.highenergymind.utils.gone

class AffirmationFullAdapter :
    ListAdapter<Affirmation, AffirmationFullAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<Affirmation>() {
        override fun areItemsTheSame(oldItem: Affirmation, newItem: Affirmation): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Affirmation, newItem: Affirmation): Boolean {
            return oldItem == newItem
        }
    }) {

    var callBack: ((item: Affirmation?, pos: Int, type: Int) -> Unit)? = null

    class ViewHolder(val binding: ItemAffirmationFullBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAffirmationFullBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        getItem(position)?.let { item ->
            with(holder) {
                binding.tvAffirmationText.text =if (ApplicationClass.isEnglishSelected) item.affirmationTextEnglish else item.affirmationTextGerman

//                if (position == 0) {
//                    binding.layoutSwipeUp.root.visible()
//                } else {
//                    binding.layoutSwipeUp.root.gone()
//                }
            }
        }
    }
}
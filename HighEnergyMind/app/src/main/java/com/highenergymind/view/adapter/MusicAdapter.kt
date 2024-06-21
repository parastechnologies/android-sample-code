package com.highenergymind.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.data.BackAudios
import com.highenergymind.databinding.ItemsMusicLayoutsBinding
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.gone
import com.highenergymind.utils.visible

class MusicAdapter(
    val subCategoryList: List<BackAudios>,
    val sharedPrefs: SharedPrefs
) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    var callBack: ((BackAudios, Int, isPremium: Boolean) -> Unit)? = null


    inner class ViewHolder(val binding: ItemsMusicLayoutsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val item = subCategoryList[absoluteAdapterPosition]
                    if (sharedPrefs.getUserData()?.isSubscription.isNullOrEmpty() && premiuemIV.visibility == View.VISIBLE) {
                        callBack?.invoke(item, absoluteAdapterPosition, true)
                    } else {
                        callBack?.invoke(item, absoluteAdapterPosition, false)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsMusicLayoutsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return subCategoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {

            val item = subCategoryList[position]
            binding.apply {
                titleIV.text = subCategoryList[position].backgroundTitle
                musicIV.glideImage(subCategoryList[position].backTrackImg)

                binding.apply {
                    if (item.backTrackType == "premium" && sharedPrefs.getUserData()?.isSubscription.isNullOrEmpty()) {
                        premiuemIV.visible()
                    } else {
                        premiuemIV.gone()
                    }
                }

            }

        }

    }

}
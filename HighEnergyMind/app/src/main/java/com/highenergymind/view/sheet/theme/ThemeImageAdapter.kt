package com.highenergymind.view.sheet.theme

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.data.BackgroundImage
import com.highenergymind.databinding.ItemsThemeImagesLayoutBinding
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.gone
import com.highenergymind.utils.visible

class ThemeImageAdapter(
    val dataList: List<BackgroundImage>,
    private val context: Context,
    val sharedPrefs: SharedPrefs = SharedPrefs(
        context.getSharedPreferences(
            context.packageName,
            Context.MODE_PRIVATE
        )
    )
) : RecyclerView.Adapter<ThemeImageAdapter.ViewHolder>() {

    var callBack: ((BackgroundImage, isPremium: Boolean) -> Unit)? = null

    inner class ViewHolder(val binding: ItemsThemeImagesLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.apply {
                root.setOnClickListener {
                    val item = dataList[absoluteAdapterPosition]
                    if (sharedPrefs.getUserData()?.isSubscription.isNullOrEmpty() && premiuemIV.visibility == View.VISIBLE) {
                        callBack?.invoke(item, true)

                    } else {
                        callBack?.invoke(item, false)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsThemeImagesLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {

            val item = dataList[position]
            binding.imageIV.glideImage(dataList[position].backgroundThemeImg)
            binding.apply {
                if (item.backgroundThemeType == "premium" && sharedPrefs.getUserData()?.isSubscription.isNullOrEmpty()) {
                    premiuemIV.visible()
                } else {
                    premiuemIV.gone()
                }
            }

        }

    }

}
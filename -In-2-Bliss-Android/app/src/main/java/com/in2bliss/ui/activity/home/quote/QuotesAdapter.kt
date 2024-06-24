package com.in2bliss.ui.activity.home.quote

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.QuotesResponseModel
import com.in2bliss.databinding.ItemQuotesBinding
import com.in2bliss.utils.extension.glide

class QuotesAdapter(
    private val requestManager: RequestManager
) :
    PagingDataAdapter<QuotesResponseModel.Data, QuotesAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<QuotesResponseModel.Data>() {

        override fun areItemsTheSame(
            oldItem: QuotesResponseModel.Data,
            newItem: QuotesResponseModel.Data
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: QuotesResponseModel.Data,
            newItem: QuotesResponseModel.Data
        ): Boolean {
            return oldItem == newItem
        }
    }) {


    inner class ViewHolder(val binding: ItemQuotesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemQuotesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvAuthorName.text=model?.name
            tvText.text=model?.quote

            ivBg.glide(
                requestManager = requestManager,
                image = BuildConfig.QUOTE_BASE_URL.plus(model?.image),
                placeholder = R.drawable.ic_error_place_holder,
                error = R.drawable.ic_error_place_holder
            )
        }
    }
}
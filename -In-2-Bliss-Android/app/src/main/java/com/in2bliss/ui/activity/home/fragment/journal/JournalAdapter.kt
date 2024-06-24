package com.in2bliss.ui.activity.home.fragment.journal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.data.model.journalList.GuidedJournalListResponse
import com.in2bliss.databinding.ItemJournalBinding
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.visibility

class JournalAdapter : PagingDataAdapter<GuidedJournalListResponse.Data, JournalAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<GuidedJournalListResponse.Data>() {
        override fun areItemsTheSame(
            oldItem: GuidedJournalListResponse.Data,
            newItem: GuidedJournalListResponse.Data
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: GuidedJournalListResponse.Data,
            newItem: GuidedJournalListResponse.Data
        ): Boolean {
            return oldItem == newItem
        }
    }
) {

    var editListener: ((position: Int, data: GuidedJournalListResponse.Data?) -> Unit)? = null
    var openDetailScreenCallBack: ((GuidedJournalListResponse.Data) -> Unit)? = null

    class ViewHolder(val view: ItemJournalBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemJournalBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.view.view.visibility(itemCount - 1 != position)

        val model = getItem(holder.absoluteAdapterPosition)

        holder.view.apply {

            tvDescription.text = model?.description

            tvDateAndTime.text = model?.date?.let {
                formatDate(
                    date = it,
                    inputFormat = "yyyy-MM-dd",
                    outPutFormat = "EEEE, d MMM",
                    isUtc = true
                )
            }

            root.setOnClickListener {
                if (model != null) {
                    openDetailScreenCallBack?.invoke(model)
                }
            }
        }

        holder.view.ivMenu.setOnClickListener {
            editListener?.invoke(
                holder.absoluteAdapterPosition,
                getItem(holder.absoluteAdapterPosition)
            )
        }
    }
}
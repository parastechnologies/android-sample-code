package com.in2bliss.ui.activity.home.profileManagement.termAndPrivacy

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.data.model.TermsAndPrivacyList
import com.in2bliss.databinding.TermAndPrivacyCartBinding

class TermAndPrivacyAdapter : ListAdapter<TermsAndPrivacyList, TermAndPrivacyAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<TermsAndPrivacyList>() {
        override fun areItemsTheSame(oldItem: TermsAndPrivacyList, newItem: TermsAndPrivacyList): Boolean {
            return oldItem.title == newItem.title
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: TermsAndPrivacyList, newItem: TermsAndPrivacyList): Boolean {
            return oldItem == newItem
        }
    }
) {
    class ViewHolder(val view: TermAndPrivacyCartBinding) : RecyclerView.ViewHolder(view.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TermAndPrivacyCartBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvTitle.text = model.title
            tvDescription.text = model.description
        }
    }
}
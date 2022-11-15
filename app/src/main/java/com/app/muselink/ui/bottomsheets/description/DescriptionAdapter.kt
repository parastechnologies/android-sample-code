package com.app.muselink.ui.bottomsheets.description

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.model.ui.DescriptionModel
import com.app.muselink.ui.dialogfragments.MilestonesDialogFragments
import java.util.*

class DescriptionAdapter(var requireActivity: FragmentActivity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mData = ArrayList<DescriptionModel>()
    fun setData(descriptionArrayList: ArrayList<DescriptionModel>) {
        mData.clear()
        mData.addAll(descriptionArrayList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            ViewHolderHeader(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_description_header, parent, false)
            )
        } else {
            ViewHolderItems(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_description_items, parent, false)
            )
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (mData[position].type == 1) {
            1
        } else {
            0
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderItems) {
            holder.tvName.text = mData[position].name
            holder.icon.text = mData[position].icon
        } else {
            holder as ViewHolderHeader
            holder.tvName.text = mData[position].name

        }
    }

    inner class ViewHolderHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tvName)
        var ivInfo: ImageView = itemView.findViewById(R.id.ivInfo)

        init {
            ivInfo.setOnClickListener {
                val bundle = Bundle()
                if (mData[adapterPosition].name == "Milestones") {
                    bundle.putString(
                        "message",
                        requireActivity.resources.getString(R.string.milestone_text)
                    )


                } else {
                    bundle.putString(
                        "message",
                        requireActivity.resources.getString(R.string.project_roles_text)
                    )

                }
                val frag = MilestonesDialogFragments()
                frag.arguments = bundle
                requireActivity.supportFragmentManager.beginTransaction().add(frag, "Milestones")
                    .commit()
            }
        }
    }

    inner class ViewHolderItems(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: TextView = itemView.findViewById(R.id.icons)
        var tvName: TextView = itemView.findViewById(R.id.tvName)
    }

    companion object {
        private const val TYPE_ITEM = 1
    }
}
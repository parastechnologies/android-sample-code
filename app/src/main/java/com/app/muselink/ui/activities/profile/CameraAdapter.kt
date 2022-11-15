package com.app.muselink.ui.activities.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.muselink.R
import java.io.File
import java.util.*

class CameraAdapter : RecyclerView.Adapter<CameraAdapter.ViewHolder>() {
    private var allShownImagesPath = ArrayList<String?>()
    var selectedPosition = 0
    var listener: OnItemSelected? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
        var mainView: RelativeLayout = itemView.findViewById(R.id.mainView)

        init {
            mainView.setOnClickListener {
                selectedPosition = adapterPosition
                allShownImagesPath[adapterPosition]?.let { it1 -> listener?.onSelect(it1) }
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_gallery, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (allShownImagesPath[position] != null) {
            val file = File(allShownImagesPath[position]!!)
            Glide.with(holder.imageView.context).load(file).into(holder.imageView)
        }
        if (selectedPosition == position) {
            holder.mainView.setBackgroundResource(R.drawable.drawable_reactangle_purple_stroke)
        } else {
            holder.mainView.setBackgroundResource(0)
        }
    }

    override fun getItemCount(): Int {
        return allShownImagesPath.size
    }

    fun setData(allShownImagesPath: ArrayList<String>) {
        this.allShownImagesPath.clear()
        this.allShownImagesPath.addAll(allShownImagesPath)
        this.notifyDataSetChanged()

    }

    fun onItemSelected(listener: OnItemSelected) {
        this.listener = listener
    }

    interface OnItemSelected {
        fun onSelect(path: String)

    }
}
package com.app.muselink.ui.adapter.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.model.ui.ModalAudioFile
import com.app.muselink.ui.bottomsheets.SongPlayProfileBottomSheet

class AdapterSearchHistory(
    val context: FragmentActivity,
    val listSearch: ArrayList<ModalAudioFile>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    internal inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSongName = itemView.findViewById<TextView>(R.id.tvSongName)
        val tvDescription = itemView.findViewById<TextView>(R.id.tvSongSinger)
        val ivOptionMenu = itemView.findViewById<ImageView>(R.id.ivOptionMenu)

        init {
            itemView.setOnClickListener {
                SongPlayProfileBottomSheet(
                    listSearch[adapterPosition]
                ).show((context as AppCompatActivity).supportFragmentManager, "SongPlay")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.soundfile_adapter, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MyHolder
        holder.tvSongName.text = listSearch[position].fullAudio
        holder.tvDescription.text = listSearch[position].description
        holder.ivOptionMenu.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return listSearch.size
    }
}
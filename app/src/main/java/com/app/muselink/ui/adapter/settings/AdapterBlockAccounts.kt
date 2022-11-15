package com.app.muselink.ui.adapter.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.muselink.R
import com.app.muselink.model.ui.BlockAccountDetail
import com.app.muselink.util.loadProfileImage
import soup.neumorphism.NeumorphCardView

class AdapterBlockAccounts(
    val context: Context,
    var listBlockAccounts: ArrayList<BlockAccountDetail>, val adapterBlockAccountsNavigator:AdapterBlockAccountsNavigator
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName = itemView.findViewById<TextView>(R.id.tvUserName)
        val imgUser = itemView.findViewById<ImageView>(R.id.imgUser)
        val nmcBlockUnBloc = itemView.findViewById<NeumorphCardView>(R.id.nmcBlockUnBloc)
    }

    fun updateList(listBlockAccounts: ArrayList<BlockAccountDetail>) {
        this.listBlockAccounts = listBlockAccounts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_item_blocked_account, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return listBlockAccounts.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holder = (holder as AdapterBlockAccounts.MyHolder)
        holder.tvUserName.setText(listBlockAccounts[position].User_Name.toString())
        holder.imgUser.loadProfileImage("")

        holder.nmcBlockUnBloc.setOnClickListener {
            adapterBlockAccountsNavigator.onClickUnBloc(position,listBlockAccounts[position])
        }

    }

    interface AdapterBlockAccountsNavigator{
        fun onClickUnBloc(position: Int,blockAccountDetail: BlockAccountDetail)
    }

}
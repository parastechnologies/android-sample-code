package com.in2bliss.ui.activity.home.myAffirmation

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.in2bliss.data.model.AffirmationListResponse
import com.in2bliss.databinding.ItemWrittenBinding
import com.in2bliss.ui.activity.home.affirmation.affirmationList.practiceAffirmation.PracticeAllAffirmationActivity
import com.in2bliss.ui.activity.home.notification.NotificationActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.formatDate
import com.in2bliss.utils.extension.intent

class WrittenAdapter : PagingDataAdapter<AffirmationListResponse.Data, WrittenAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<AffirmationListResponse.Data>() {
        override fun areItemsTheSame(
            oldItem: AffirmationListResponse.Data,
            newItem: AffirmationListResponse.Data
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: AffirmationListResponse.Data,
            newItem: AffirmationListResponse.Data
        ): Boolean {
            return oldItem == newItem
        }
    }
) {

    var listener: ((position: Int, affirmationId: Int, data: AffirmationListResponse.Data?) -> Unit)? =
        null

    class ViewHolder(val view: ItemWrittenBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemWrittenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)
            tvDescription.text = model?.description
            ivMenu.setOnClickListener {
                listener?.invoke(holder.absoluteAdapterPosition, model?.id ?: 0, model)
            }
            tvDate.text = formatDate(
                date = model?.createdAt.orEmpty(),
                inputFormat = "yyyy-MM-dd HH:mm:ss",
                isUtc = true,
                outPutFormat = "EEEE, dd MMM hh:mm:aa"
            )

            relativeRV.setOnClickListener {
                val intent = Intent(it.context, PracticeAllAffirmationActivity::class.java)
                intent.putExtra(AppConstant.AFFIRMATION,model?.id.toString())
                it.context.startActivity(intent)
            }
        }
    }
}


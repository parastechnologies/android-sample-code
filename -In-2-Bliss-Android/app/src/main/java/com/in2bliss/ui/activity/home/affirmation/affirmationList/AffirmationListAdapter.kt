package com.in2bliss.ui.activity.home.affirmation.affirmationList

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.AffirmationListResponse
import com.in2bliss.databinding.ItemAffirmationListBinding
import com.in2bliss.ui.activity.home.affirmation.affirmationList.practiceAffirmation.PracticeAllAffirmationActivity
import com.in2bliss.utils.constants.AppConstant
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.loadSvg

class AffirmationListAdapter(
    private val requestManager: RequestManager,
    private val imageLoader: ImageLoader,
    private val imageRequest: ImageRequest.Builder
) : PagingDataAdapter<AffirmationListResponse.Data, AffirmationListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<AffirmationListResponse.Data>() {
        override fun areItemsTheSame(
            oldItem: AffirmationListResponse.Data,
            newItem: AffirmationListResponse.Data
        ): Boolean {
            return oldItem.id == newItem.id
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

    class ViewHolder(val view: ItemAffirmationListBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAffirmationListBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            val model = getItem(holder.absoluteAdapterPosition)

            tvCategory.text = model?.category?.name
            tvDescription.text = model?.description
            //    view.visibility(holder.absoluteAdapterPosition != currentList.size - 1)

            ivMenu.setOnClickListener {
                listener?.invoke(holder.absoluteAdapterPosition, model?.id ?: 0, model)
            }

            val extension = model?.category?.icon?.let {
                model.category.icon.substring(
                    it.lastIndexOf(".")
                )
            }

            if (extension == ".svg") {
                ivCategory.loadSvg(
                    imageLoader = imageLoader,
                    imageRequest = imageRequest,
                    url = BuildConfig.IMAGE_BASE_URL.plus(model.category.icon)
                )
            } else {
                model?.category?.icon?.let { image ->
                    ivCategory.glide(
                        requestManager = requestManager,
                        image = BuildConfig.IMAGE_BASE_URL.plus(image),
                        error = R.drawable.ic_error_place_holder,
                        placeholder = R.drawable.ic_error_place_holder
                    )
                }
            }

            tvDescription.setOnClickListener {
                val intent = Intent(it.context, PracticeAllAffirmationActivity::class.java)
                intent.putExtra(AppConstant.AFFIRMATION, model?.id.toString())
                it.context.startActivity(intent)
            }

        }
    }
}


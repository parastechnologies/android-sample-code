package com.in2bliss.ui.activity.auth.stepFive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.RequestManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.CategoryResponse
import com.in2bliss.databinding.ItemSelectCategoryBinding
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.loadSvg
import com.in2bliss.utils.extension.visible

class MyAffirmationAdapter(
    private val requestManager: RequestManager,
    private val imageLoader: ImageLoader,
    private val imageRequest: ImageRequest.Builder
) :
    ListAdapter<CategoryResponse.Data, MyAffirmationAdapter.ViewHolder>(
        object : DiffUtil.ItemCallback<CategoryResponse.Data>() {

            override fun areItemsTheSame(
                oldItem: CategoryResponse.Data,
                newItem: CategoryResponse.Data
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CategoryResponse.Data,
                newItem: CategoryResponse.Data
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    var addListener: ((position: Int, subCategoryList: List<CategoryResponse.Data.SubCategory>) -> Unit)? =
        null

    var isGeneral: ((String, String) -> Unit)? = null
    var addDataListener: ((position: Int, model: CategoryResponse.Data, subCategoryList: List<CategoryResponse.Data.SubCategory>) -> Unit)? =
        null

    inner class ViewHolder(val view: ItemSelectCategoryBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSelectCategoryBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {

            val model = getItem(holder.absoluteAdapterPosition)

            tvCateName.text = model.name
            model?.icon?.let { image ->
                if (image.takeLast(3) == "svg") {
                    ivIcon.loadSvg(
                        imageLoader = imageLoader,
                        imageRequest = imageRequest,
                        url = BuildConfig.IMAGE_BASE_URL.plus(image),
                        error = R.drawable.ic_error_place_holder,
                        placeholder = R.drawable.ic_error_place_holder
                    )
                } else ivIcon.glide(
                    requestManager = requestManager,
                    image = BuildConfig.IMAGE_BASE_URL.plus(image),
                    error = R.drawable.ic_error_place_holder,
                    placeholder = R.drawable.ic_error_place_holder
                )
            }


        }
    }
}
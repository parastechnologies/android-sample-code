package com.in2bliss.ui.activity.auth.stepFive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import coil.ImageLoader
import coil.request.ImageRequest
import com.bumptech.glide.RequestManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.in2bliss.BuildConfig
import com.in2bliss.R
import com.in2bliss.data.model.CategoryResponse
import com.in2bliss.databinding.ItemSelectCategoryBinding
import com.in2bliss.utils.extension.glide
import com.in2bliss.utils.extension.gone
import com.in2bliss.utils.extension.loadSvg
import com.in2bliss.utils.extension.visibility
import com.in2bliss.utils.extension.visible

class SelectSubCategoryAdapter(
    private val requestManager: RequestManager,
    private val imageLoader: ImageLoader,
    private val imageRequest: ImageRequest.Builder
) :
    ListAdapter<CategoryResponse.Data, SelectSubCategoryAdapter.ViewHolder>(
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
    var type: String? = null
    var isGeneral: ((String?, String?) -> Unit)? = null
    var addDataListener: ((position: Int, model: CategoryResponse.Data, subCategoryList: List<CategoryResponse.Data.SubCategory>) -> Unit)? =
        null
    var myAffirmation: ((String) -> Unit)? = null
    private var isCurrent = -1
    private var isPrevious = -1

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

            if (model.generalStatus == 1) ivAdd.gone() else if (type == "0") ivAdd.gone() else ivAdd.visible()

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

            if (model.selectedCategoryList == null) model.selectedCategoryList = mutableListOf()

            model.selectedCategoryList?.isNotEmpty()?.let { subcategory.visibility(it) }

            /** Setting recycler view if list is not empty */
            if (model.selectedCategoryList?.isNotEmpty() == true) {
                val layoutManager = FlexboxLayoutManager(root.context)
                layoutManager.flexDirection = FlexDirection.ROW

                val adapter = SelectSubCategoryNestedAdapter()
                rvSubCategory.layoutManager = layoutManager
                rvSubCategory.adapter = adapter
                (rvSubCategory.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                    false
                adapter.submitList(model.selectedCategoryList)

                /** Deleting subCategory */
                adapter.deleteSubCategoriesListener = { deletePosition ->
                    val currentList = adapter.currentList.toMutableList()
                    currentList.removeAt(deletePosition)
                    adapter.submitList(currentList) {
                        model.selectedCategoryList?.clear()
                        model.selectedCategoryList?.addAll(adapter.currentList)
                        if (adapter.currentList.isEmpty()) {
                            notifyItemChanged(position)
                        }
                    }
                }
            }
            clContainer.setOnClickListener {
                if (type == "0") {
                    isCurrent = holder.absoluteAdapterPosition
                    notifyItemChanged(isCurrent)
                    notifyItemChanged(isPrevious)
                    isPrevious = isCurrent
                    myAffirmation?.invoke(model.id.toString())
                } else if (model.generalStatus == 1) {
                    if (model.isChecked){
                        model.isChecked=false
                        clContainer.setBackgroundColor(
                            ContextCompat.getColor(
                                root.context,
                                R.color.grey_FAFAFF
                            )
                        )
                        tvCateName.setTextColor(
                            ContextCompat.getColor(
                                root.context,
                                R.color.prime_purple_5F46F4
                            )
                        )
                        isGeneral?.invoke(null, null)

                    }else {
                        model.isChecked=true

                        clContainer.setBackgroundColor(
                            ContextCompat.getColor(
                                holder.view.root.context,
                                R.color.purple_5a52ed
                            )
                        )
                        tvCateName.setTextColor(
                            ContextCompat.getColor(
                                holder.view.root.context,
                                R.color.white
                            )
                        )
                        isGeneral?.invoke(model.generalStatus.toString(), model.id.toString())
                    }
                } else {

                    addDataListener?.invoke(
                        holder.absoluteAdapterPosition,
                        model,
                        model.subCategory
                    )
                }
            }



            if (type=="0"){
                if (isCurrent == position) {
                    clContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.prime_purple_5F46F4
                        )
                    )
                    tvCateName.setTextColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.grey_FAFAFF
                        )
                    )
                }else{
                    clContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.grey_FAFAFF
                        )
                    )
                    tvCateName.setTextColor(
                        ContextCompat.getColor(
                            root.context,
                            R.color.prime_purple_5F46F4
                        )
                    )
                }
            }else{

                /** Setting color */
                var categoryColor = R.color.prime_purple_5F46F4
                var addButtonColor = R.color.inactive_purple_7168A6
                var backgroundColor = R.color.grey_FAFAFF

                if (model.selectedCategoryList?.isNotEmpty() == true) {
                    categoryColor = R.color.white
                    addButtonColor = R.color.white
                    backgroundColor = R.color.prime_purple_5F46F4
                }

                tvCateName.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        categoryColor
                    )
                )

                ivAdd.setColorFilter(
                    ContextCompat.getColor(
                        root.context,
                        addButtonColor
                    )
                )

                clContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        backgroundColor
                    )
                )
            }


        }

    }
}
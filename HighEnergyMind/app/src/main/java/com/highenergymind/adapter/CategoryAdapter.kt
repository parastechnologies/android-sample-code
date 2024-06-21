package com.highenergymind.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.highenergymind.R
import com.highenergymind.data.CategoriesData
import com.highenergymind.data.SubCategoryData
import com.highenergymind.databinding.ItemsEmpoweringAffirmationsBinding
import com.highenergymind.utils.SharedPrefs
import com.highenergymind.utils.glideImage
import com.highenergymind.utils.gone
import com.highenergymind.utils.visible


class CategoryAdapter(
    private val isManage: Boolean = false,
    val categoryList: MutableList<CategoriesData>, val sharedPrefs: SharedPrefs
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    var callBack: ((pos: Int, item: CategoriesData, type: Int) -> Unit)? = null
    var deleteCallBack: ((innerItem: SubCategoryData, isLastItem: Boolean) -> Unit)? =
        null

    inner class ViewHolder(val binding: ItemsEmpoweringAffirmationsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var innerAdapter: InnerItemAdapter? = null

        init {
            binding.apply {
                rlContainer.setOnClickListener {
//                    if (sharedPrefs.getUserData()?.isSubscription.isNullOrEmpty().not()) {
                    val item = categoryList[absoluteAdapterPosition]
                    callBack?.invoke(absoluteAdapterPosition, item, R.id.addIV)
//                    }
                }

                addIV.setOnClickListener {
//                    if (sharedPrefs.getUserData()?.isSubscription.isNullOrEmpty().not()) {
                    val item = categoryList[absoluteAdapterPosition]
                    callBack?.invoke(absoluteAdapterPosition, item, R.id.addIV)
//                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemsEmpoweringAffirmationsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = categoryList[position]
        with(holder.binding) {
            categoryNameTV.text = item.categoryName
//            if (sharedPrefs.getUserData()?.isSubscription.isNullOrEmpty()) {
//                addIV.setImageResource(R.drawable.premiuem_ic)
//            } else {
            addIV.setImageResource(R.drawable.add_circle_ic)
//            }
            if (item.isChecked || isManage) {
                categoryIC.glideImage(item.categoryImgActivePath, R.drawable.hem_picture_logo)


                val selectedColor = ContextCompat.getColorStateList(
                    root.context, R.color.content_color
                )
                categoryNameTV.setTextColor(
                    selectedColor
                )
                rlContainer.setBackgroundResource(R.drawable.drawable_checked_gradient_content)
                if (!item.subCategoryList.isNullOrEmpty()) {
                    holder.innerAdapter = InnerItemAdapter(item.subCategoryList!!)
                    holder.innerAdapter?.deleteCallBack = { subDel ->
                        categoryList[holder.absoluteAdapterPosition].allSubCategoryList?.find { it.id == subDel.id }?.isChecked =
                            false
                        if (holder.innerAdapter?.subCategoryList.isNullOrEmpty()) {

                            if (isManage) {
                                categoryList.removeAt(holder.absoluteAdapterPosition)
                                notifyItemRemoved(holder.absoluteAdapterPosition)
                            } else {
                                categoryList[holder.absoluteAdapterPosition].isChecked = false
                                notifyItemChanged(holder.absoluteAdapterPosition)
                            }
                            deleteCallBack?.invoke(subDel, true)
                        } else {
                            deleteCallBack?.invoke(subDel, false)
                        }
                    }
                    subcategoryRV.adapter = holder.innerAdapter
                    viewV.visible()
                    subcategoryRV.visible()
                } else {
                    viewV.gone()
                    subcategoryRV.gone()
                }
            } else {
                categoryIC.glideImage(item.categoryImg, R.drawable.hem_picture_logo)

                val unSelectedColor = ContextCompat.getColorStateList(
                    root.context, R.color.unselected_text_color
                )
                categoryNameTV.setTextColor(
                    unSelectedColor
                )
                rlContainer.setBackgroundResource(R.drawable.drawable_un_checked_category_affirmation)
                viewV.gone()
                subcategoryRV.gone()
            }


        }

    }

}
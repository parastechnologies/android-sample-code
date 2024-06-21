package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by developer on 05/03/24
 */
data class GetCategoriesResponse(
    @SerializedName("data")
    val `data`: MutableList<CategoriesData>,
) : BaseResponse()

data class CategoriesData(
    @SerializedName("category_img_path")
    val categoryImg: String,
    @SerializedName("category_img_active_path")
    val categoryImgActivePath: String?,
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("category_status")
    val categoryStatus: String? = "",
    @SerializedName("id")
    val id: Int,
    @SerializedName("subcategories")
    var subCategoryList: MutableList<SubCategoryData>? = null,
    var allSubCategoryList: MutableList<SubCategoryData>? = null,
    var isChecked: Boolean = false
)
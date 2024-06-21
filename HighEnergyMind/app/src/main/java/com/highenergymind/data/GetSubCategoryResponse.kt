package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by developer on 06/03/24
 */
data class GetSubCategoryResponse(

    @SerializedName("data")
    val `data`: List<SubCategoryData>

) : BaseResponse()

data class SubCategoryData(
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("sub_category_img", alternate = ["sub_category_img_path"])
    val subCategoryImg: String,
    @SerializedName("sub_category_name")
    val subCategoryName: String,
    var isChecked: Boolean = false
)
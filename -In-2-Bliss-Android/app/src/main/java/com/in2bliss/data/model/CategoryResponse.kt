package com.in2bliss.data.model

import com.google.gson.annotations.SerializedName
import com.in2bliss.base.BaseResponse

data class CategoryResponse(
    @SerializedName("data")
    val `data`: List<Data>
) : BaseResponse() {
    data class Data(
        @SerializedName("icon")
        val icon: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("generalStatus")
        var generalStatus: Int? = null,
        @SerializedName("subCategory")
        val subCategory: List<SubCategory>,
        var selectedCategoryList: MutableList<SelectedCategory>? = mutableListOf(),
        var isChecked: Boolean = false
    ) {
        data class SubCategory(
            @SerializedName("CID")
            val cID: Int?,
            @SerializedName("icon")
            val icon: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("name")
            val name: String?,
            var isSelected: Boolean = false
        )
    }
}
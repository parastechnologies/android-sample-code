package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by developer on 23/04/24
 */
data class BackgroundThemeResponse(

    @SerializedName("data")
    val `data`: List<ThemeData>

) : BaseResponse()

data class ThemeData(
    @SerializedName("backgroundImages")
    val backgroundImages: List<BackgroundImage>,
    @SerializedName("background_theme_category_name")
    val backgroundThemeCategoryName: String,
    @SerializedName("id")
    val id: Int
)

data class BackgroundImage(
    @SerializedName("background_theme_category_id")
    val backgroundThemeCategoryId: Int,
    @SerializedName("background_theme_img")
    val backgroundThemeImg: String,
    @SerializedName("background_theme_type")
    val backgroundThemeType: String,
    @SerializedName("only_img")
    val onlyImage: String,
    @SerializedName("id")
    val id: Int
)
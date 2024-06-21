package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by developer on 17/04/24
 */
data class GetMusicResponse(
    @SerializedName("data")
    val `data`: List<MusicData>
) : BaseResponse()

data class MusicData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("background_music_category_name")
    val backgroundCategoryName: String,
    @SerializedName("backgroundAudios")
    val backgroundAudios: List<BackAudios>
)

data class BackAudios(
    @SerializedName("background_title")
    val backgroundTitle: String,
    @SerializedName("background_audio")
    val backAudio: String,
    @SerializedName("background_category_id")
    val backTrackCatId: Int,
    @SerializedName("background_desc")
    val backTrackDesc: String,
    @SerializedName("background_img")
    val backTrackImg: String,
    @SerializedName("background_type")
    val backTrackType: String,
    @SerializedName("id")
    val id: Int
)
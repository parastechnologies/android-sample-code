package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by developer on 20/03/24
 */
data class HomeDashboardResponse(
    @SerializedName("data")
    val `data`: HomeData
) : BaseResponse()

data class HomeData(
    @SerializedName("background_affirmation_img")
    val backgroundAffImg:String?=null,
    @SerializedName("affDay")
    val affDay: AffDay?,
    @SerializedName("lastTrack")
    val lastTrack: List<TrackOb>,
    @SerializedName("playlist")
    val playlist: List<TrackOb>,
    @SerializedName("popular")
    val popular: List<TrackOb>,
    @SerializedName("recommendation")
    val recommendation: List<TrackOb>
)

data class AffDay(
    @SerializedName("affirmation_id")
    val affirmationId: String,
    @SerializedName("affirmation_text_english")
    val affirmationTextEnglish: String,
    @SerializedName("affirmation_text_german")
    val affirmationTextGerman: String,
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("created_at")
    var createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_favourite")
    var isFav: Int? = 0,
    @SerializedName("today_date")
    var todayDate: String? = null,
    @SerializedName("background_track_music")
    val backgroundTrackMusic: String? = null,
    @SerializedName("background_track_img")
    val backgroundTrackImg: String? = null,
    @SerializedName("track_id")
    val trackId: String? = null
)

data class TrackOb(
    @SerializedName("background_img_id")
    val backgroundImgId: String?,
    @SerializedName("track_thumbnail")
    val trackThumbnail:String?=null,
    @SerializedName("background_music_id")
    val backgroundMusicId: Any,
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("track_desc")
    val trackDesc: String,
    @SerializedName("track_title")
    val trackTitle: String,
    @SerializedName("is_favourite")
    var isFav: Int? = 0,
    @SerializedName("background_track_img")
    val backgroundTrackImage: String? = null,
    @SerializedName("background_track_music")
    val backgroundTrackMusic: String? = null,
    @SerializedName("total_track_duration")
    val totalTrackDuration: Long? = null,

    /**  by default @affirmationList variable getting null
     * we will get this value from other api and assign to this variable after that
     * **/

    var affirmationList: List<AffirmationData>? = null
)


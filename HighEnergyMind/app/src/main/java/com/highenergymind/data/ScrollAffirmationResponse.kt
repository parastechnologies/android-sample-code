package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by developer on 24/04/24
 */
data class ScrollAffirmationResponse(

    val `data`: ScrollAffData,

    ) : BaseResponse()

data class ScrollAffData(
    @SerializedName("affirmationList")
    val affirmationList: List<Affirmation>,
    @SerializedName("background_affirmation_img")
    val backgroundAffirmationImg: String
)

data class Affirmation(
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
    var isFavourite: Int,
    @SerializedName("speakerAudios")
    val speakerAudios: List<SpeakerAudio>
)

data class SpeakerAudio(
    @SerializedName("aff_duration_english")
    val affDurationEnglish: String,
    @SerializedName("aff_duration_german")
    val affDurationGerman: String,

    @SerializedName("aff_english")
    val affEnglish: String,
    @SerializedName("aff_german")
    val affGerman: String,
    @SerializedName("aff_id")
    val affId: Int,
    @SerializedName("id")
    val id: Int
)
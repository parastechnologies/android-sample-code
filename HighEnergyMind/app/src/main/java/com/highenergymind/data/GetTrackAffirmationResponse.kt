package com.highenergymind.data

import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by developer on 04/04/24
 */
data class GetTrackAffirmationResponse(

    @SerializedName("data")
    val `data`: List<AffirmationData>,
    @SerializedName("trackDetails")
    val trackDetails: TrackOb?

) : BaseResponse()

data class AffirmationData(
    @SerializedName("affirmation_id")
    val affirmationId: String,
    @SerializedName("affirmation_text_english")
    val affirmationTextEnglish: String,
    @SerializedName("affirmation_text_german")
    val affirmationTextGerman: String,
    @SerializedName("audioFiles")
    val audioFiles: List<AudioFile>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int
)

data class AudioFile(
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
    val id: Int,
    @SerializedName("speaker_name")
    val speakerName: String,
    @SerializedName("speaker_img")
    val speakerImg: String
)
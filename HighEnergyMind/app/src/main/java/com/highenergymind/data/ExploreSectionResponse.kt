package com.highenergymind.data
import com.google.gson.annotations.SerializedName
import com.highenergymind.base.BaseResponse


/**
 * Created by developer on 08/04/24
 */
data class ExploreSectionResponse(
    @SerializedName("data")
    val `data`: ExploreSectionData
):BaseResponse()

data class ExploreSectionData(
    @SerializedName("curated")
    val curated: List<TrackOb>,
    @SerializedName("popular")
    val popular: List<TrackOb>
)

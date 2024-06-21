package com.mindbyromanzanoni.data.response.allCategoryDetail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data {
    @SerializedName("createdOn")
    @Expose
    var createdOn: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("content")
    @Expose
    var content: String? = null

    @SerializedName("videoName")
    @Expose
    var videoName: String? = null

    @SerializedName("type")
    @Expose
    var type: Int? = null

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("videoThumbImage")
    @Expose
    var videoThumbImage: String? = null

    @SerializedName("fileName")
    @Expose
    var fileName: String? = null
    @SerializedName("videoHlsLink")
    @Expose
    var videoHlsLink: String? = null
}

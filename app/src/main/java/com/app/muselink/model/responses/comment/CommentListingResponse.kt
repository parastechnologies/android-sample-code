package com.app.muselink.data.modals.responses.comment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.app.muselink.data.modals.responses.BaseResponseComment

class CommentListingResponse : BaseResponseComment() {

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<CommentResponseData>? = null
}
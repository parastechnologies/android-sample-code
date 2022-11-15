package com.app.muselink.data.modals.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UploadAudioResponseModel {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: UploadAudioData? = null
}
class UploadAudioData {
    @SerializedName("Audio_Id")
    @Expose
    var audioId: String? = null

    @SerializedName("User_Id")
    @Expose
    var userId: String? = null

    @SerializedName("Full_Audio")
    @Expose
    var fullAudio: String? = null

    @SerializedName("Trim_Audio")
    @Expose
    var trimAudio: String? = null

    @SerializedName("Description")
    @Expose
    var description: String? = null

    @SerializedName("Description_Color")
    @Expose
    var descriptionColor: String? = null

    @SerializedName("Audio_Date")
    @Expose
    var audioDate: String? = null
}
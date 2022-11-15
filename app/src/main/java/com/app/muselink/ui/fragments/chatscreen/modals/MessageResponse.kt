package com.app.muselink.ui.fragments.chatscreen.modals

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MessageResponse : BaseResponseChat(){

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<ModalMessage>? = null

}
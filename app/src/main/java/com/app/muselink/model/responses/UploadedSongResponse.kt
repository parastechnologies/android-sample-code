package com.app.muselink.model.responses

import com.app.muselink.model.BaseResponse
import com.app.muselink.model.ui.ModalAudioFile

class UploadedSongResponse : BaseResponse(){
    var subscriptionStatus: Int = 0
    var DMCount: Int = 0
    var likeCount: Int = 0
    var data : ArrayList<ModalAudioFile>? = null

}
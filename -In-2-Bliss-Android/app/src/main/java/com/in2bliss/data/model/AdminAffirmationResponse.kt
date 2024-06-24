package com.in2bliss.data.model

data class AdminAffirmationResponse(
    val `data`: Data?,
    val message: String?,
    val status: Int?
) {
    data class Data(
        val CID: Int?,
        val SCID: Int?,
        val UID: Any?,
        val affirmation: Any?,
        val audio: Any?,
        val audioName: Any?,
        val audioType: Any?,
        val background: Any?,
        val cIcon: String?,
        val cName: String?,
        val createdBy: Int?,
        val created_at: String?,
        val description: String?,
        val duration: Any?,
        val favouriteStatus: Int?,
        val id: Int?,
        val introAffirmation: Any?,
        val liveStatus: Int?,
        val sIcon: String?,
        val sName: String?,
        val scheduleTime: Any?,
        val status: Int?,
        val thumbnail: Any?,
        val title: Any?,
        val transcript: Any?,
        val type: Int?,
        val updated_at: String?
    )
}
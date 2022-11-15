package com.example.commonresources

import java.io.Serializable

data class ModalAudioFile(
    val Audio_Id: String? = null,
    val User_Id: String? = null,
    val Full_Audio: String? = null,
    val Trim_Audio: String? = null,
    val Description: String? = null,
    val Description_Color: String? = null,
    val Audio_Date: String? = null
): Serializable
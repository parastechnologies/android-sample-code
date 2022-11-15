package com.app.muselink.model.ui

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ModalAudioFile(

	@field:SerializedName("Audio_Id")
	var audioId: String? = null,

	@field:SerializedName("Audio_Date")
	val audioDate: String? = null,

	@field:SerializedName("Description")
	val description: String? = null,

	@field:SerializedName("User_Name")
	val userName: String? = null,

	@field:SerializedName("Full_Audio")
	val fullAudio: String? = null,

	@field:SerializedName("Notification_Status")
	var Notification_Status: String? = null,

	@field:SerializedName("Trim_Audio")
	val trimAudio: String? = null,

	@field:SerializedName("User_Id")
	val userId: String? = null,

	@field:SerializedName("favoriteAudio")
	var favoriteAudio: Int? = null,

	@field:SerializedName("Description_Color")
	val descriptionColor: String? = null
) :Serializable

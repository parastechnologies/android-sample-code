package com.app.muselink.ui.fragments.chatscreen.modals

import java.io.Serializable

data class RecentChatDetails(
	val senderName: Any? = null,
	val sender_profile_picture: Any? = null,
	val created_on: String? = null,
	val receiverName: Any? = null,
	val receiver_id: String? = null,
	val receiver_profile_picture: Any? = null,
	val message: String? = null,
	val sender_id: String? = null,
	val hash: String? = null,
	val onlineStatus: String? = null
):Serializable


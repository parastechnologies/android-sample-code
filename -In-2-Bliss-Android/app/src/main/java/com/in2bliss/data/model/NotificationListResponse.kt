package com.in2bliss.data.model

data class NotificationListResponse(
    val message: String?,
    val read: List<Read>,
    val status: Int?,
    val unread: List<Unread>
) {
    data class Read(
        val created_at: String?,
        val date: String?,
        val id: Int?,
        val message: String?,
        val status: Int?,
        val title: String?,
        val type: Int?,
        val updated_at: String?,
        val userID: Int?
    )

    data class Unread(
        val created_at: String?,
        val date: String?,
        val id: Int?,
        val message: String?,
        val status: Int?,
        val title: String?,
        val type: Int?,
        val updated_at: Any?,
        val userID: Int?
    )
}
package com.in2bliss.data.model

data class GetNotificationStatusResponse(
    val affirmation: Affirmation? = null,
    val journal: Journal? = null,
    val message: String? = null,
    val quote: Quote? = null,
    val status: Int? = null
) {
    data class Affirmation(
        val affirmationStatus: Int?,
        val reminder: Reminder?
    ) {
        data class Reminder(
            val UID: Int?,
            val created_at: String?,
            val days: String?,
            val endTime: String?,
            val id: Int?,
            val interval: Int?,
            val startTime: String?,
            val status: Int?,
            val type: Int?,
            val updated_at: String?
        )
    }

    data class Journal(
        val notificationStatus: Int?,
        val reminder: Reminder?,
        val gratitude: Gratitude?
    ) {
        data class Reminder(
            val UID: Int?,
            val created_at: String?,
            val days: String?,
            val endTime: String?,
            val id: Int?,
            val interval: Int?,
            val startTime: String?,
            val status: Int?,
            val type: Int?,
            val updated_at: String?
        )

        data class Gratitude(
            val UID: Int? = 0,
            val created_at: String? = "",
            val days: String? = "",
            val endTime: String? = "",
            val id: Int? = 0,
            val interval: Int? = 0,
            val startTime: String? = "",
            val status: Int? = 0,
            val type: Int? = 0,
            val updated_at: String? = ""
        )
    }

    data class Quote(
        val affirmationStatus: Int?,
        val reminder: Reminder?
    ) {
        data class Reminder(
            val UID: Int? = 0,
            val created_at: String? = "",
            val days: String? = "",
            val endTime: String? = "",
            val id: Int? = 0,
            val interval: Int? = 0,
            val startTime: String? = "",
            val status: Int? = 0,
            val type: Int? = 0,
            val updated_at: String? = ""
        )
    }
}

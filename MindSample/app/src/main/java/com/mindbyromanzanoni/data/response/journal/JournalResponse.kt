package com.mindbyromanzanoni.data.response.journal

import com.mindbyromanzanoni.base.BaseResponse
import java.io.Serializable

data class JournalResponse(
    val `data`: ArrayList<JournalListResponse>,
) :BaseResponse()

data class JournalListResponse(
    val createdOn: String,
    val journalId: Int,
    val notes: String,
    val subject: String,
    val type: String,
    val typeId: Int,
    val userId: Int,
    val journalDate : String
)
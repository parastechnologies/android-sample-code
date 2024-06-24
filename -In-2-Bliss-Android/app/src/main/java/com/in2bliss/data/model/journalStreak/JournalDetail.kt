package com.in2bliss.data.model.journalStreak

data class JournalDetail(
    val description: String,
    val backgroundImage: String,
    val id: String,
    val date : String,
    val categoryIcon : String? = null,
    val categoryName : String? = null,
)

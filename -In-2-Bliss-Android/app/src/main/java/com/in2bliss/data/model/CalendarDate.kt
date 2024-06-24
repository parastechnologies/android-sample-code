package com.in2bliss.data.model

data class CalendarDate(
    var date: Int? = null,
    val year: Int? = null,
    val month: Int? = null,
    val weekOfDay: String? = null,
    val isEmpty: Boolean = false,
    var isEvent: Boolean = false,
    var data: String? = null
)

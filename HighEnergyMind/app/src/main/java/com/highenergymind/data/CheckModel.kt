package com.highenergymind.data

data class CheckModel(
    val name: String,
    var isChecked: Boolean = false,
    val id: String? = null
)

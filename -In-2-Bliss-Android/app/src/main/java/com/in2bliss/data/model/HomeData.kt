package com.in2bliss.data.model

data class HomeData(
    val name: String,
    val dataList: List<MusicDescription>
) {
    data class MusicDescription(
        val title: String,
        val description: String? = null,
        val image: Int? = null,
        val isFav: Boolean = false
    )
}


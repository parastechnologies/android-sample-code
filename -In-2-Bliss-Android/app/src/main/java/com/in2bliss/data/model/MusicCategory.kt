package com.in2bliss.data.model

data class MusicCategory(
    val name: String,
    val categoryList: List<CategoryList>
) {
    data class CategoryList(
        val title: String,
        val image: Int
    )
}

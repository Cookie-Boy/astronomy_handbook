package com.sibsutis.astronomyhandbook.model

data class News(
    val id: Int,
    val title: String,
    val content: String,
    var likes: Int = 0
)
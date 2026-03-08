package com.sibsutis.astronomyhandbook.opengl.model

data class Planet(
    val name: String,
    val radius: Float,
    val orbitRadius: Float,
    val speed: Float,
    val color: FloatArray,
    var angle: Float = 0f
)
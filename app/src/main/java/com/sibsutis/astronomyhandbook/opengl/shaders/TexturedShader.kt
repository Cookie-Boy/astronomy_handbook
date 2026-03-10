package com.sibsutis.astronomyhandbook.opengl.shaders

interface TexturedShader {
    fun getPositionAttrib(): Int
    fun getNormalAttrib(): Int
    fun getTexCoordAttrib(): Int
}
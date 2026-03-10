package com.sibsutis.astronomyhandbook.opengl.shaders

import android.opengl.GLES20

class BackgroundShader {
    private var programId: Int = 0
    private var uMVPMatrix: Int = 0
    private var uTexture: Int = 0
    private var aPosition: Int = 0
    private var aTexCoord: Int = 0

    init {
        compileProgram()
    }

    private fun compileProgram() {
        val vertexSource = """
            uniform mat4 uMVPMatrix;
            attribute vec4 aPosition;
            attribute vec2 aTexCoord;
            varying vec2 vTexCoord;
            void main() {
                vTexCoord = aTexCoord;
                gl_Position = uMVPMatrix * aPosition;
            }
        """.trimIndent()

        val fragmentSource = """
            precision mediump float;
            varying vec2 vTexCoord;
            uniform sampler2D uTexture;
            void main() {
                gl_FragColor = texture2D(uTexture, vTexCoord);
            }
        """.trimIndent()

        programId = ShaderUtils.createProgram(vertexSource, fragmentSource)
        uMVPMatrix = GLES20.glGetUniformLocation(programId, "uMVPMatrix")
        uTexture = GLES20.glGetUniformLocation(programId, "uTexture")
        aPosition = GLES20.glGetAttribLocation(programId, "aPosition")
        aTexCoord = GLES20.glGetAttribLocation(programId, "aTexCoord")
    }

    fun use() = GLES20.glUseProgram(programId)

    fun setUniforms(mvpMatrix: FloatArray, textureId: Int) {
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(uTexture, 0)
    }

    fun getPositionAttrib() = aPosition
    fun getTexCoordAttrib() = aTexCoord
}
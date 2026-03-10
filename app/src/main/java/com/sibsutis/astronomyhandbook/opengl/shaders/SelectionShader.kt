package com.sibsutis.astronomyhandbook.opengl.shaders

import android.opengl.GLES20

class SelectionShader {
    private var programId: Int = 0
    private var uMVPMatrix: Int = 0
    private var uColor: Int = 0
    private var aPosition: Int = 0

    init {
        compileProgram()
    }

    private fun compileProgram() {
        val vertexSource = """
            uniform mat4 uMVPMatrix;
            attribute vec4 aPosition;
            void main() {
                gl_Position = uMVPMatrix * aPosition;
            }
        """.trimIndent()

        val fragmentSource = """
            precision mediump float;
            uniform vec4 uColor;
            void main() {
                gl_FragColor = uColor;
            }
        """.trimIndent()

        programId = ShaderUtils.createProgram(vertexSource, fragmentSource)
        uMVPMatrix = GLES20.glGetUniformLocation(programId, "uMVPMatrix")
        uColor = GLES20.glGetUniformLocation(programId, "uColor")
        aPosition = GLES20.glGetAttribLocation(programId, "aPosition")
    }

    fun use() = GLES20.glUseProgram(programId)

    fun setUniforms(mvpMatrix: FloatArray, color: FloatArray) {
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0)
        GLES20.glUniform4fv(uColor, 1, color, 0)
    }

    fun getPositionAttrib() = aPosition
}
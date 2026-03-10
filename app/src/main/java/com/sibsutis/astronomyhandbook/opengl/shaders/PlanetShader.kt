package com.sibsutis.astronomyhandbook.opengl.shaders

import android.opengl.GLES20

class PlanetShader : TexturedShader {
    private var programId: Int = 0

    // Uniform locations
    private var uMVMatrix: Int = 0
    private var uPMatrix: Int = 0
    private var uNMatrix: Int = 0
    private var uLightPosition: Int = 0
    private var uAmbient: Int = 0
    private var uDiffuse: Int = 0
    private var uEmission: Int = 0
    private var uTexture: Int = 0
    private var uUseTexture: Int = 0

    // Attribute locations
    private var aPosition: Int = 0
    private var aNormal: Int = 0
    private var aTexCoord: Int = 0

    init {
        compileProgram()
    }

    private fun compileProgram() {
        val vertexSource = """
            uniform mat4 uMVMatrix;
            uniform mat4 uPMatrix;
            uniform mat3 uNMatrix;
            attribute vec4 aPosition;
            attribute vec3 aNormal;
            attribute vec2 aTexCoord;
            varying vec3 vPosition;
            varying vec3 vNormal;
            varying vec2 vTexCoord;
            void main() {
                vPosition = vec3(uMVMatrix * aPosition);
                vNormal = normalize(uNMatrix * aNormal);
                vTexCoord = aTexCoord;
                gl_Position = uPMatrix * uMVMatrix * aPosition;
            }
        """.trimIndent()

        val fragmentSource = """
            precision mediump float;
            varying vec3 vPosition;
            varying vec3 vNormal;
            varying vec2 vTexCoord;
            uniform vec3 uLightPosition;
            uniform vec4 uAmbient;
            uniform vec4 uDiffuse;
            uniform vec4 uEmission;
            uniform sampler2D uTexture;
            uniform bool uUseTexture;
            void main() {
                vec3 normal = normalize(vNormal);
                vec3 lightDir = normalize(uLightPosition - vPosition);
                float diff = max(dot(normal, lightDir), 0.0);
                vec4 diffuse = diff * uDiffuse;
                vec4 ambient = uAmbient;
                vec4 emission = uEmission;
                if (uUseTexture) {
                    vec4 texColor = texture2D(uTexture, vTexCoord);
                    gl_FragColor = (ambient + diffuse + emission) * texColor;
                } else {
                    gl_FragColor = ambient + diffuse + emission;
                }
            }
        """.trimIndent()

        programId = ShaderUtils.createProgram(vertexSource, fragmentSource)
        if (programId == 0) throw RuntimeException("Failed to create planet shader")

        uMVMatrix = GLES20.glGetUniformLocation(programId, "uMVMatrix")
        uPMatrix = GLES20.glGetUniformLocation(programId, "uPMatrix")
        uNMatrix = GLES20.glGetUniformLocation(programId, "uNMatrix")
        uLightPosition = GLES20.glGetUniformLocation(programId, "uLightPosition")
        uAmbient = GLES20.glGetUniformLocation(programId, "uAmbient")
        uDiffuse = GLES20.glGetUniformLocation(programId, "uDiffuse")
        uEmission = GLES20.glGetUniformLocation(programId, "uEmission")
        uTexture = GLES20.glGetUniformLocation(programId, "uTexture")
        uUseTexture = GLES20.glGetUniformLocation(programId, "uUseTexture")

        aPosition = GLES20.glGetAttribLocation(programId, "aPosition")
        aNormal = GLES20.glGetAttribLocation(programId, "aNormal")
        aTexCoord = GLES20.glGetAttribLocation(programId, "aTexCoord")
    }

    fun use() {
        GLES20.glUseProgram(programId)
    }

    fun setUniforms(mvpMatrix: FloatArray, mvMatrix: FloatArray, normalMatrix: FloatArray,
                    lightPos: FloatArray, ambient: FloatArray, diffuse: FloatArray,
                    emission: FloatArray, useTexture: Boolean, textureId: Int = 0) {
        GLES20.glUniformMatrix4fv(uMVMatrix, 1, false, mvMatrix, 0)
        GLES20.glUniformMatrix4fv(uPMatrix, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix3fv(uNMatrix, 1, false, normalMatrix, 0)
        GLES20.glUniform3fv(uLightPosition, 1, lightPos, 0)
        GLES20.glUniform4fv(uAmbient, 1, ambient, 0)
        GLES20.glUniform4fv(uDiffuse, 1, diffuse, 0)
        GLES20.glUniform4fv(uEmission, 1, emission, 0)
        GLES20.glUniform1i(uUseTexture, if (useTexture) 1 else 0)
        if (useTexture) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glUniform1i(uTexture, 0)
        }
    }

    override fun getPositionAttrib() = aPosition
    override fun getNormalAttrib() = aNormal
    override fun getTexCoordAttrib() = aTexCoord
}
package com.sibsutis.astronomyhandbook.opengl.shaders

import android.opengl.GLES20

class ObjectShader : TexturedShader {
    private var programId: Int = 0

    // Uniform locations
    private var uMVPMatrix: Int = 0
    private var uMVMatrix: Int = 0
    private var uNMatrix: Int = 0
    private var uLightPos: Int = 0
    private var uAmbient: Int = 0
    private var uDiffuse: Int = 0
    private var uSpecular: Int = 0
    private var uShininess: Int = 0
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
                uniform mat4 uMVPMatrix;
                uniform mat4 uMVMatrix;
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
                    gl_Position = uMVPMatrix * aPosition;
                }
            """.trimIndent()

        val fragmentSource = """
                precision mediump float;
                varying vec3 vPosition;
                varying vec3 vNormal;
                varying vec2 vTexCoord;
                uniform vec3 uLightPos;
                uniform vec4 uAmbient;
                uniform vec4 uDiffuse;
                uniform vec4 uSpecular;
                uniform float uShininess;
                uniform sampler2D uTexture;
                uniform bool uUseTexture;
                void main() {
                    vec3 normal = normalize(vNormal);
                    vec3 lightDir = normalize(uLightPos - vPosition);
                    vec3 viewDir = normalize(-vPosition);
                    vec3 reflectDir = reflect(-lightDir, normal);
                    
                    // Фоновое
                    vec4 ambient = uAmbient;
                    
                    // Диффузное
                    float diff = max(dot(normal, lightDir), 0.0);
                    vec4 diffuse = diff * uDiffuse;
                    
                    // Блики (зеркальное)
                    float spec = 0.0;
                    if (diff > 0.0) {
                        spec = pow(max(dot(viewDir, reflectDir), 0.0), uShininess);
                    }
                    vec4 specular = spec * uSpecular;
                    
                    vec4 baseColor = ambient + diffuse + specular;
                    
                    if (uUseTexture) {
                        vec4 texColor = texture2D(uTexture, vTexCoord);
                        gl_FragColor = baseColor * texColor;
                    } else {
                        gl_FragColor = baseColor;
                    }
                }
            """.trimIndent()

        programId = ShaderUtils.createProgram(vertexSource, fragmentSource)
        if (programId == 0) throw RuntimeException("Failed to create object shader")

        uMVPMatrix = GLES20.glGetUniformLocation(programId, "uMVPMatrix")
        uMVMatrix = GLES20.glGetUniformLocation(programId, "uMVMatrix")
        uNMatrix = GLES20.glGetUniformLocation(programId, "uNMatrix")
        uLightPos = GLES20.glGetUniformLocation(programId, "uLightPos")
        uAmbient = GLES20.glGetUniformLocation(programId, "uAmbient")
        uDiffuse = GLES20.glGetUniformLocation(programId, "uDiffuse")
        uSpecular = GLES20.glGetUniformLocation(programId, "uSpecular")
        uShininess = GLES20.glGetUniformLocation(programId, "uShininess")
        uTexture = GLES20.glGetUniformLocation(programId, "uTexture")
        uUseTexture = GLES20.glGetUniformLocation(programId, "uUseTexture")

        aPosition = GLES20.glGetAttribLocation(programId, "aPosition")
        aNormal = GLES20.glGetAttribLocation(programId, "aNormal")
        aTexCoord = GLES20.glGetAttribLocation(programId, "aTexCoord")
    }

    fun use() {
        GLES20.glUseProgram(programId)
    }

    fun setUniforms(
        mvpMatrix: FloatArray,
        mvMatrix: FloatArray,
        normalMatrix: FloatArray,
        lightPos: FloatArray,
        ambient: FloatArray,
        diffuse: FloatArray,
        specular: FloatArray,
        shininess: Float,
        useTexture: Boolean,
        textureId: Int
    ) {
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(uMVMatrix, 1, false, mvMatrix, 0)
        GLES20.glUniformMatrix3fv(uNMatrix, 1, false, normalMatrix, 0)
        GLES20.glUniform3fv(uLightPos, 1, lightPos, 0)
        GLES20.glUniform4fv(uAmbient, 1, ambient, 0)
        GLES20.glUniform4fv(uDiffuse, 1, diffuse, 0)
        GLES20.glUniform4fv(uSpecular, 1, specular, 0)
        GLES20.glUniform1f(uShininess, shininess)
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
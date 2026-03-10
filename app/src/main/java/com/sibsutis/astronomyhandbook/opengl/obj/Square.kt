// Square.kt
package com.sibsutis.astronomyhandbook.opengl.obj

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Square {
    private var vertexBuffer: FloatBuffer? = null
    private var texCoordBuffer: FloatBuffer? = null
    private var indexBuffer: ShortBuffer? = null
    private val indexCount: Int = 6

    init {
        val vertices = floatArrayOf(
            -1f, -1f, 0f,
            1f, -1f, 0f,
            -1f,  1f, 0f,
            1f,  1f, 0f
        )
        val texCoords = floatArrayOf(
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
        )
        val indices = shortArrayOf(0, 1, 2, 1, 3, 2)

        vertexBuffer = createFloatBuffer(vertices)
        texCoordBuffer = createFloatBuffer(texCoords)
        indexBuffer = createShortBuffer(indices)
    }

    private fun createFloatBuffer(array: FloatArray): FloatBuffer {
        val buffer = ByteBuffer.allocateDirect(array.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        buffer.put(array)
        buffer.position(0)
        return buffer
    }

    private fun createShortBuffer(array: ShortArray): ShortBuffer {
        val buffer = ByteBuffer.allocateDirect(array.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
        buffer.put(array)
        buffer.position(0)
        return buffer
    }

    fun draw(shader: com.sibsutis.astronomyhandbook.opengl.shaders.BackgroundShader) {
        val posAttrib = shader.getPositionAttrib()
        val texAttrib = shader.getTexCoordAttrib()

        vertexBuffer?.let {
            GLES20.glEnableVertexAttribArray(posAttrib)
            GLES20.glVertexAttribPointer(posAttrib, 3, GLES20.GL_FLOAT, false, 0, it)
        }

        texCoordBuffer?.let {
            GLES20.glEnableVertexAttribArray(texAttrib)
            GLES20.glVertexAttribPointer(texAttrib, 2, GLES20.GL_FLOAT, false, 0, it)
        }

        indexBuffer?.let {
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, it)
        }

        GLES20.glDisableVertexAttribArray(posAttrib)
        GLES20.glDisableVertexAttribArray(texAttrib)
    }
}
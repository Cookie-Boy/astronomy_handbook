package com.sibsutis.astronomyhandbook.opengl.obj

import android.opengl.GLES20
import com.sibsutis.astronomyhandbook.opengl.shaders.SelectionShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class TransparentCube {
    private var vertexBuffer: FloatBuffer? = null
    private var indexBuffer: ShortBuffer? = null
    private val indexCount = 36

    init {
        val vertices = floatArrayOf(
            // Front face
            -1f, -1f,  1f,
            1f, -1f,  1f,
            1f,  1f,  1f,
            -1f,  1f,  1f,
            // Back face
            -1f, -1f, -1f,
            -1f,  1f, -1f,
            1f,  1f, -1f,
            1f, -1f, -1f,
            // Top face
            -1f,  1f, -1f,
            -1f,  1f,  1f,
            1f,  1f,  1f,
            1f,  1f, -1f,
            // Bottom face
            -1f, -1f, -1f,
            1f, -1f, -1f,
            1f, -1f,  1f,
            -1f, -1f,  1f,
            // Right face
            1f, -1f, -1f,
            1f,  1f, -1f,
            1f,  1f,  1f,
            1f, -1f,  1f,
            // Left face
            -1f, -1f, -1f,
            -1f, -1f,  1f,
            -1f,  1f,  1f,
            -1f,  1f, -1f
        )

        val indices = shortArrayOf(
            // Front face
            0, 1, 2, 0, 2, 3,
            // Back face
            4, 5, 6, 4, 6, 7,
            // Top face
            8, 9, 10, 8, 10, 11,
            // Bottom face
            12, 13, 14, 12, 14, 15,
            // Right face
            16, 17, 18, 16, 18, 19,
            // Left face
            20, 21, 22, 20, 22, 23
        )

        vertexBuffer = createFloatBuffer(vertices)
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

    fun draw(shader: SelectionShader) {
        val posAttrib = shader.getPositionAttrib()

        vertexBuffer?.let {
            GLES20.glEnableVertexAttribArray(posAttrib)
            GLES20.glVertexAttribPointer(posAttrib, 3, GLES20.GL_FLOAT, false, 0, it)
        }

        indexBuffer?.let {
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, it)
        }

        GLES20.glDisableVertexAttribArray(posAttrib)
    }
}
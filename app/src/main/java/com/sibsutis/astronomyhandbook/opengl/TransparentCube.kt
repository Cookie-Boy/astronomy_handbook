package com.sibsutis.astronomyhandbook.opengl

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

class TransparentCube {
    // Вершины куба (8 вершин)
    private val cubeCoords = floatArrayOf(
        -1f, -1f, -1f,   // 0
        1f, -1f, -1f,   // 1
        1f,  1f, -1f,   // 2
        -1f,  1f, -1f,   // 3
        -1f, -1f,  1f,   // 4
        1f, -1f,  1f,   // 5
        1f,  1f,  1f,   // 6
        -1f,  1f,  1f    // 7
    )

    // Индексы для 12 треугольников
    private val indices = shortArrayOf(
        // Передняя
        0, 1, 2, 0, 2, 3,
        // Задняя
        4, 5, 6, 4, 6, 7,
        // Левая
        0, 3, 7, 0, 7, 4,
        // Правая
        1, 2, 6, 1, 6, 5,
        // Верхняя
        3, 2, 6, 3, 6, 7,
        // Нижняя
        0, 1, 5, 0, 5, 4
    )

    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer

    init {
        val vbb = ByteBuffer.allocateDirect(cubeCoords.size * 4).order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer().apply { put(cubeCoords); position(0) }

        val ibb = ByteBuffer.allocateDirect(indices.size * 2).order(ByteOrder.nativeOrder())
        indexBuffer = ibb.asShortBuffer().apply { put(indices); position(0) }
    }

    fun draw(gl: GL10) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.size, GL10.GL_UNSIGNED_SHORT, indexBuffer)
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
    }
}
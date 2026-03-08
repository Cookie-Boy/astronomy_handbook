package com.sibsutis.astronomyhandbook.opengl

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

class Cube {
    // Вершины куба (8 вершин)
    private val cubeCoords = floatArrayOf(
        -1.0f, -1.0f, -1.0f,   // 0
        1.0f, -1.0f, -1.0f,   // 1
        1.0f,  1.0f, -1.0f,   // 2
        -1.0f,  1.0f, -1.0f,   // 3
        -1.0f, -1.0f,  1.0f,   // 4
        1.0f, -1.0f,  1.0f,   // 5
        1.0f,  1.0f,  1.0f,   // 6
        -1.0f,  1.0f,  1.0f    // 7
    )

    private val vertices = floatArrayOf(
        // Передняя грань (z = 1)
        -1.0f, -1.0f,  1.0f,  // 0
        1.0f, -1.0f,  1.0f,  // 1
        1.0f,  1.0f,  1.0f,  // 2
        -1.0f,  1.0f,  1.0f,  // 3
        // Задняя грань (z = -1)
        -1.0f, -1.0f, -1.0f,  // 4
        -1.0f,  1.0f, -1.0f,  // 5
        1.0f,  1.0f, -1.0f,  // 6
        1.0f, -1.0f, -1.0f,  // 7
        // Левая грань (x = -1)
        -1.0f, -1.0f, -1.0f,  // 8
        -1.0f, -1.0f,  1.0f,  // 9
        -1.0f,  1.0f,  1.0f,  // 10
        -1.0f,  1.0f, -1.0f,  // 11
        // Правая грань (x = 1)
        1.0f, -1.0f,  1.0f,  // 12
        1.0f, -1.0f, -1.0f,  // 13
        1.0f,  1.0f, -1.0f,  // 14
        1.0f,  1.0f,  1.0f,  // 15
        // Верхняя грань (y = 1)
        -1.0f,  1.0f, -1.0f,  // 16
        -1.0f,  1.0f,  1.0f,  // 17
        1.0f,  1.0f,  1.0f,  // 18
        1.0f,  1.0f, -1.0f,  // 19
        // Нижняя грань (y = -1)
        -1.0f, -1.0f,  1.0f,  // 20
        -1.0f, -1.0f, -1.0f,  // 21
        1.0f, -1.0f, -1.0f,  // 22
        1.0f, -1.0f,  1.0f   // 23
    )

    // Текстурные координаты для каждой из 24 вершин (одинаковые для каждой грани: от (0,0) до (1,1))
    private val texCoords = floatArrayOf(
        // Передняя грань
        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
        // Задняя грань
        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
        // Левая грань
        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
        // Правая грань
        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
        // Верхняя грань
        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
        // Нижняя грань
        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f
    )

    // Индексы для 12 треугольников (по 2 на грань)
    private val indices = shortArrayOf(
        // Передняя
        0, 1, 2, 0, 2, 3,
        // Задняя
        4, 5, 6, 4, 6, 7,
        // Левая
        8, 9, 10, 8, 10, 11,
        // Правая
        12, 13, 14, 12, 14, 15,
        // Верхняя
        16, 17, 18, 16, 18, 19,
        // Нижняя
        20, 21, 22, 20, 22, 23
    )

    private val vertexBuffer: FloatBuffer
    private val textureBuffer: FloatBuffer
    private val indexBuffer: java.nio.ShortBuffer

    init {
        // Вершинный буфер
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        // Текстурный буфер
        val tbb = ByteBuffer.allocateDirect(texCoords.size * 4)
        tbb.order(ByteOrder.nativeOrder())
        textureBuffer = tbb.asFloatBuffer()
        textureBuffer.put(texCoords)
        textureBuffer.position(0)

        // Индексный буфер
        val ibb = ByteBuffer.allocateDirect(indices.size * 2)
        ibb.order(ByteOrder.nativeOrder())
        indexBuffer = ibb.asShortBuffer()
        indexBuffer.put(indices)
        indexBuffer.position(0)
    }

    fun draw(gl: GL10) {
        gl.glFrontFace(GL10.GL_CCW)
        gl.glEnable(GL10.GL_CULL_FACE)
        gl.glCullFace(GL10.GL_BACK)

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer)

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.size, GL10.GL_UNSIGNED_SHORT, indexBuffer)

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glDisable(GL10.GL_CULL_FACE)
    }
}
package com.sibsutis.astronomyhandbook.opengl

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

class Sphere(radius: Float, private val slices: Int = 24, private val stacks: Int = 12) {
    private val vertexBuffer: FloatBuffer
    private val normalBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    private val numIndices: Int

    init {
        val vertices = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val indices = mutableListOf<Short>()

        val phiStep = Math.PI.toFloat() / stacks
        val thetaStep = 2 * Math.PI.toFloat() / slices

        for (i in 0..stacks) {
            val phi = -Math.PI.toFloat() / 2 + i * phiStep
            val sinPhi = kotlin.math.sin(phi)
            val cosPhi = kotlin.math.cos(phi)

            for (j in 0..slices) {
                val theta = j * thetaStep
                val sinTheta = kotlin.math.sin(theta)
                val cosTheta = kotlin.math.cos(theta)

                val x = cosPhi * cosTheta
                val y = sinPhi
                val z = cosPhi * sinTheta

                vertices.add(x * radius)
                vertices.add(y * radius)
                vertices.add(z * radius)

                normals.add(x)
                normals.add(y)
                normals.add(z)
            }
        }

        for (i in 0 until stacks) {
            for (j in 0 until slices) {
                val first = (i * (slices + 1) + j).toShort()
                val second = (i * (slices + 1) + j + 1).toShort()
                val third = ((i + 1) * (slices + 1) + j).toShort()
                val fourth = ((i + 1) * (slices + 1) + j + 1).toShort()

                indices.add(first)
                indices.add(second)
                indices.add(third)
                indices.add(second)
                indices.add(fourth)
                indices.add(third)
            }
        }

        numIndices = indices.size

        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(vertices.toFloatArray()); position(0) }

        normalBuffer = ByteBuffer.allocateDirect(normals.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(normals.toFloatArray()); position(0) }

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .apply { put(indices.toShortArray()); position(0) }
    }

    fun draw(gl: GL10) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer)

        gl.glDrawElements(GL10.GL_TRIANGLES, numIndices, GL10.GL_UNSIGNED_SHORT, indexBuffer)

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY)
    }
}
package com.sibsutis.astronomyhandbook.opengl.obj

import android.opengl.GLES20
import com.sibsutis.astronomyhandbook.opengl.shaders.TexturedShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.*

class Sphere(private val radius: Float, private val stacks: Int = 36, private val slices: Int = 36) {
    private var vertexBuffer: FloatBuffer? = null
    private var normalBuffer: FloatBuffer? = null
    private var texCoordBuffer: FloatBuffer? = null
    private var indexCount: Int = 0

    init {
        generateData()
    }

    private fun generateData() {
        val vertices = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val texCoords = mutableListOf<Float>()

        val phiStep = PI.toFloat() / stacks
        val thetaStep = (2 * PI).toFloat() / slices

        for (i in 0 until stacks) {
            val phi = i * phiStep
            val nextPhi = (i + 1) * phiStep
            for (j in 0 until slices) {
                val theta = j * thetaStep
                val nextTheta = (j + 1) * thetaStep

                // Треугольник 1
                addVertex(vertices, normals, texCoords, phi, theta)
                addVertex(vertices, normals, texCoords, nextPhi, theta)
                addVertex(vertices, normals, texCoords, phi, nextTheta)

                // Треугольник 2
                addVertex(vertices, normals, texCoords, nextPhi, theta)
                addVertex(vertices, normals, texCoords, nextPhi, nextTheta)
                addVertex(vertices, normals, texCoords, phi, nextTheta)
            }
        }

        indexCount = vertices.size / 3
        vertexBuffer = createFloatBuffer(vertices.toFloatArray())
        normalBuffer = createFloatBuffer(normals.toFloatArray())
        texCoordBuffer = createFloatBuffer(texCoords.toFloatArray())
    }

    private fun addVertex(vertices: MutableList<Float>, normals: MutableList<Float>,
                          texCoords: MutableList<Float>, phi: Float, theta: Float) {
        val x = radius * sin(phi) * cos(theta)
        val y = radius * cos(phi)
        val z = radius * sin(phi) * sin(theta)

        vertices.add(x)
        vertices.add(y)
        vertices.add(z)

        normals.add(x / radius)
        normals.add(y / radius)
        normals.add(z / radius)

        texCoords.add((theta / (2 * PI)).toFloat())
        texCoords.add((phi / PI).toFloat())
    }

    private fun createFloatBuffer(array: FloatArray): FloatBuffer {
        val buffer = ByteBuffer.allocateDirect(array.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        buffer.put(array)
        buffer.position(0)
        return buffer
    }

    fun draw(shader: TexturedShader) {
        val posAttrib = shader.getPositionAttrib()
        val normAttrib = shader.getNormalAttrib()
        val texAttrib = shader.getTexCoordAttrib()

        vertexBuffer?.let {
            GLES20.glEnableVertexAttribArray(posAttrib)
            GLES20.glVertexAttribPointer(posAttrib, 3, GLES20.GL_FLOAT, false, 0, it)
        }
        normalBuffer?.let {
            GLES20.glEnableVertexAttribArray(normAttrib)
            GLES20.glVertexAttribPointer(normAttrib, 3, GLES20.GL_FLOAT, false, 0, it)
        }
        texCoordBuffer?.let {
            GLES20.glEnableVertexAttribArray(texAttrib)
            GLES20.glVertexAttribPointer(texAttrib, 2, GLES20.GL_FLOAT, false, 0, it)
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, indexCount)

        GLES20.glDisableVertexAttribArray(posAttrib)
        GLES20.glDisableVertexAttribArray(normAttrib)
        GLES20.glDisableVertexAttribArray(texAttrib)
    }
}
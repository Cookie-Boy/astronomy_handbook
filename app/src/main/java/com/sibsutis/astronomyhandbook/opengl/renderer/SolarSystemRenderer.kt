package com.sibsutis.astronomyhandbook.opengl.renderer

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import com.sibsutis.astronomyhandbook.R
import com.sibsutis.astronomyhandbook.opengl.obj.Sphere
import com.sibsutis.astronomyhandbook.opengl.obj.Square
import com.sibsutis.astronomyhandbook.opengl.obj.TransparentCube
import com.sibsutis.astronomyhandbook.opengl.model.Planet
import com.sibsutis.astronomyhandbook.opengl.model.SelectableObject
import com.sibsutis.astronomyhandbook.opengl.shaders.PlanetShader
import com.sibsutis.astronomyhandbook.opengl.shaders.BackgroundShader
import com.sibsutis.astronomyhandbook.opengl.shaders.SelectionShader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class SolarSystemRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var square: Square
    private lateinit var sphere: Sphere
    private lateinit var selectionCube: TransparentCube
    private var textureBackground = -1

    private lateinit var planetShader: PlanetShader
    private lateinit var backgroundShader: BackgroundShader
    private lateinit var selectionShader: SelectionShader

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val normalMatrix = FloatArray(9)

    private val lightPosEye = floatArrayOf(0f, 0f, -6f)

    private val planets = listOf(
        Planet("Mercury", 0.5f, 1.5f, 0.02f, floatArrayOf(0.8f, 0.8f, 0.8f, 1f)),
        Planet("Venus", 0.53f, 2.2f, 0.015f, floatArrayOf(1f, 0.8f, 0.5f, 1f)),
        Planet("Earth", 0.55f, 2.9f, 0.01f, floatArrayOf(0.2f, 0.5f, 1f, 1f)),
        Planet("Mars", 0.51f, 3.6f, 0.008f, floatArrayOf(1f, 0.3f, 0.2f, 1f)),
        Planet("Jupiter", 0.7f, 4.5f, 0.005f, floatArrayOf(0.9f, 0.7f, 0.5f, 1f)),
        Planet("Saturn", 0.65f, 5.2f, 0.004f, floatArrayOf(0.9f, 0.8f, 0.6f, 1f))
    )

    private var moonAngle = 0f
    private val moonSpeed = 0.05f
    private val moonOrbitRadius = 0.6f
    private val moonRadius = 0.25f
    private var lastMoonX = 0f
    private var lastMoonY = 0f
    private var lastMoonZ = 0f

    private var selectedObjectIndex = 0

    fun setSelectedObjectIndex(index: Int) {
        selectedObjectIndex = index
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        planetShader = PlanetShader()
        backgroundShader = BackgroundShader()
        selectionShader = SelectionShader()

        sphere = Sphere(1.0f)
        square = Square()
        selectionCube = TransparentCube()

        textureBackground = loadTexture(R.drawable.dark_galaxy)

        // Настройки OpenGL
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        Matrix.setIdentityM(viewMatrix, 0)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 100f)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        drawBackground()

        val selectableObjects = mutableListOf<SelectableObject>()

        drawSun(selectableObjects)
        drawPlanetsAndMoon(selectableObjects)
        drawSelection(selectableObjects)
    }

    private fun drawBackground() {
        backgroundShader.use()
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -20f)
        Matrix.scaleM(modelMatrix, 0, 20f, 20f, 1f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0)

        backgroundShader.setUniforms(mvpMatrix, textureBackground)
        square.draw(backgroundShader)
    }

    private fun drawSun(selectableObjects: MutableList<SelectableObject>) {
        planetShader.use()
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -6f)
        Matrix.scaleM(modelMatrix, 0, 1f, 1f, 1f)

        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)
        extractNormalMatrix(normalMatrix, mvMatrix)

        // Sun's materials
        val ambient = floatArrayOf(0f, 0f, 0f, 1f)
        val diffuse = floatArrayOf(1f, 1f, 0.5f, 1f)
        val emission = floatArrayOf(0.8f, 0.8f, 0.3f, 1f)

        planetShader.setUniforms(mvpMatrix, mvMatrix, normalMatrix, lightPosEye,
            ambient, diffuse, emission, false)
        sphere.draw(planetShader)
    }

    private fun drawPlanetsAndMoon(selectableObjects: MutableList<SelectableObject>) {
        for (planet in planets) {
            planet.angle += planet.speed
            val x = planet.orbitRadius * cos(planet.angle)
            val y = planet.orbitRadius * sin(planet.angle)

            drawPlanet(planet, x, y)
            selectableObjects.add(SelectableObject(planet.name, x, y, 0f, planet.radius))

            if (planet.name == "Earth") {
                moonAngle += moonSpeed
                val moonX = moonOrbitRadius * cos(moonAngle)
                val moonZ = moonOrbitRadius * sin(moonAngle)
                val absMoonX = x + moonX
                val absMoonY = y
                val absMoonZ = moonZ

                drawMoon(absMoonX, absMoonY, absMoonZ)

                lastMoonX = absMoonX
                lastMoonY = absMoonY
                lastMoonZ = absMoonZ
            }
        }
        selectableObjects.add(SelectableObject("Moon", lastMoonX, lastMoonY, lastMoonZ, moonRadius))
    }

    private fun drawPlanet(planet: Planet, x: Float, y: Float) {
        planetShader.use()
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -6f) // центр системы
        Matrix.translateM(modelMatrix, 0, x, y, 0f)
        Matrix.scaleM(modelMatrix, 0, planet.radius, planet.radius, planet.radius)

        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)
        extractNormalMatrix(normalMatrix, mvMatrix)

        val ambient = floatArrayOf(0.2f, 0.2f, 0.2f, 1f)
        val diffuse = planet.color
        val emission = floatArrayOf(0f, 0f, 0f, 1f)

        planetShader.setUniforms(mvpMatrix, mvMatrix, normalMatrix, lightPosEye,
            ambient, diffuse, emission, false)
        sphere.draw(planetShader)
    }

    private fun drawMoon(x: Float, y: Float, z: Float) {
        planetShader.use()
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -6f)
        Matrix.translateM(modelMatrix, 0, x, y, z)
        Matrix.scaleM(modelMatrix, 0, moonRadius, moonRadius, moonRadius)

        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)
        extractNormalMatrix(normalMatrix, mvMatrix)

        val ambient = floatArrayOf(0.2f, 0.2f, 0.2f, 1f)
        val diffuse = floatArrayOf(0.8f, 0.8f, 0.8f, 1f)
        val emission = floatArrayOf(0f, 0f, 0f, 1f)

        planetShader.setUniforms(mvpMatrix, mvMatrix, normalMatrix, lightPosEye,
            ambient, diffuse, emission, false)
        sphere.draw(planetShader)
    }

    private fun drawSelection(selectableObjects: MutableList<SelectableObject>) {
        if (selectedObjectIndex !in selectableObjects.indices) return

        val obj = selectableObjects[selectedObjectIndex]

        selectionShader.use()
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -6f) // центр системы
        Matrix.translateM(modelMatrix, 0, obj.x, obj.y, obj.z)
        Matrix.scaleM(modelMatrix, 0, obj.radius * 0.7f, obj.radius * 0.7f, obj.radius * 0.7f)

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0)

        val color = floatArrayOf(1f, 1f, 1f, 0.3f)
        selectionShader.setUniforms(mvpMatrix, color)

        selectionCube.draw(selectionShader)
    }

    private fun extractNormalMatrix(normalMat: FloatArray, mvMat: FloatArray) {
        normalMat[0] = mvMat[0]; normalMat[1] = mvMat[1]; normalMat[2] = mvMat[2]
        normalMat[3] = mvMat[4]; normalMat[4] = mvMat[5]; normalMat[5] = mvMat[6]
        normalMat[6] = mvMat[8]; normalMat[7] = mvMat[9]; normalMat[8] = mvMat[10]
    }

    private fun loadTexture(resourceId: Int): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        val textureId = textureIds[0]
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
            ?: throw RuntimeException("Failed to decode bitmap")
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
        return textureId
    }
}
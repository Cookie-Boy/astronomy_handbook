package com.sibsutis.astronomyhandbook.opengl.renderer

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import com.sibsutis.astronomyhandbook.R
import com.sibsutis.astronomyhandbook.opengl.obj.Sphere
import com.sibsutis.astronomyhandbook.opengl.obj.Square
import com.sibsutis.astronomyhandbook.opengl.obj.TransparentCube
import com.sibsutis.astronomyhandbook.opengl.model.Planet
import com.sibsutis.astronomyhandbook.opengl.model.SelectableObject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class PlanetGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var square: Square
    private lateinit var sphere: Sphere
    private var textureBackground = -1

    private lateinit var selectionCube: TransparentCube

    private val planets = listOf(
        Planet("Mercury", 0.15f, 1.5f, 0.02f, floatArrayOf(0.8f, 0.8f, 0.8f, 1f)),
        Planet("Venus", 0.18f, 2.2f, 0.015f, floatArrayOf(1f, 0.8f, 0.5f, 1f)),
        Planet("Earth", 0.2f, 2.9f, 0.01f, floatArrayOf(0.2f, 0.5f, 1f, 1f)),
        Planet("Mars", 0.16f, 3.6f, 0.008f, floatArrayOf(1f, 0.3f, 0.2f, 1f)),
        Planet("Jupiter", 0.35f, 4.5f, 0.005f, floatArrayOf(0.9f, 0.7f, 0.5f, 1f)),
        Planet("Saturn", 0.3f, 5.2f, 0.004f, floatArrayOf(0.9f, 0.8f, 0.6f, 1f))
    )

    private var moonAngle = 0f
    private val moonSpeed = 0.05f
    private val moonOrbitRadius = 0.6f
    private val moonRadius = 0.1f

    private var lastMoonX = 0f
    private var lastMoonY = 0f
    private var lastMoonZ = 0f

    private var selectedObjectIndex = 0

    fun setSelectedObjectIndex(index: Int) {
        selectedObjectIndex = index
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        square = Square()
        sphere = Sphere(1.0f)
        selectionCube = TransparentCube()

        textureBackground = loadTexture(gl, R.drawable.dark_galaxy)

        // Настройки OpenGL
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glDepthFunc(GL10.GL_LEQUAL)
        gl.glEnable(GL10.GL_TEXTURE_2D)

        // Освещение
        gl.glEnable(GL10.GL_LIGHTING)
        gl.glEnable(GL10.GL_LIGHT0)
        val lightAmbient = floatArrayOf(0.2f, 0.2f, 0.2f, 1f)
        val lightDiffuse = floatArrayOf(1f, 1f, 1f, 1f)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0)

        // Цвет материала
        gl.glEnable(GL10.GL_COLOR_MATERIAL)
//        gl.glColorMaterial(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        gl.glViewport(0, 0, width, height)
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()
        val ratio = width.toFloat() / height.toFloat()
        gl.glFrustumf(-ratio, ratio, -1f, 1f, 1f, 100f)  // ближняя плоскость 1, дальняя 100
    }

    override fun onDrawFrame(gl: GL10) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

        // Задний фон
        gl.glDisable(GL10.GL_LIGHTING)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glTranslatef(0f, 0f, -20f)
        gl.glScalef(20f, 20f, 1f)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureBackground)
        square.draw(gl)

        // Включаем освещение для планет
        gl.glEnable(GL10.GL_LIGHTING)
        val lightPos = floatArrayOf(0f, 0f, -6f, 1f)  // Солнце будет в (0,0,-6)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0)

        // Солнце
        gl.glLoadIdentity()
        gl.glTranslatef(0f, 0f, -6f)  // центр системы
        val sunEmission = floatArrayOf(0.8f, 0.8f, 0.3f, 1f)
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, sunEmission, 0)
        gl.glColor4f(1f, 1f, 0.5f, 1f)
        gl.glPushMatrix()
        gl.glScalef(0.8f, 0.8f, 0.8f)
        sphere.draw(gl)
        gl.glPopMatrix()
        val noEmission = floatArrayOf(0f, 0f, 0f, 1f)
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, noEmission, 0)

        val selectableObjects = mutableListOf<SelectableObject>()

        // Планеты
        for ((index, planet) in planets.withIndex()) {
            planet.angle += planet.speed
            val x = planet.orbitRadius * cos(planet.angle)
            val y = planet.orbitRadius * sin(planet.angle)

            // Draw the planet
            gl.glPushMatrix()
            gl.glTranslatef(x, y, 0f)
            gl.glColor4f(planet.color[0], planet.color[1], planet.color[2], planet.color[3])
            gl.glScalef(planet.radius, planet.radius, planet.radius)
            sphere.draw(gl)
            gl.glPopMatrix()

            selectableObjects.add(SelectableObject(planet.name, x, y, 0f, planet.radius))

            if (planet.name == "Earth") {
                moonAngle += moonSpeed
                val moonX = moonOrbitRadius * cos(moonAngle)
                val moonZ = moonOrbitRadius * sin(moonAngle)
                val absMoonX = x + moonX
                val absMoonY = y
                val absMoonZ = moonZ

                gl.glPushMatrix()
                gl.glTranslatef(absMoonX, absMoonY, absMoonZ)
                gl.glColor4f(0.8f, 0.8f, 0.8f, 1f)
                gl.glScalef(moonRadius, moonRadius, moonRadius)
                sphere.draw(gl)
                gl.glPopMatrix()

                lastMoonX = absMoonX
                lastMoonY = absMoonY
                lastMoonZ = absMoonZ
            }
        }

        selectableObjects.add(SelectableObject("Moon", lastMoonX, lastMoonY, lastMoonZ, moonRadius))

        if (selectedObjectIndex in selectableObjects.indices) {
            val obj = selectableObjects[selectedObjectIndex]
            gl.glDisable(GL10.GL_LIGHTING)
            gl.glEnable(GL10.GL_BLEND)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
            gl.glDisable(GL10.GL_TEXTURE_2D)

            gl.glColor4f(1f, 1f, 1f, 0.3f)  // белый полупрозрачный

            gl.glPushMatrix()
            gl.glLoadIdentity()
            gl.glTranslatef(0f, 0f, -6f)       // центр системы
            gl.glTranslatef(obj.x, obj.y, obj.z) // позиция объекта
            gl.glScalef(obj.radius * 1.2f, obj.radius * 1.2f, obj.radius * 1.2f)
            selectionCube.draw(gl)
            gl.glPopMatrix()

            gl.glEnable(GL10.GL_LIGHTING)
            gl.glDisable(GL10.GL_BLEND)
            gl.glEnable(GL10.GL_TEXTURE_2D)
        }
    }

    private fun loadTexture(gl: GL10, resourceId: Int): Int {
        val textureIds = IntArray(1)
        gl.glGenTextures(1, textureIds, 0)
        val textureId = textureIds[0]
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId)

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
            ?: throw RuntimeException("Failed to decode bitmap")
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
        return textureId
    }
}
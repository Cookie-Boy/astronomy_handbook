package com.sibsutis.astronomyhandbook.opengl

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.util.Log
import com.sibsutis.astronomyhandbook.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class PlanetGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var square: Square
    private lateinit var sphere: Sphere
    private var textureBackground = -1

    private lateinit var selectionCube: TransparentCube
    private var selectedPlanetIndex = 0

    private data class Planet(
        val name: String,
        val radius: Float,
        val orbitRadius: Float,
        val speed: Float,
        val color: FloatArray,
        var angle: Float = 0f
    )

    private val planets = listOf(
        Planet("Mercury", 0.15f, 1.5f, 0.02f, floatArrayOf(0.8f, 0.8f, 0.8f, 1f)),
        Planet("Venus",   0.18f, 2.2f, 0.015f, floatArrayOf(1f, 0.8f, 0.5f, 1f)),
        Planet("Earth",   0.2f,  2.9f, 0.01f, floatArrayOf(0.2f, 0.5f, 1f, 1f)),
        Planet("Mars",    0.16f, 3.6f, 0.008f, floatArrayOf(1f, 0.3f, 0.2f, 1f)),
        Planet("Jupiter", 0.35f, 4.5f, 0.005f, floatArrayOf(0.9f, 0.7f, 0.5f, 1f)),
        Planet("Saturn",  0.3f,  5.2f, 0.004f, floatArrayOf(0.9f, 0.8f, 0.6f, 1f))
    )

    private var moonAngle = 0f
    private val moonSpeed = 0.05f
    private val moonOrbitRadius = 0.6f

    fun setSelectedPlanetIndex(index: Int) {
        selectedPlanetIndex = index
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        Log.d("MyGLRenderer", "onSurfaceCreated")
        square = Square()
        sphere = Sphere(1.0f)
        selectionCube = TransparentCube()

        textureBackground = loadTexture(gl, R.drawable.galaxy_background)


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

        // --- Включаем освещение для небесных тел ---
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

        var selectedX = 0f
        var selectedY = 0f
        var selectedRadius = 0f

        // Планеты
        for ((index, planet) in planets.withIndex()) {
            planet.angle += planet.speed
            val x = planet.orbitRadius * cos(planet.angle)
            val y = planet.orbitRadius * sin(planet.angle)
            gl.glPushMatrix()
            gl.glTranslatef(x, y, 0f)
            gl.glColor4f(planet.color[0], planet.color[1], planet.color[2], planet.color[3])
            gl.glScalef(planet.radius, planet.radius, planet.radius)
            sphere.draw(gl)
            gl.glPopMatrix()

            if (planet.name == "Earth") {
                moonAngle += moonSpeed
                val moonX = moonOrbitRadius * cos(moonAngle)
                val moonZ = moonOrbitRadius * sin(moonAngle)
                gl.glPushMatrix()
                gl.glTranslatef(x + moonX, y, moonZ)
                gl.glColor4f(0.8f, 0.8f, 0.8f, 1f)
                gl.glScalef(0.1f, 0.1f, 0.1f)
                sphere.draw(gl)
                gl.glPopMatrix()
            }

            if (index == selectedPlanetIndex) {
                selectedX = x
                selectedY = y
                selectedRadius = planet.radius
            }
        }

        if (selectedPlanetIndex in planets.indices) {
            gl.glDisable(GL10.GL_LIGHTING)
            gl.glEnable(GL10.GL_BLEND)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
            gl.glDisable(GL10.GL_TEXTURE_2D)

            gl.glColor4f(1f, 1f, 1f, 0.3f)  // белый полупрозрачный

            gl.glPushMatrix()
            gl.glLoadIdentity()
            gl.glTranslatef(0f, 0f, -6f)       // центр системы
            gl.glTranslatef(selectedX, selectedY, 0f) // позиция планеты
            // Масштабируем куб так, чтобы он был чуть больше планеты
            gl.glScalef(selectedRadius * 1.2f, selectedRadius * 1.2f, selectedRadius * 1.2f)
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
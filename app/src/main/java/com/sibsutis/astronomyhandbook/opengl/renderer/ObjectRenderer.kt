package com.sibsutis.astronomyhandbook.opengl.renderer

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import com.sibsutis.astronomyhandbook.R
import com.sibsutis.astronomyhandbook.data.PlanetDescriptions
import com.sibsutis.astronomyhandbook.opengl.obj.Sphere
import com.sibsutis.astronomyhandbook.opengl.obj.Square
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ObjectRenderer(
    private val context: Context,
    private val objectName: String
) : GLSurfaceView.Renderer {

    private lateinit var sphere: Sphere
    private lateinit var square: Square
    private var textureBackground = -1
    private var textureId = -1
    private var rotationAngle = 0f

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        square = Square()
        sphere = Sphere(1.0f, true)

        textureBackground = loadTexture(gl, R.drawable.dark_galaxy)

        val planetData = PlanetDescriptions.getPlanetData(objectName)
        textureId = planetData?.textureResId?.let { loadTexture(gl, it) } ?: -1

        // Настройки
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glDepthFunc(GL10.GL_LEQUAL)
        gl.glEnable(GL10.GL_TEXTURE_2D)

        // Освещение по Фонгу
        gl.glEnable(GL10.GL_LIGHTING)
        gl.glEnable(GL10.GL_LIGHT0)

        val lightAmbient = floatArrayOf(0.2f, 0.2f, 0.2f, 1f) // фон
        val lightDiffuse = floatArrayOf(1f, 1f, 1f, 1f) // отражение света
        val lightSpecular = floatArrayOf(1f, 1f, 1f, 1f) // блики
        val lightPos = floatArrayOf(5f, 5f, 10f, 1f)

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, lightSpecular, 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0)

        val matAmbient = floatArrayOf(0.2f, 0.2f, 0.2f, 1f)
        val matDiffuse = floatArrayOf(1f, 1f, 1f, 1f)
        val matSpecular = floatArrayOf(0.5f, 0.5f, 0.5f, 1f)
        val matShininess = floatArrayOf(50f)

        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0)
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0)
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, matSpecular, 0)
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, matShininess, 0)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        gl.glViewport(0, 0, width, height)
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()
        val ratio = width.toFloat() / height.toFloat()
        gl.glFrustumf(-ratio, ratio, -1f, 1f, 1f, 20f)
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

        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glTranslatef(0f, 0f, -6f)
        gl.glRotatef(rotationAngle, 0f, 1f, 1f)

        if (textureId != -1) {
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId)
        }
        sphere.draw(gl)

        rotationAngle += 0.5f
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
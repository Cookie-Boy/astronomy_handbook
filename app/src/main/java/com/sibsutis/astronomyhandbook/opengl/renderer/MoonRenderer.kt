package com.sibsutis.astronomyhandbook.opengl.renderer

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import com.sibsutis.astronomyhandbook.R
import com.sibsutis.astronomyhandbook.opengl.obj.Sphere
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MoonRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var sphere: Sphere
    private var moonTexture = -1
    private var rotationAngle = 0f

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        sphere = Sphere(1.0f)

        // Загружаем текстуру Луны
        moonTexture = loadTexture(gl, R.drawable.moon)

        // Настройки
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glDepthFunc(GL10.GL_LEQUAL)
        gl.glEnable(GL10.GL_TEXTURE_2D)

        // Включаем освещение (модель Фонга)
        gl.glEnable(GL10.GL_LIGHTING)
        gl.glEnable(GL10.GL_LIGHT0)

        // Параметры света
        val lightAmbient = floatArrayOf(0.2f, 0.2f, 0.2f, 1f)
        val lightDiffuse = floatArrayOf(1f, 1f, 1f, 1f)
        val lightSpecular = floatArrayOf(1f, 1f, 1f, 1f)
        val lightPos = floatArrayOf(5f, 5f, 10f, 1f)

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, lightSpecular, 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0)

        // Параметры материала Луны (модель Фонга)
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

        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glTranslatef(0f, 0f, -6f)
        gl.glRotatef(rotationAngle, 0f, 1f, 1f) // медленное вращение

        gl.glBindTexture(GL10.GL_TEXTURE_2D, moonTexture)
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
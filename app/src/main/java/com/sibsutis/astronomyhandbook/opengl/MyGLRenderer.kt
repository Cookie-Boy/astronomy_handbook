package com.sibsutis.astronomyhandbook.opengl

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import com.sibsutis.astronomyhandbook.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(private val context: Context) : android.opengl.GLSurfaceView.Renderer {

    private lateinit var square: Square
    private lateinit var cube: Cube
    private var textureCube = -1
    private var textureBackground = -1

    private var cubeRotation: Float = 0f

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        square = Square()
        cube = Cube()

        textureCube = loadTexture(gl, R.drawable.cube_texture)
        textureBackground = loadTexture(gl, R.drawable.galaxy_background)

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)   // чёрный фон
        gl.glEnable(GL10.GL_DEPTH_TEST)            // тест глубины
        gl.glDepthFunc(GL10.GL_LEQUAL)

        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        gl.glViewport(0, 0, width, height)

        // Настройка перспективной проекции
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()
        val ratio = width.toFloat() / height.toFloat()
        gl.glFrustumf(-ratio, ratio, -1f, 1f, 3f, 100f)  // ближняя плоскость 3, дальняя 100
    }

    override fun onDrawFrame(gl: GL10) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureBackground)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
        gl.glTranslatef(0f, 0f, -20f)   // назад на 20
        gl.glScalef(15f, 15f, 1f)
        square.draw(gl)

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureCube)
        gl.glLoadIdentity()
        gl.glTranslatef(0f, 0f, -6f)
        gl.glRotatef(cubeRotation, 1f, 1f, 0f)
        cube.draw(gl)

        cubeRotation += 1.2f
    }

    private fun loadTexture(gl: GL10, resourceId: Int): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        val textureId = textureIds[0]

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId)

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())

        val options = BitmapFactory.Options()
        options.inScaled = false   // без масштабирования
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)

        bitmap.recycle()
        return textureId
    }
}
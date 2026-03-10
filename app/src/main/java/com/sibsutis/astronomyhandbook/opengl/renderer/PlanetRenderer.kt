package com.sibsutis.astronomyhandbook.opengl.renderer

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import com.sibsutis.astronomyhandbook.R
import com.sibsutis.astronomyhandbook.data.PlanetDescriptions
import com.sibsutis.astronomyhandbook.opengl.obj.Sphere
import com.sibsutis.astronomyhandbook.opengl.obj.Square
import com.sibsutis.astronomyhandbook.opengl.shaders.BackgroundShader
import com.sibsutis.astronomyhandbook.opengl.shaders.ObjectShader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class PlanetRenderer(
    private val context: Context,
    private val objectName: String
) : GLSurfaceView.Renderer {

    // Геометрические объекты
    private lateinit var sphere: Sphere
    private lateinit var square: Square

    // Шейдеры
    private lateinit var objectShader: ObjectShader
    private lateinit var backgroundShader: BackgroundShader

    // Текстуры
    private var textureBackground = -1
    private var textureId = -1

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val normalMatrix = FloatArray(9)

    private val lightPosWorld = floatArrayOf(5f, 5f, 10f, 1f)
    private val lightPosEye = FloatArray(4)

    private val ambient = floatArrayOf(0.2f, 0.2f, 0.2f, 1f)
    private val diffuse = floatArrayOf(1f, 1f, 1f, 1f)
    private val specular = floatArrayOf(0.5f, 0.5f, 0.5f, 1f)
    private var shininess = 50f

    private var rotationAngle = 0f

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // Создаём объекты геометрии
        sphere = Sphere(1.0f) // радиус 1.0, текстурные координаты генерируются внутри
        square = Square()

        // Создаём шейдеры
        objectShader = ObjectShader()
        backgroundShader = BackgroundShader()

        // Загружаем текстуры
        textureBackground = loadTexture(R.drawable.dark_galaxy)

        val planetData = PlanetDescriptions.getPlanetData(objectName)
        textureId = planetData?.textureResId?.let { loadTexture(it) } ?: -1

        // Настройки OpenGL
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        // View матрица – камера в начале координат, смотрит вдоль -Z
        Matrix.setIdentityM(viewMatrix, 0)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 20f)
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        drawBackground()
        drawPlanet()
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

    private fun drawPlanet() {
        objectShader.use()

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -6f)
        Matrix.rotateM(modelMatrix, 0, rotationAngle, 0f, 1f, 1f)

        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)

        extractNormalMatrix(normalMatrix, mvMatrix)

        Matrix.multiplyMV(lightPosEye, 0, viewMatrix, 0, lightPosWorld, 0)

        objectShader.setUniforms(
            mvpMatrix = mvpMatrix, // локальные координаты -> экранные координаты
            mvMatrix = mvMatrix, // локальные координаты -> координаты камеры
            normalMatrix = normalMatrix, // нормали -> координаты камеры
            lightPos = lightPosEye, // источник света
            ambient = ambient, // фоновое освещение
            diffuse = diffuse, // рассеянное освещение
            specular = specular, // блики
            shininess = shininess, // блеск
            useTexture = textureId != -1,
            textureId = textureId
        )

        sphere.draw(objectShader)

        rotationAngle += 0.5f
    }

    private fun extractNormalMatrix(normalMat: FloatArray, mvMat: FloatArray) {
        // Извлекаем 3x3 подматрицу
        normalMat[0] = mvMat[0]
        normalMat[1] = mvMat[1]
        normalMat[2] = mvMat[2]
        normalMat[3] = mvMat[4]
        normalMat[4] = mvMat[5]
        normalMat[5] = mvMat[6]
        normalMat[6] = mvMat[8]
        normalMat[7] = mvMat[9]
        normalMat[8] = mvMat[10]
        // При равномерном масштабировании можно не инвертировать/транспонировать
    }

    private fun loadTexture(resourceId: Int): Int {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        val textureId = textureIds[0]
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
            ?: throw RuntimeException("Failed to decode bitmap")
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
        return textureId
    }
}
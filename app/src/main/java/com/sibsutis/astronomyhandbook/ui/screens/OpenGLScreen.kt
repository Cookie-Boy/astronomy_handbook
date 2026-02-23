package com.sibsutis.astronomyhandbook.ui.screens

import android.opengl.GLSurfaceView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.sibsutis.astronomyhandbook.opengl.MyGLRenderer
import com.sibsutis.astronomyhandbook.opengl.PlanetGLRenderer

@Composable
fun OpenGLScreen() {
    val context = LocalContext.current
    AndroidView(
        factory = { ctx ->
            GLSurfaceView(ctx).apply {
                setEGLContextClientVersion(1)
                setRenderer(PlanetGLRenderer(context))
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
        },
        modifier = Modifier.fillMaxSize().clipToBounds()
    )
}
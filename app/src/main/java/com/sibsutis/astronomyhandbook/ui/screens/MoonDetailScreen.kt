package com.sibsutis.astronomyhandbook.ui.screens

import android.opengl.GLSurfaceView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.sibsutis.astronomyhandbook.opengl.renderer.MoonRenderer

@Composable
fun MoonDetailScreen() {
    val context = LocalContext.current
    AndroidView(
        factory = { ctx ->
            GLSurfaceView(ctx).apply {
                setEGLContextClientVersion(1)
                setRenderer(MoonRenderer(context))
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
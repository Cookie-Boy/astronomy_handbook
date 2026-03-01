package com.sibsutis.astronomyhandbook.ui.screens

import android.opengl.GLSurfaceView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.sibsutis.astronomyhandbook.opengl.PlanetGLRenderer

@Composable
fun OpenGLScreen(selectedPlanetIndex: Int) {
    val context = LocalContext.current
    val rendererRef = remember { mutableStateOf<PlanetGLRenderer?>(null) }
    AndroidView(
        factory = { ctx ->
            GLSurfaceView(ctx).apply {
                setEGLContextClientVersion(1)
                val renderer = PlanetGLRenderer(context)
                setRenderer(renderer)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                rendererRef.value = renderer
                this
            }
        },
        modifier = Modifier.fillMaxSize().clipToBounds()
    )

    LaunchedEffect(selectedPlanetIndex) {
        rendererRef.value?.setSelectedPlanetIndex(selectedPlanetIndex)
    }
}
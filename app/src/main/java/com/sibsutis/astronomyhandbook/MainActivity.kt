package com.sibsutis.astronomyhandbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sibsutis.astronomyhandbook.ui.screens.NewsScreen
import com.sibsutis.astronomyhandbook.ui.screens.OpenGLScreen
import com.sibsutis.astronomyhandbook.ui.theme.AstronomyHandbookTheme
import com.sibsutis.astronomyhandbook.viewmodel.NewsViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AstronomyHandbookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: NewsViewModel = viewModel()

                    var showOpenGL by remember { mutableStateOf(false) }

                    if (!showOpenGL) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            TopAppBar(
                                title = { Text("Астрономический справочник") },
                                actions = {
                                    Button(
                                        onClick = { showOpenGL = true },
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text("3D")
                                    }
                                }
                            )

                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                NewsScreen(viewModel = viewModel)
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            TopAppBar(
                                title = { Text("Вернуться к новостям") },
                                navigationIcon = {
                                    IconButton(onClick = { showOpenGL = false }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.left_arrow_icon),
                                            contentDescription = "Назад",
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )

                            Box(
                                modifier = Modifier.weight(1f).clipToBounds()
                            ) {
                                OpenGLScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
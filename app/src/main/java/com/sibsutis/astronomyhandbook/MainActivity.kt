package com.sibsutis.astronomyhandbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sibsutis.astronomyhandbook.ui.screens.NewsScreen
import com.sibsutis.astronomyhandbook.ui.screens.SolarSystemScreen
import com.sibsutis.astronomyhandbook.ui.screens.PlanetScreen
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
                    var showPlanetDetail by remember { mutableStateOf(false) }
                    var selectedDetailObjectName by remember { mutableStateOf("") }

                    val objectNames = listOf("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Moon")

                    var selectedObjectIndex by remember { mutableStateOf(0) }

                    if (showPlanetDetail) {
                        PlanetScreen(
                            objectName = selectedDetailObjectName,
                            onBackPressed = { showPlanetDetail = false }
                        )
                    } else if (!showOpenGL) {
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
                        Box(modifier = Modifier.fillMaxSize()) {
                            SolarSystemScreen(selectedObjectIndex  = selectedObjectIndex)

                            // Нижняя панель с кнопками
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = {
                                        selectedObjectIndex = (selectedObjectIndex  - 1 + objectNames.size) % objectNames.size
                                    }
                                ) {
                                    Text(text = "←", fontSize = 24.sp)
                                }
                                Button(
                                    onClick = {
                                        val currentName = objectNames[selectedObjectIndex]
                                        selectedDetailObjectName = currentName
                                        showPlanetDetail = true
                                    }
                                ) {
                                    Text(text = "Info", fontSize = 24.sp)
                                }
                                Button(
                                    onClick = {
                                        selectedObjectIndex = (selectedObjectIndex + 1) % objectNames.size
                                    }
                                ) {
                                    Text(text = "→", fontSize = 24.sp);
                                }
                            }

                            // Кнопка "Назад" в верхнем левом углу
                            IconButton(
                                onClick = { showOpenGL = false },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.left_arrow_icon),
                                    contentDescription = "Назад",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
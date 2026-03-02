package com.sibsutis.astronomyhandbook

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    val planetNames = listOf("Меркурий", "Венера", "Земля", "Марс", "Юпитер", "Сатурн")
                    var selectedPlanetIndex by remember { mutableStateOf(0) }

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
                        Box(modifier = Modifier.fillMaxSize()) {
                            OpenGLScreen(selectedPlanetIndex = selectedPlanetIndex)

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
                                        selectedPlanetIndex = (selectedPlanetIndex - 1 + planetNames.size) % planetNames.size
                                    }
                                ) {
                                    Text(text = "←", fontSize = 24.sp)
                                }
                                Button(
                                    onClick = {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Выбрана планета: ${planetNames[selectedPlanetIndex]}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                ) {
                                    Text(text = "Info", fontSize = 24.sp)
                                }
                                Button(
                                    onClick = {
                                        selectedPlanetIndex = (selectedPlanetIndex + 1) % planetNames.size
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
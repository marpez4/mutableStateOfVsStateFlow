package com.example.temperaturaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.temperaturaapp.ui.theme.TemperaturaAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val viewModel = WeatherViewModel()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemperaturaAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreenStateFlow(viewModel = viewModel)
                    // WeatherScreen()
                }
            }
        }
    }
}

val temperatures = listOf(20, 25, 30, 28)

@Composable
fun WeatherScreen() {
    var temperature by remember { mutableStateOf(temperatures.first()) }

    LaunchedEffect(key1 = Unit) {
        var index = 0
        while (true) {
            delay(2000)
            index = (index + 1) % temperatures.size
            temperature = temperatures[index]
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Temperatura actual: $temperature °C",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

class WeatherViewModel : ViewModel() {
    private val _temperatures = listOf(20, 25, 30, 28) // Arreglo de temperaturas
    private val _currentIndex = MutableStateFlow(0) // Índice actual en el arreglo

    // Usamos map para crear un flujo de temperaturas y lo convertimos a StateFlow con stateIn
    val temperature: StateFlow<Int> = _currentIndex
        .map { _temperatures[it] }
        .stateIn(viewModelScope, SharingStarted.Lazily, _temperatures[0])

    init {
        viewModelScope.launch {
            while (true) {
                delay(2000)
                _currentIndex.value = (_currentIndex.value + 1) % _temperatures.size
            }
        }
    }
}

@Composable
fun WeatherScreenStateFlow(viewModel: WeatherViewModel) {
    val temperature by viewModel.temperature.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Temperatura actual: $temperature °C",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

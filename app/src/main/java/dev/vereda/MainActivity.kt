package dev.vereda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.vereda.ui.home.HomeRoute
import dev.vereda.ui.home.HomeViewModel
import dev.vereda.ui.theme.VeredaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as VeredaApplication).container
        setContent {
            VeredaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val homeViewModel: HomeViewModel =
                        viewModel(
                            factory =
                                viewModelFactory {
                                    initializer {
                                        HomeViewModel(
                                            streakRepository = container.streakRepository,
                                            progressRepository = container.progressRepository,
                                        )
                                    }
                                },
                        )
                    HomeRoute(
                        viewModel = homeViewModel,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

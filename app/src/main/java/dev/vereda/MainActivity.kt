package dev.vereda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.vereda.di.AppContainer
import dev.vereda.ui.home.HomeRoute
import dev.vereda.ui.home.HomeViewModel
import dev.vereda.ui.reading.ReadingRoute
import dev.vereda.ui.reading.ReadingViewModel
import dev.vereda.ui.theme.VeredaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as VeredaApplication).container
        setContent {
            VeredaTheme {
                VeredaApp(container)
            }
        }
    }
}

/**
 * Top-level UI: shows the Home screen and, while reading, the reading screen for a chapter.
 *
 * Navigation is a lightweight in-Activity state switch for now; Navigation Compose will be adopted
 * once the book list and chapter grid screens (with a real back stack) are added.
 */
@Composable
private fun VeredaApp(container: AppContainer) {
    // Negative book id means "on the Home screen"; otherwise we are reading that chapter.
    var readingBookId by rememberSaveable { mutableStateOf(-1) }
    var readingChapter by rememberSaveable { mutableStateOf(-1) }

    val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory(container))

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        if (readingBookId > 0) {
            val readingViewModel: ReadingViewModel =
                viewModel(
                    key = "reading-$readingBookId-$readingChapter",
                    factory = readingViewModelFactory(container, readingBookId, readingChapter),
                )
            BackHandler {
                readingBookId = -1
                homeViewModel.refresh()
            }
            ReadingRoute(viewModel = readingViewModel, modifier = contentModifier)
        } else {
            HomeRoute(
                viewModel = homeViewModel,
                // Placeholder entry point until the book navigation screen lands: open Genesis 1.
                onContinueReading = {
                    readingBookId = 1
                    readingChapter = 1
                },
                modifier = contentModifier,
            )
        }
    }
}

private fun homeViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            HomeViewModel(
                streakRepository = container.streakRepository,
                progressRepository = container.progressRepository,
            )
        }
    }

private fun readingViewModelFactory(
    container: AppContainer,
    bookId: Int,
    chapter: Int,
): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            ReadingViewModel(
                bookId = bookId,
                chapter = chapter,
                readingRepository = container.bibleReadingRepository,
                progressRepository = container.progressRepository,
                streakRepository = container.streakRepository,
            )
        }
    }

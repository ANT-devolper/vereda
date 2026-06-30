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
import dev.vereda.ui.books.BooksRoute
import dev.vereda.ui.books.BooksViewModel
import dev.vereda.ui.chapters.ChaptersRoute
import dev.vereda.ui.chapters.ChaptersViewModel
import dev.vereda.ui.home.HomeRoute
import dev.vereda.ui.home.HomeViewModel
import dev.vereda.ui.reading.ReadingRoute
import dev.vereda.ui.reading.ReadingViewModel
import dev.vereda.ui.settings.RemindersRoute
import dev.vereda.ui.settings.RemindersViewModel
import dev.vereda.ui.theme.VeredaTheme

private const val ROUTE_HOME = "home"
private const val ROUTE_BOOKS = "books"
private const val ROUTE_CHAPTERS = "chapters"
private const val ROUTE_READING = "reading"
private const val ROUTE_SETTINGS = "settings"

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
 * Top-level UI: drills down Home → books → chapters → reading.
 *
 * Navigation is a lightweight in-Activity state machine for now; Navigation Compose will be adopted
 * once the back stack grows richer. Each forward step bumps a reload token so a screen re-reads
 * progress when revisited (e.g. after completing a chapter).
 */
@Composable
private fun VeredaApp(container: AppContainer) {
    var route by rememberSaveable { mutableStateOf(ROUTE_HOME) }
    var selectedBookId by rememberSaveable { mutableStateOf(-1) }
    var selectedChapter by rememberSaveable { mutableStateOf(-1) }
    var booksToken by rememberSaveable { mutableStateOf(0) }
    var chaptersToken by rememberSaveable { mutableStateOf(0) }

    val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory(container))

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (route) {
            ROUTE_BOOKS -> {
                val booksViewModel: BooksViewModel =
                    viewModel(key = "books-$booksToken", factory = booksViewModelFactory(container))
                BackHandler {
                    route = ROUTE_HOME
                    homeViewModel.refresh()
                }
                BooksRoute(
                    viewModel = booksViewModel,
                    onBookClick = { bookId ->
                        selectedBookId = bookId
                        chaptersToken++
                        route = ROUTE_CHAPTERS
                    },
                    modifier = contentModifier,
                )
            }

            ROUTE_CHAPTERS -> {
                val chaptersViewModel: ChaptersViewModel =
                    viewModel(
                        key = "chapters-$selectedBookId-$chaptersToken",
                        factory = chaptersViewModelFactory(container, selectedBookId),
                    )
                BackHandler {
                    booksToken++
                    route = ROUTE_BOOKS
                }
                ChaptersRoute(
                    viewModel = chaptersViewModel,
                    onChapterClick = { chapter ->
                        selectedChapter = chapter
                        route = ROUTE_READING
                    },
                    modifier = contentModifier,
                )
            }

            ROUTE_READING -> {
                val readingViewModel: ReadingViewModel =
                    viewModel(
                        key = "reading-$selectedBookId-$selectedChapter",
                        factory = readingViewModelFactory(container, selectedBookId, selectedChapter),
                    )
                BackHandler {
                    chaptersToken++
                    route = ROUTE_CHAPTERS
                }
                ReadingRoute(viewModel = readingViewModel, modifier = contentModifier)
            }

            ROUTE_SETTINGS -> {
                val remindersViewModel: RemindersViewModel =
                    viewModel(factory = remindersViewModelFactory(container))
                BackHandler { route = ROUTE_HOME }
                RemindersRoute(viewModel = remindersViewModel, modifier = contentModifier)
            }

            else -> {
                HomeRoute(
                    viewModel = homeViewModel,
                    onChooseReading = {
                        booksToken++
                        route = ROUTE_BOOKS
                    },
                    onOpenSettings = { route = ROUTE_SETTINGS },
                    modifier = contentModifier,
                )
            }
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

private fun booksViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            BooksViewModel(progressRepository = container.progressRepository)
        }
    }

private fun remindersViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            RemindersViewModel(reminderRepository = container.reminderRepository)
        }
    }

private fun chaptersViewModelFactory(
    container: AppContainer,
    bookId: Int,
): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            ChaptersViewModel(
                bookId = bookId,
                catalog = container.bibleCatalog,
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

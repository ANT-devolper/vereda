package dev.vereda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import dev.vereda.ui.history.HistoryRoute
import dev.vereda.ui.history.HistoryViewModel
import dev.vereda.ui.home.HomeRoute
import dev.vereda.ui.home.HomeViewModel
import dev.vereda.ui.onboarding.OnboardingRoute
import dev.vereda.ui.onboarding.OnboardingViewModel
import dev.vereda.ui.reading.ReadingRoute
import dev.vereda.ui.reading.ReadingViewModel
import dev.vereda.ui.settings.RemindersRoute
import dev.vereda.ui.settings.RemindersViewModel
import dev.vereda.ui.theme.VeredaTheme

private const val ROUTE_ONBOARDING = "onboarding"
private const val ROUTE_HOME = "home"
private const val ROUTE_BOOKS = "books"
private const val ROUTE_CHAPTERS = "chapters"
private const val ROUTE_READING = "reading"
private const val ROUTE_HISTORY = "history"
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
 * Top-level UI: first run shows onboarding, then drills down Home → books → chapters → reading.
 *
 * Navigation is a lightweight in-Activity state machine for now; Navigation Compose will be adopted
 * once the back stack grows richer. Each forward step bumps a reload token so a screen re-reads
 * progress when revisited (e.g. after completing a chapter). The start route is `null` until the
 * onboarding-completed flag is read.
 */
@Composable
private fun VeredaApp(container: AppContainer) {
    var route by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedBookId by rememberSaveable { mutableStateOf(-1) }
    var selectedChapter by rememberSaveable { mutableStateOf(-1) }
    var booksToken by rememberSaveable { mutableStateOf(0) }
    var chaptersToken by rememberSaveable { mutableStateOf(0) }
    var historyToken by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        if (route == null) {
            route = if (container.onboardingRepository.isCompleted()) ROUTE_HOME else ROUTE_ONBOARDING
        }
    }

    val homeViewModel: HomeViewModel = viewModel(factory = homeViewModelFactory(container))

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (route) {
            null -> {
                Box(
                    modifier = contentModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            ROUTE_ONBOARDING -> {
                val onboardingViewModel: OnboardingViewModel =
                    viewModel(factory = onboardingViewModelFactory(container))
                OnboardingRoute(
                    viewModel = onboardingViewModel,
                    onFinished = {
                        route = ROUTE_HOME
                        homeViewModel.refresh()
                    },
                    modifier = contentModifier,
                )
            }

            ROUTE_BOOKS -> {
                val booksViewModel: BooksViewModel =
                    viewModel(key = "books-$booksToken", factory = booksViewModelFactory(container))
                val back = {
                    route = ROUTE_HOME
                    homeViewModel.refresh()
                }
                BackHandler { back() }
                BooksRoute(
                    viewModel = booksViewModel,
                    onBack = back,
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
                val back = {
                    booksToken++
                    route = ROUTE_BOOKS
                }
                BackHandler { back() }
                ChaptersRoute(
                    viewModel = chaptersViewModel,
                    onBack = back,
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
                val back = {
                    chaptersToken++
                    route = ROUTE_CHAPTERS
                }
                BackHandler { back() }
                ReadingRoute(
                    viewModel = readingViewModel,
                    onBack = back,
                    onNavigateToChapter = { bookId, chapter ->
                        selectedBookId = bookId
                        selectedChapter = chapter
                        chaptersToken++
                    },
                    modifier = contentModifier,
                )
            }

            ROUTE_SETTINGS -> {
                val remindersViewModel: RemindersViewModel =
                    viewModel(factory = remindersViewModelFactory(container))
                val back = { route = ROUTE_HOME }
                BackHandler { back() }
                RemindersRoute(viewModel = remindersViewModel, onBack = back, modifier = contentModifier)
            }

            ROUTE_HISTORY -> {
                val historyViewModel: HistoryViewModel =
                    viewModel(key = "history-$historyToken", factory = historyViewModelFactory(container))
                val back = { route = ROUTE_HOME }
                BackHandler { back() }
                HistoryRoute(viewModel = historyViewModel, onBack = back, modifier = contentModifier)
            }

            else -> {
                HomeRoute(
                    viewModel = homeViewModel,
                    onChooseReading = {
                        booksToken++
                        route = ROUTE_BOOKS
                    },
                    onViewHistory = {
                        historyToken++
                        route = ROUTE_HISTORY
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

private fun historyViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            HistoryViewModel(readingHistoryRepository = container.readingHistoryRepository)
        }
    }

private fun remindersViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            RemindersViewModel(
                reminderRepository = container.reminderRepository,
                reminderScheduler = container.reminderScheduler,
            )
        }
    }

private fun onboardingViewModelFactory(container: AppContainer): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            OnboardingViewModel(
                reminderRepository = container.reminderRepository,
                onboardingRepository = container.onboardingRepository,
                reminderScheduler = container.reminderScheduler,
            )
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
                bibleCatalog = container.bibleCatalog,
            )
        }
    }

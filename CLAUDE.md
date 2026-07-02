# CLAUDE.md

Guidance for working in this repository. This file applies to both human contributors and AI assistants.

## Project

**Vereda** is a native **Android** app for gamified Bible reading, inspired by Duolingo: the user reads at
least **one chapter per day**, keeps a daily **streak**, and gets a **daily reminder notification** so they
don't forget. The app is **offline-first** — reading progress and streak live on the device, and the Bible
text is bundled with the app.

## Tech stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM (`ViewModel` + `StateFlow`), single-Activity, Navigation Compose
- **Local storage:** Room (SQLite) for read chapters and daily activity; DataStore (Preferences) for settings
- **Bible content:** bundled SQLite database in `assets/` (pre-populated, loaded via `Room.createFromAsset`)
- **Daily reminder:** local scheduled notifications (AlarmManager + NotificationCompat), no server
- **Build:** Gradle (Kotlin DSL `build.gradle.kts`) with a Version Catalog (`libs.versions.toml`)

## Language convention

**English is the project's working language.** All source code, documentation, README, commit messages,
code comments, identifiers, and issues/PRs must be written in English.

## Development methodology: TDD

This project follows **test-driven development**:

1. **Test first** — every implementation starts with a test describing the expected behavior.
2. **Red → green → refactor** — write a failing test, write the minimum code to make it pass, then refactor
   to improve the code while keeping the suite green.
3. **Bugs become regression tests** — every fixed bug starts with a test that reproduces it, so it can't
   silently return.
4. **Definition of done** — when an implementation is finished, in this order:
   1. Run the full test suite (all green).
   2. Go through **code review**.
   3. Run the **linter** (ktlint).
   4. **Build a debug APK** (`./gradlew assembleDebug`) for the user to manually test on Android
      (APK at `app/build/outputs/apk/debug/app-debug.apk`).
   5. **Wait for the user's approval** after manual testing before continuing.
   6. Once approved, **update `CLAUDE.md`** — its "Current state" and code map/history — to reflect
      the change.
   7. **Commit** the work (Conventional Commits, atomic).

## Commits

- Follow **Conventional Commits**: `feat:`, `fix:`, `test:`, `refactor:`, `docs:`, `chore:`, etc.
- Keep commits **atomic and small** — each commit is one coherent, isolated change.
- Write commit messages in English.

## Quality tooling

- **Lint/format:** ktlint (auto-fix where possible)
- **Tests:** JUnit (unit) + Compose UI testing
  - Unit tests: `./gradlew test`
  - Instrumented/UI tests: `./gradlew connectedAndroidTest`
- **CI:** not set up yet — validate tests and lint locally. GitHub Actions may be added as the project matures.

## MVP scope

- **Free navigation:** the user freely chooses which chapter to read (no fixed plan). The daily goal is to
  read at least one chapter per day; any chapter completed that day keeps the streak.
- **Completion:** scrolling to the end of a chapter reveals a **"Mark as completed"** button; completion is
  recorded only when the user taps it.
- **Streak:** missing a day resets the streak to zero (streak freeze/protection is a future iteration).
- **Gamification (MVP):** streak + day counter (current streak, best streak, visual "flame") and reading
  progress per book and overall.
- **Reminders:** the user manually manages **up to 3 daily reminders** (each a time of day, repeated
  daily); reminders are added, edited and removed in a **Settings** screen. There is no fixed/single
  reminder. Saving reminders schedules them as inexact daily alarms (AlarmManager) that post a local
  notification; alarms are re-scheduled after reboot via a `BOOT_COMPLETED` receiver.
- **No account/login** (offline-first). Onboarding (first run) seeds the initial reminders (one suggested
  time, editable) and requests notification permission.

### Data model

- **Room** `chapter_read` (`bookId`, `chapter`, `firstReadAt`; unique per `bookId+chapter`) — basis for
  per-book and overall progress (distinct chapters read).
- **Room** `daily_activity` (unique local `date`, `chaptersCompleted`) — basis for the streak (consecutive
  days with at least one completed chapter); current/best streak is derived from the recorded dates.
- **DataStore (Preferences)** for settings: the up-to-3 daily reminder times and the onboarding-completed
  flag.

### Out of scope (future iterations)

- Achievements/badges, XP/levels, and streak freeze/protection.

## Current state (pre-context)

The MVP is implemented end-to-end and navigable: **Onboarding → Home → Books → Chapters → Reading →
"Mark as completed"**, with streak, progress and **real reminder scheduling/firing** (alarms + boot
reschedule). All work is committed on `main`; the latest debug APK builds and installs. The test suite
(JUnit + Robolectric + coroutines-test) is green and ktlint is clean.

Post-MVP features added on `main`:

- **Top-bar back navigation:** a shared `ui/components/VeredaTopBar` (title + standard Android back
  arrow `Icons.AutoMirrored.Filled.ArrowBack`) wires a back action into Books, Chapters, Reading and
  Reminders; `MainActivity` hoists a `val back = { … }` per screen shared by the `BackHandler` and the
  top bar's `onBack`.
- **Next-chapter reading flow:** after "Mark as completed" the Reading screen shows a **"Próximo
  capítulo"** button. `ReadingViewModel.nextChapterAfter` (uses `BibleCatalog`) resolves the next
  chapter of the book, or chapter 1 of the next book, or `null` at the end of the last book
  (`ChapterTarget` in `reading/Reading.kt`); scroll resets per chapter via `key(bookId, chapter)`.
- **Reading history:** a `history` screen (reachable from Home) lists completed chapters grouped by
  local day, most-recent first, with completion time. Derived from existing `chapter_read` rows — no
  new table/migration (a chapter is markable only once, so `firstReadAt` is its completion instant).

### Code map (`app/src/main/java/dev/vereda`)

- **Entry / DI / nav:** `VeredaApplication` (holds `DefaultAppContainer`), `MainActivity`
  (`VeredaApp(container)` — a manual `rememberSaveable` route state machine: `ROUTE_ONBOARDING/HOME/
  BOOKS/CHAPTERS/READING/HISTORY/SETTINGS`, with `BackHandler`s and per-screen reload tokens
  (`booksToken`/`chaptersToken`/`historyToken`); **Navigation Compose deliberately deferred**).
  `di/AppContainer` — interface + `DefaultAppContainer` wiring Room, the shared DataStore and the
  scheduler.
- **`data/`** — Room. `VeredaDatabase` (`vereda.db`) holds `chapter_read` + `daily_activity` (DAOs,
  entities, `Instant`/`LocalDate` converters). `BibleContentDatabase` (`bible-content.db` via
  `createFromAsset("bible.db")`) holds verses. Repositories: `StreakRepository`, `ProgressRepository`,
  `ReadingHistoryRepository`, `BibleReadingRepository` (interfaces + `Default…` impls). `ChapterReadDao`
  also exposes `allReads()` (`ORDER BY firstReadAt DESC`) feeding the history.
- **`progress/`** — `BibleCatalog` + `PortugueseBibleCatalog` (book/chapter counts), `ProgressCalculator`.
- **`streak/`** — `StreakCalculator` (current/best streak derived from `daily_activity` dates).
- **`history/`** — `ReadingHistory` domain (`HistoryEntry`/`HistoryDay`) + `HistoryCalculator` (pure:
  groups `chapter_read` rows by local day via an injected `ZoneId`, days desc / entries by time asc).
- **`reading/`** — `Reading` domain (scroll-to-end completion model, `ChapterTarget` for next-chapter).
- **`settings/`** — `ReminderRepository` (`stringSetPreferencesKey("reminders")`, `MAX_REMINDERS = 3`,
  normalize = distinct/sorted/take 3), `OnboardingRepository`
  (`booleanPreferencesKey("onboarding_completed")`), `ReminderEditing` (pure add/update/remove keeping
  ≤3, no dupes, sorted).
- **`reminders/`** — `ReminderScheduling` (pure `nextOccurrence`), `ReminderScheduler` interface +
  `AlarmReminderScheduler` (`setInexactRepeating`, `INTERVAL_DAY`, `RTC_WAKEUP`; per-slot
  PendingIntent request codes 0..2; avoids `SCHEDULE_EXACT_ALARM`), `ReminderNotifier`
  (`NotificationChannelCompat` `CHANNEL_ID = "reading_reminders"`), `ReminderReceiver` (alarm → notify),
  `BootReceiver` (`BOOT_COMPLETED` → reschedule via `goAsync` + coroutine).
- **`ui/`** — Compose screens, each `…Route(viewModel, …)` + stateless `…Screen(state, …)`: `home`
  (streak/progress + "Escolher leitura"/"Histórico"/"Ajustes"), `books`, `chapters`, `reading`,
  `history` (day-grouped `LazyColumn`; `pt-BR` date via `DateTimeFormatter`), `settings` (Reminders;
  `ReminderListEditor` + Material3 `TimePickerDialog`), `onboarding` (seeds 08:00, requests
  `POST_NOTIFICATIONS`), `components` (`VeredaTopBar`), `theme`.

### Manifest

Permissions `POST_NOTIFICATIONS` + `RECEIVE_BOOT_COMPLETED`; `android:name=".VeredaApplication"`;
receivers `.reminders.ReminderReceiver` (not exported) and `.reminders.BootReceiver` (exported, with
the `BOOT_COMPLETED` intent-filter). App icon `android:icon="@drawable/ic_launcher"` — a single
(non-adaptive) vector drawable: a white Latin cross centered on a solid black background.

### Conventions in use

- **Manual DI** via `AppContainer`; ViewModels built with `viewModelFactory { initializer { … } }` in
  `MainActivity`.
- **`java.time`** throughout (enabled via core library desugaring).
- Avoided extra deps: only the small `material-icons-core` (for the back arrow) is used — **not**
  `material-icons-extended`; also no Navigation Compose and no Hilt. Note `material-icons-core` is no
  longer provided transitively by `material3`, so it is declared explicitly (versioned by the Compose
  BOM). DataStore pinned to **1.1.7** (`minCompileSdk=34`; do **not** bump to 1.3.0-alpha).
- Bundled Bible text is **Bíblia Livre (CC BY 4.0)** — only public-domain/freely-licensed text;
  attribution is in the repo.

### Build & test (see also [[build-from-wsl]])

- No `java`/`gradle` on PATH in WSL: `export JAVA_HOME=/opt/android-studio/jbr` then
  `./gradlew … --no-daemon` (uses Android Studio's bundled JBR + SDK).
- `compileSdk=36`, `minSdk=24`, `targetSdk=36`. Kotlin DSL + Version Catalog (`libs.versions.toml`).
- Commands: `./gradlew testDebugUnitTest` (unit), `./gradlew ktlintCheck`, `./gradlew assembleDebug`
  (APK at `app/build/outputs/apk/debug/app-debug.apk`).
- **ktlint quirk:** `ktlintFormat` must run in **separate** gradle invocations to converge — running it
  twice in one invocation makes the second pass a no-op (up-to-date).
- DataStore/settings tests run as pure JVM via `PreferenceDataStoreFactory.create(produceFile = { tmp })`
  + JUnit `TemporaryFolder`; scheduler/notifier tests use Robolectric (`shadowOf` AlarmManager /
  NotificationManager).

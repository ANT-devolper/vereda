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
4. **Definition of done** — when an implementation is finished: run the full test suite (all green) → go
   through **code review** → run the **linter** before completing or merging.

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
  reminder. *(Actually scheduling and firing the notifications — AlarmManager + boot reschedule — is a
  future iteration; for now reminders are only configured and persisted.)*
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

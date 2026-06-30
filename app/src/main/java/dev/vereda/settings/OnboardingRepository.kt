package dev.vereda.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first

/** Persists whether the user has finished the first-run onboarding. */
interface OnboardingRepository {
    suspend fun isCompleted(): Boolean

    suspend fun complete()
}

/** DataStore-backed [OnboardingRepository]. */
class DefaultOnboardingRepository(
    private val dataStore: DataStore<Preferences>,
) : OnboardingRepository {
    override suspend fun isCompleted(): Boolean = dataStore.data.first()[KEY] ?: false

    override suspend fun complete() {
        dataStore.edit { preferences -> preferences[KEY] = true }
    }

    private companion object {
        val KEY = booleanPreferencesKey("onboarding_completed")
    }
}

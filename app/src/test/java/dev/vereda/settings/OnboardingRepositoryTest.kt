package dev.vereda.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** Persists the onboarding-completed flag through a real Preferences DataStore (pure JVM). */
class OnboardingRepositoryTest {
    @get:Rule
    val tmp = TemporaryFolder()

    private val store: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.create(produceFile = { tmp.newFile("onboarding.preferences_pb") })
    }
    private val repository: OnboardingRepository by lazy { DefaultOnboardingRepository(store) }

    @Test
    fun `is not completed by default`() =
        runTest {
            assertFalse(repository.isCompleted())
        }

    @Test
    fun `complete marks onboarding as completed`() =
        runTest {
            repository.complete()

            assertTrue(repository.isCompleted())
        }
}

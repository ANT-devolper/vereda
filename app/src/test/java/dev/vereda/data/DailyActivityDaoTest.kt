package dev.vereda.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/**
 * DAO tests for daily reading activity, run on the JVM via Robolectric with an in-memory database.
 * This is the persistence source for the reading streak (one row per day with a completed chapter).
 */
@RunWith(AndroidJUnit4::class)
class DailyActivityDaoTest {
    private lateinit var database: VeredaDatabase
    private lateinit var dao: DailyActivityDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, VeredaDatabase::class.java).build()
        dao = database.dailyActivityDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `returns activity dates sorted ascending`() =
        runBlocking {
            dao.upsert(DailyActivity(date = LocalDate.of(2026, 6, 28), chaptersCompleted = 1))
            dao.upsert(DailyActivity(date = LocalDate.of(2026, 6, 30), chaptersCompleted = 2))
            dao.upsert(DailyActivity(date = LocalDate.of(2026, 6, 29), chaptersCompleted = 1))

            val dates = dao.getActivityDates()

            assertEquals(
                listOf(
                    LocalDate.of(2026, 6, 28),
                    LocalDate.of(2026, 6, 29),
                    LocalDate.of(2026, 6, 30),
                ),
                dates,
            )
        }

    @Test
    fun `getByDate returns the stored row`() =
        runBlocking {
            val activity = DailyActivity(date = LocalDate.of(2026, 6, 30), chaptersCompleted = 3)
            dao.upsert(activity)

            assertEquals(activity, dao.getByDate(LocalDate.of(2026, 6, 30)))
        }

    @Test
    fun `getByDate returns null for a day without activity`() =
        runBlocking {
            assertNull(dao.getByDate(LocalDate.of(2026, 6, 30)))
        }

    @Test
    fun `upsert on the same date replaces the row`() =
        runBlocking {
            val date = LocalDate.of(2026, 6, 30)
            dao.upsert(DailyActivity(date = date, chaptersCompleted = 1))
            dao.upsert(DailyActivity(date = date, chaptersCompleted = 5))

            assertEquals(listOf(date), dao.getActivityDates())
            assertEquals(5, dao.getByDate(date)?.chaptersCompleted)
        }
}

package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersDatabase: RemindersDatabase

    @Before
    fun initDb() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = remindersDatabase.close()

    @Test
    fun insertAndGetData() = runBlockingTest {
        var reminder = ReminderDTO(
            "Title",
            "Desc",
            "cairo",
            40.43,
            56.23
        )
        remindersDatabase.reminderDao().saveReminder(reminder)
        var loadedData = remindersDatabase.reminderDao().getReminderById(reminder.id)
        assertThat<ReminderDTO>(loadedData as ReminderDTO, notNullValue())
        assertThat(loadedData.id, `is`(reminder.id))
        assertThat(loadedData.title, `is`(reminder.title))
        assertThat(loadedData.description, `is`(reminder.description))
        assertThat(loadedData.location, `is`(reminder.location))
        assertThat(loadedData.latitude, `is`(reminder.latitude))
        assertThat(loadedData.longitude, `is`(reminder.longitude))
    }
}


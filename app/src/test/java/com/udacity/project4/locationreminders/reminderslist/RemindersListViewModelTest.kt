package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(maxSdk = Build.VERSION_CODES.R)
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects


    @get:Rule
    val mainCoroutineRule= MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var remindersListViewModel : RemindersListViewModel

    @Before
    fun createReminderList(){
        stopKoin()
        fakeDataSource = FakeDataSource()
        remindersListViewModel= RemindersListViewModel(ApplicationProvider.getApplicationContext()
        ,fakeDataSource)
    }

    @Test
    fun loadNoReminders(){
        fakeDataSource.shouldReturnError()
        remindersListViewModel.loadReminders()
        var value=remindersListViewModel.showSnackBar.getOrAwaitValue()
        fakeDataSource.shouldReturnErrorCompleted()
        assertThat(value,notNullValue())
    }

    @Test
    fun loadReminders_Loading_Corotine(){
        mainCoroutineRule.pauseDispatcher()
        fakeDataSource.shouldReturnError()
        remindersListViewModel.loadReminders()
        var valueBefore=remindersListViewModel.showLoading.getOrAwaitValue()
        assertThat(valueBefore,`is`(true))

        mainCoroutineRule.resumeDispatcher()

        var valueAfter=remindersListViewModel.showLoading.getOrAwaitValue()

        fakeDataSource.shouldReturnErrorCompleted()
        assertThat(valueAfter,`is`(false))

    }


    @Test
    fun load_Reminders_WithData(){

        var reminder=ReminderDTO(
            "Title",
            "desc",
            "cairo",
            30.4443,
            43.6534
        )
        runBlocking{fakeDataSource.saveReminder(reminder)}
        remindersListViewModel.loadReminders()
        var value=remindersListViewModel.remindersList.getOrAwaitValue()
        assertThat(value, notNullValue())
    }

}
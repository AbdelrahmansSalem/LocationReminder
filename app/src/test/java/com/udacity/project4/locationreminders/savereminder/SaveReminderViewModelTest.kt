package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
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
class SaveReminderViewModelTest {


    //TODO: provide testing to the SaveReminderView and its live data objects
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule=MainCoroutineRule()

    lateinit var fakeDataSource: FakeDataSource
    lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun createSaveReieminder() {
        stopKoin()
        fakeDataSource = FakeDataSource()
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
        saveReminderViewModel.onClear()
    }


    @Test
    fun SaveRemindertest() {
        var reminderDataItem = ReminderDataItem(
            "title",
            "description",
            "location",
            30.343,
            30.565
        )
        saveReminderViewModel.saveReminder(reminderDataItem)
        var value = saveReminderViewModel.showToast.getOrAwaitValue()
        assertThat(value, not(nullValue()))
    }

    @Test
    fun saveReminder_emptyLocation_returnNull() {

        val reminderDataItem = ReminderDataItem(
            "title",
            "description",
            "",
            30.343,
            30.565
        )

        saveReminderViewModel.validateAndSaveReminder(reminderDataItem)
        var value = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        assertThat(value, `is`(notNullValue()))

    }


    @Test
    fun saveReminder_emptyTitle_returnNull() {

        val reminderDataItem = ReminderDataItem(
            "",
            "description",
            "locatiom",
            30.343,
            30.565
        )

        saveReminderViewModel.validateAndSaveReminder(reminderDataItem)
        var value = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        assertThat(value, `is`(notNullValue()))
    }

    @Test
    fun  saveReminder_Loading_Corotine(){
        mainCoroutineRule.pauseDispatcher()
        val reminderDataItem = ReminderDataItem(
            "title",
            "description",
            "locatiom",
            30.343,
            30.565
        )
        saveReminderViewModel.saveReminder(reminderDataItem)
        var valueBefore=saveReminderViewModel.showLoading.getOrAwaitValue()
        assertThat(valueBefore,`is`(true))

        mainCoroutineRule.resumeDispatcher()

        var valueAfter=saveReminderViewModel.showLoading.getOrAwaitValue()
        assertThat(valueAfter,`is`(false))
    }
}
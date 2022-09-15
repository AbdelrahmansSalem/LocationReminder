package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result


//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders:MutableList<ReminderDTO>?= mutableListOf()) : ReminderDataSource {

//    TODO: Create a fake data source to act as a double to the real data source
   private var shouldReturnError = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
            if (!shouldReturnError){
                return Result.Success(ArrayList(reminders))
            }
        return Result.Error("No Reminders found")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        var reminder=reminders?.find {
            it.id==id
        }

        if (reminder == null) {
            return Result.Error("Reminder not found")

        } else {
            return Result.Success(reminder)
        }
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }


    fun shouldReturnError() {
        this.shouldReturnError = true
    }
    fun shouldReturnErrorCompleted() {
        this.shouldReturnError = false
    }
}
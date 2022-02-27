package com.sameeraw.remindbuddy.data.store

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sameeraw.remindbuddy.data.dao.ReminderDao
import com.sameeraw.remindbuddy.data.entity.Reminder

@Database(
    entities = [Reminder::class],
    version = 6,
    exportSchema = false,

)
abstract class RemindBuddyDatabase : RoomDatabase() {
    abstract val reminderDao: ReminderDao
}
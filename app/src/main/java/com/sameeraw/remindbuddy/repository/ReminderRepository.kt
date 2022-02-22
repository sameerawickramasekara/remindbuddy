package com.sameeraw.remindbuddy.repository

import com.sameeraw.remindbuddy.data.dao.ReminderDao
import com.sameeraw.remindbuddy.data.entity.Reminder
import kotlinx.coroutines.flow.Flow

/**
 * Reminder repository for interacting with database
 */
class ReminderRepository(
    private val reminderDao: ReminderDao
) {
    suspend fun insert(entity: Reminder): Long {
        return reminderDao.insert(entity)
    }

    fun getAllReminders():Flow<List<Reminder>>{
        return reminderDao.getReminders()
    }

    suspend fun getReminderById(reminderId:Long):Reminder?{
        return reminderDao.getReminderById(reminderId)
    }

    suspend fun insertAll(entities: Collection<Reminder>) {
        return reminderDao.insertAll(entities)
    }

    suspend fun update(entity: Reminder) {
        return reminderDao.update(entity)
    }

    suspend fun delete(entity: Reminder): Int {
        return reminderDao.delete(entity)
    }
}
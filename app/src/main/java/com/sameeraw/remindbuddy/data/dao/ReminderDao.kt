package com.sameeraw.remindbuddy.data.dao

import androidx.room.*
import com.sameeraw.remindbuddy.data.entity.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ReminderDao {

    @Query(
        value = "SELECT * FROM reminder"
    )
    abstract fun getReminders():Flow<List<Reminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Reminder): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(entities: Collection<Reminder>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: Reminder)

    @Delete
    abstract suspend fun delete(entity: Reminder): Int

    @Query(
        value = "SELECT * FROM reminder WHERE id=:reminderId"
    )
    abstract suspend fun getReminderById(reminderId: Long): Reminder?
}
package com.sameeraw.remindbuddy.di

import android.app.Application
import androidx.room.Room
import com.sameeraw.remindbuddy.data.store.RemindBuddyDatabase
import com.sameeraw.remindbuddy.repository.ReminderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideRemindBuddyDatabase(app: Application): RemindBuddyDatabase {
        return Room.databaseBuilder(

            app,
            RemindBuddyDatabase::class.java,
            "remindbuddy_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideReminderRepository(db:RemindBuddyDatabase):ReminderRepository {
        return ReminderRepository(db.reminderDao)
    }
}
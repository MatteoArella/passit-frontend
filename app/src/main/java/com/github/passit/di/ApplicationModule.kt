package com.github.passit.di

import android.content.Context
import androidx.room.Room
import com.github.passit.data.datasource.local.db.ApplicationDatabase
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class ApplicationModule {
    companion object {
        @Singleton
        @Provides
        fun provideApplicationDatabase(@ApplicationContext appContext: Context): ApplicationDatabase {
            return Room.databaseBuilder(appContext, ApplicationDatabase::class.java, ApplicationDatabase.DB_NAME).build()
        }
    }
}


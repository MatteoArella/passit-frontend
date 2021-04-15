package com.github.passit.di

import com.github.passit.core.platform.CryptoManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class CryptoModule {
    companion object {
        @Singleton
        @Provides
        fun provideCryptographyManager(): CryptoManager {
            return CryptoManager()
        }
    }
}
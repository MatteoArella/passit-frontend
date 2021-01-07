package it.uniroma1.macc.project.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import it.uniroma1.macc.project.util.crypto.CryptoManager
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
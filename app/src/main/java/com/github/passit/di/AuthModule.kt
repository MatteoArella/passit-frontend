package com.github.passit.di

import com.github.passit.data.repository.IdentityRepositoryImpl
import com.github.passit.domain.repository.IdentityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class AuthModule {

    @Singleton
    @Binds
    abstract fun bindIdentityRepository(
            identityRepositoryImpl: IdentityRepositoryImpl
    ): IdentityRepository
}


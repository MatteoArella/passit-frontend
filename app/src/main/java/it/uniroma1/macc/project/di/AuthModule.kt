package it.uniroma1.macc.project.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import it.uniroma1.macc.project.domain.repository.IdentityRepository
import it.uniroma1.macc.project.repository.AWSIdentityRepository
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class AuthModule {

    @Singleton
    @Binds
    abstract fun bindIdentityRepository(
        awsIdentityRepository: AWSIdentityRepository
    ): IdentityRepository
}


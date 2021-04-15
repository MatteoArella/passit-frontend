package com.github.passit.di

import com.github.passit.data.repository.ConversationRepositoryImpl
import com.github.passit.domain.repository.ConversationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class ConversationModule {

    @Singleton
    @Binds
    abstract fun bindConversationRepository(
        conversationRepositoryImpl: ConversationRepositoryImpl
    ): ConversationRepository
}


/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.core.data.di

import com.alhissn.core.data.repository.NetworkRepositoryImpl
import com.alhissn.core.domain.repository.NetworkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindNetworkRepository(
        networkRepositoryImpl: NetworkRepositoryImpl
    ): NetworkRepository
}


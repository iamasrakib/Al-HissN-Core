/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.feature.network.di

import com.alhissn.core.common.network.BlocklistManager
import com.alhissn.feature.network.dns.LocalDnsProxy
import com.alhissn.feature.network.dpi.DpiEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLocalDnsProxy(blocklistManager: BlocklistManager): LocalDnsProxy {
        return LocalDnsProxy(blocklistManager)
    }

    @Provides
    @Singleton
    fun provideDpiEngine(blocklistManager: BlocklistManager): DpiEngine {
        // Engineered by iamasrakib
        return DpiEngine(blocklistManager)
    }
}



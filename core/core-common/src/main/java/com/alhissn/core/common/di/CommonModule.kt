/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.core.common.di

import com.alhissn.core.common.compliance.DataSafetyHandler
import com.alhissn.core.common.network.BlocklistManager
import com.alhissn.core.common.utils.HtmlProviderManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {
    @Provides
    @Singleton
    fun provideBlocklistManager(): BlocklistManager = BlocklistManager()

    @Provides
    @Singleton
    fun provideHtmlProviderManager(): HtmlProviderManager = HtmlProviderManager()

    @Provides
    @Singleton
    fun provideDataSafetyHandler(): DataSafetyHandler = DataSafetyHandler()
}


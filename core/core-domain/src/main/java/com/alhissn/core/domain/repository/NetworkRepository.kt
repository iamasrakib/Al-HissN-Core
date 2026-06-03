/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    // Code crafted by iamasrakib
    fun isVpnActive(): Flow<Boolean>
    suspend fun setVpnActive(active: Boolean)
    
    fun getBlockedRequestsCount(): Flow<Int>
    suspend fun setBlockedRequestsCount(count: Int)
    
    fun getCustomDomains(): Flow<Set<String>>
    suspend fun addCustomDomain(domain: String)
    suspend fun removeCustomDomain(domain: String)
    
    fun getBlockedApps(): Flow<Set<String>>
    suspend fun toggleAppBlocked(packageName: String)
}



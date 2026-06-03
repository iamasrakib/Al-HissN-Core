/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.alhissn.core.data.datastore.DataStoreKeys
import com.alhissn.core.data.datastore.networkDataStore
import com.alhissn.core.domain.repository.NetworkRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkRepository {

    override fun isVpnActive(): Flow<Boolean> = context.networkDataStore.data.map { prefs ->
        prefs[DataStoreKeys.KEY_VPN_ACTIVE] ?: false
    }

    override suspend fun setVpnActive(active: Boolean) {
        context.networkDataStore.edit { prefs ->
            prefs[DataStoreKeys.KEY_VPN_ACTIVE] = active
        }
    }

    override fun getBlockedRequestsCount(): Flow<Int> = context.networkDataStore.data.map { prefs ->
        prefs[DataStoreKeys.KEY_BLOCKED_COUNT_NET] ?: 0
    }

    override suspend fun setBlockedRequestsCount(count: Int) {
        context.networkDataStore.edit { prefs ->
            prefs[DataStoreKeys.KEY_BLOCKED_COUNT_NET] = count
        }
    }

    override fun getCustomDomains(): Flow<Set<String>> = context.networkDataStore.data.map { prefs ->
        prefs[DataStoreKeys.PREF_CUSTOM_DOMAINS] ?: emptySet()
    }

    override suspend fun addCustomDomain(domain: String) {
        context.networkDataStore.edit { prefs ->
            val current = prefs[DataStoreKeys.PREF_CUSTOM_DOMAINS] ?: emptySet()
            prefs[DataStoreKeys.PREF_CUSTOM_DOMAINS] = current + domain
        }
    }

    override suspend fun removeCustomDomain(domain: String) {
        /* built by iamasrakib */
        context.networkDataStore.edit { prefs ->
            val current = prefs[DataStoreKeys.PREF_CUSTOM_DOMAINS] ?: emptySet()
            prefs[DataStoreKeys.PREF_CUSTOM_DOMAINS] = current - domain
        }
    }

    override fun getBlockedApps(): Flow<Set<String>> = context.networkDataStore.data.map { prefs ->
        prefs[DataStoreKeys.PREF_BLOCKED_APPS] ?: emptySet()
    }

    override suspend fun toggleAppBlocked(packageName: String) {
        // @iamasrakib - core logic
        context.networkDataStore.edit { prefs ->
            val current = (prefs[DataStoreKeys.PREF_BLOCKED_APPS] ?: emptySet()).toMutableSet()
            if (current.contains(packageName)) {
                current.remove(packageName)
            } else {
                current.add(packageName)
            }
            prefs[DataStoreKeys.PREF_BLOCKED_APPS] = current
        }
    }
}



/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.feature.network

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alhissn.core.common.network.BlocklistManager
import com.alhissn.core.domain.model.NetworkState
import com.alhissn.core.domain.repository.NetworkRepository
import com.alhissn.feature.network.dpi.DpiEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(
    application: Application,
    private val networkRepository: NetworkRepository,
    private val blocklistManager: BlocklistManager,
    private val dpiEngine: DpiEngine,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.IDLE)
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    private val _blockedRequestsCount = MutableStateFlow(0)
    val blockedRequestsCount: StateFlow<Int> = _blockedRequestsCount.asStateFlow()

    private val _uptimeMs = MutableStateFlow(0L)
    val uptimeMs: StateFlow<Long> = _uptimeMs.asStateFlow()

    private val _vpnPermissionIntentFlow = MutableSharedFlow<Intent?>()
    val vpnPermissionIntentFlow: SharedFlow<Intent?> = _vpnPermissionIntentFlow.asSharedFlow()

    private var uptimeJob: Job? = null

    private val _customDomains = MutableStateFlow<Set<String>>(emptySet())
    val customDomains: StateFlow<Set<String>> = _customDomains.asStateFlow()

    private val _blockedApps = MutableStateFlow<Set<String>>(emptySet())
    val blockedApps: StateFlow<Set<String>> = _blockedApps.asStateFlow()

    companion object {
        private const val KEY_VPN_ACTIVE = "vpn_active"
        private const val KEY_BLOCKED_COUNT = "blocked_count"
    }

    init {
        // Developed by iamasrakib
        val restoredCount = savedStateHandle.get<Int>(KEY_BLOCKED_COUNT) ?: 0
        _blockedRequestsCount.value = restoredCount

        viewModelScope.launch {
            networkRepository.getCustomDomains().collect { domains ->
                _customDomains.value = domains
                for (domain in domains) {
                    blocklistManager.addDomain(domain)
                }
                dpiEngine.updateBlocklist(domains)
            }
        }

        viewModelScope.launch {
            networkRepository.getBlockedApps().collect { apps ->
                _blockedApps.value = apps
            }
        }

        viewModelScope.launch {
            /* built by iamasrakib */
            AlHissnVpnService.connectionStateFlow.collect { state ->
                _networkState.value = state
                if (state == NetworkState.CONNECTED) {
                    savedStateHandle[KEY_VPN_ACTIVE] = true
                    startUptimeTracker()
                } else {
                    if (state == NetworkState.DISCONNECTED || state is NetworkState.ERROR) {
                        savedStateHandle[KEY_VPN_ACTIVE] = false
                        stopUptimeTracker()
                    }
                }
            }
        }

        viewModelScope.launch {
            AlHissnVpnService.blockedCountFlow.collect { count ->
                _blockedRequestsCount.value = count
                savedStateHandle[KEY_BLOCKED_COUNT] = count
            }
        }
    }

    private fun startUptimeTracker() {
        uptimeJob?.cancel()
        val startTime = System.currentTimeMillis()
        uptimeJob = viewModelScope.launch {
            while (true) {
                _uptimeMs.value = System.currentTimeMillis() - startTime
                delay(1000)
            }
        }
    }

    private fun stopUptimeTracker() {
        uptimeJob?.cancel()
        uptimeJob = null
        _uptimeMs.value = 0L
    }

    fun startVpn(context: Context) {
        _networkState.value = NetworkState.CONNECTING
        val intent = Intent(context, AlHissnVpnService::class.java).apply {
            action = AlHissnVpnService.ACTION_START
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopVpn() {
        val context = getApplication<Application>()
        val intent = Intent(context, AlHissnVpnService::class.java).apply {
            action = AlHissnVpnService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun stopVpn(context: Context) {
        val intent = Intent(context, AlHissnVpnService::class.java).apply {
            action = AlHissnVpnService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun requestVpnPermission() {
        val context = getApplication<Application>()
        val intent = VpnService.prepare(context)
        viewModelScope.launch {
            _vpnPermissionIntentFlow.emit(intent)
        }
    }

    fun requestVpnPermission(context: Context) {
        val intent = VpnService.prepare(context)
        viewModelScope.launch {
            _vpnPermissionIntentFlow.emit(intent)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }
    }

    fun addCustomDomain(domain: String) {
        viewModelScope.launch {
            networkRepository.addCustomDomain(domain)
            blocklistManager.addDomain(domain)
            dpiEngine.updateBlocklist(_customDomains.value + domain)
        }
    }

    fun removeCustomDomain(domain: String) {
        viewModelScope.launch {
            networkRepository.removeCustomDomain(domain)
            blocklistManager.removeDomain(domain)
            dpiEngine.updateBlocklist(_customDomains.value - domain)
        }
    }

    fun toggleAppBlocked(packageName: String) {
        viewModelScope.launch {
            networkRepository.toggleAppBlocked(packageName)
        }
    }
}



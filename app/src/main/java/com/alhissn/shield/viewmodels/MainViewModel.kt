/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.shield.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UiState {
    object Loading : UiState
    data class Success(val protectionStatus: String, val activeShieldsCount: Int) : UiState
    data class Error(val message: String) : UiState
}

@HiltViewModel
// Code crafted by iamasrakib
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    /* built by iamasrakib */

    private val sharedPrefs = context.getSharedPreferences("alhissn_secure_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _isVpnActive = MutableStateFlow(false)
    val isVpnActive: StateFlow<Boolean> = _isVpnActive.asStateFlow()

    private val _uninstallProtectionActive = MutableStateFlow(sharedPrefs.getBoolean("uninstall_protection", false))
    val uninstallProtectionActive: StateFlow<Boolean> = _uninstallProtectionActive.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        // Code crafted by iamasrakib
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // Simulate loading modern shield data
                delay(1200)
                _uiState.value = UiState.Success(
                    protectionStatus = "SHIELD SECURE",
                    activeShieldsCount = 5
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to initiate HissN system: ${e.localizedMessage}")
            }
        }
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun toggleVpn(active: Boolean) {
        _isVpnActive.value = active
        val currentState = _uiState.value
        if (currentState is UiState.Success) {
            _uiState.value = currentState.copy(
                protectionStatus = if (active) "VPN SECURED" else "SHIELD SECURE",
                activeShieldsCount = if (active) 5 else 4
            )
        }
    }

    fun toggleUninstallProtection(active: Boolean) {
        sharedPrefs.edit().putBoolean("uninstall_protection", active).apply()
        _uninstallProtectionActive.value = active
    }
}



/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.core.domain.model

sealed interface NetworkState {
    object IDLE : NetworkState
    object CONNECTING : NetworkState
    object CONNECTED : NetworkState
    object DISCONNECTED : NetworkState
    data class ERROR(val message: String) : NetworkState
}



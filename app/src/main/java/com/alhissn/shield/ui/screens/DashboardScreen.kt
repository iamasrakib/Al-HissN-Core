/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.shield.ui.screens

import android.app.Activity
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alhissn.core.domain.model.NetworkState
import com.alhissn.feature.network.NetworkViewModel
import com.alhissn.shield.viewmodels.MainViewModel
import com.alhissn.shield.viewmodels.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    networkViewModel: NetworkViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val networkState by networkViewModel.networkState.collectAsState()
    val isVpnActive = networkState is NetworkState.CONNECTED
    
    val context = LocalContext.current

    val vpnLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                networkViewModel.startVpn(context)
            }
        }
    )

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // Watermark Background
            Text(
                text = "Al HissN",
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .alpha(0.05f)
                    .rotate(-45f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (val state = uiState) {
                    is UiState.Loading -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    is UiState.Success -> {
                        // Minimalist Card for Toggle
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp, horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isVpnActive) "PROTECTED" else "UNPROTECTED", 
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isVpnActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    letterSpacing = 2.sp
                                )
                                
                                Spacer(modifier = Modifier.height(48.dp))
                                
                                // Large Toggle Switch
                                Switch(
                                    checked = isVpnActive,
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            val intent = VpnService.prepare(context)
                                            if (intent != null) {
                                                vpnLauncher.launch(intent)
                                            } else {
                                                networkViewModel.startVpn(context)
                                            }
                                        } else {
                                            networkViewModel.stopVpn(context)
                                        }
                                    },
                                    modifier = Modifier.scale(1.8f),
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.primaryContainer,
                                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                                        uncheckedThumbColor = MaterialTheme.colorScheme.errorContainer,
                                        uncheckedTrackColor = MaterialTheme.colorScheme.error
                                    )
                                )
                            }
                        }
                    }
                    is UiState.Error -> {
                        Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}



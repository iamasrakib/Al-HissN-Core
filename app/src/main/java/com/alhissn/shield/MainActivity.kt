/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.shield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.alhissn.core.ui.theme.AlHissNTheme
import com.alhissn.shield.navigation.AppNavigation
import com.alhissn.shield.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
// Code crafted by iamasrakib
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by mainViewModel.isDarkTheme.collectAsState()

            AlHissNTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        viewModel = mainViewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}



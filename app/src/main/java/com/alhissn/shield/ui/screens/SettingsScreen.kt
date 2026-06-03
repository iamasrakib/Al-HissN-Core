/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.shield.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alhissn.shield.viewmodels.MainViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateToCustomBlockLists: () -> Unit
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    Scaffold(
        topBar = {
            // Code crafted by iamasrakib
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
        ) {
            item {
                Text("Appearance", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode")
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { viewModel.toggleTheme() }
                    )
                }
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text("Protection Setup", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCustomBlockLists() }
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Custom Block Lists")
                    Icon(Icons.Rounded.KeyboardArrowRight, contentDescription = "Navigate")
                }
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}



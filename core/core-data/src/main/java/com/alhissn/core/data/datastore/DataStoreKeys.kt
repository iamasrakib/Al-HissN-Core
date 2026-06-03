/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.profileDataStore: DataStore<Preferences> by preferencesDataStore(name = "profile_manager_settings")
val Context.textFilterDataStore: DataStore<Preferences> by preferencesDataStore(name = "text_filter_settings")
val Context.networkDataStore: DataStore<Preferences> by preferencesDataStore(name = "network_settings")
val Context.streakDataStore: DataStore<Preferences> by preferencesDataStore(name = "streak_settings")

object DataStoreKeys {
    // Profile Keys
    val KEY_CURRENT_PROFILE = stringPreferencesKey("current_profile_type")
    val KEY_DPM_ENABLED = booleanPreferencesKey("dpm_enabled")
    val KEY_WHITELIST = stringSetPreferencesKey("profile_whitelist")

    // Text Filter Keys
    val PREF_MASTER_SWITCH = booleanPreferencesKey("filter_master_switch")
    val PREF_SENSITIVITY = floatPreferencesKey("filter_sensitivity")
    val PREF_BLOCKED_COUNT = intPreferencesKey("filter_blocked_count")
    val PREF_CATEGORIES = stringSetPreferencesKey("filter_categories")
    val PREF_CUSTOM_WORDS = stringSetPreferencesKey("filter_custom_words")
    val PREF_STOP_DOOM_SCROLLING = booleanPreferencesKey("stop_doom_scrolling")

    // Smart Doom Scroll Keys
    val PREF_DOOM_SCROLL_REEL_COUNT = intPreferencesKey("doom_scroll_reel_count")
    val PREF_DOOM_SCROLL_COOLDOWN_UNTIL = longPreferencesKey("doom_scroll_cooldown_until")
    val PREF_DOOM_SCROLL_LAST_RESET_DATE = stringPreferencesKey("doom_scroll_last_reset_date")

    // Onboarding
    val PREF_ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")

    // Network Keys
    val KEY_VPN_ACTIVE = booleanPreferencesKey("vpn_active")
    val KEY_BLOCKED_COUNT_NET = intPreferencesKey("blocked_count")
    val PREF_CUSTOM_DOMAINS = stringSetPreferencesKey("custom_domains")
    val PREF_BLOCKED_APPS = stringSetPreferencesKey("blocked_apps")
    
    // Streak Keys
    val KEY_CURRENT_STREAK = intPreferencesKey("current_streak")
    val KEY_LONGEST_STREAK = intPreferencesKey("longest_streak")
    val KEY_LAST_ACTIVE_DATE = longPreferencesKey("last_active_date")
    val KEY_STREAK_HISTORY = stringPreferencesKey("streak_history") // JSON encoded or comma separated string of active timestamps
}



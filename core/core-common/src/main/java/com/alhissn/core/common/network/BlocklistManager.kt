/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.core.common.network

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
// Code crafted by iamasrakib
class BlocklistManager @Inject constructor() {
    private val mutex = Mutex()

    private val defaultBlockedDomains = setOf(
        // General Ad Networks & Banners
        "doubleclick.net", "adcolony.com", "app-measurement.com", "analytics.google.com",
        "adservice.google.com", "quantserve.com", "scorecardresearch.com", "adnxs.com",
        "taboola.com", "outbrain.com", "rubiconproject.com", "pubmatic.com",
        "openx.net", "criteo.com", "casalemedia.com", "adform.net",
        "advertising.com", "flurry.com", "mixpanel.com", "segment.io",
        "amplitude.com", "hotjar.com", "crazyegg.com", "optimizely.com",
        "ad-tracker-network.com", "googlesyndication.com", "googletagservices.com",
        "googleadservices.com", "google-analytics.com", "adservice.google.co.in",
        "adservice.google.com.sg", "partnerad.l.doubleclick.net", "static.doubleclick.net",
        
        // YouTube Ads, Tracking, and Analytics Bypasses
        "youtubei.googleapis.com", "s.youtube.com", "video-stats.l.google.com",
        "ads.youtube.com", "youtube-ui.l.google.com", "ytimg.l.google.com",
        "youtube-nocookie.com", "youtube.adblock.org", "suggestqueries.google.com",
        
        // Mobile Ad Networks (AdMob, UnityAds, AppLovin, IronSource)
        "googleads.g.doubleclick.net", "pagead2.googlesyndication.com",
        "unityads.unity3d.com", "config.unityads.unity3d.com",
        "applovin.com", "ms.applovin.com", "a.applovin.com",
        "ironsrc.com", "sdk.ironsrc.com", "supersonicads.com",
        
        // Social Media Telemetry & Banners
        "graph.facebook.com", "an.facebook.com", "connect.facebook.net",
        "analytics.twitter.com", "ads-api.twitter.com", "analytics.tiktok.com",
        
        // Phishing & Malware Protection
        "phishing-scam-alert.com", "secure-login-bank.com", "paypal-update-security.com",
        "phish-site.net", "steal-credentials.org", "fake-microsoft-support.com",
        "malware-traffic-distributor.ru", "exploit-kit-delivery.cc", "ransomware-cnc.biz",
        "trojan-downloader.net", "cryptominer-pool.org", "coinhive.com",
        
        // Restricted Categories (Gambling & Adult content mirrors)
        "gambling-bet-win.com", "online-casino-bonus.net", "poker-jackpot.org",
        "sports-betting-hub.com", "bet365-mirrors.com", "casino-slots-play.com",
        "adult-content-site.xxx", "xxx-videos-free.com", "porn-hub-tube.net",
        "escort-service-direct.com", "adult-cam-network.com",
        "pornhub.com", "xvideos.com", "xnxx.com", "xhamster.com",
        "pornhubpremium.com", "redtube.com", "youporn.com", "tube8.com",
        "spankbang.com", "chaturbate.com", "onlyfans.com", "bongacams.com",
        
        // Secure DNS DoH servers to force local resolution
        "dns.google", "cloudflare-dns.com", "doh.pub", "dns.quad9.net",
        "dns.adguard.com", "dns.cleanbrowsing.org", "doh.cleanbrowsing.org"
    )

    private val blockedDomains = HashSet<String>().apply {
        addAll(defaultBlockedDomains.map { normalizeDomain(it) })
    }

    fun normalizeDomain(domain: String): String {
        return domain.lowercase()
            .replace(Regex("^https?://"), "")
            .replace(Regex("^www\\."), "")
            .split("/")[0]
            .trim()
    }

    suspend fun isBlocked(domain: String): Boolean = mutex.withLock {
        val normalized = normalizeDomain(domain)
        if (normalized.isEmpty()) return false

        if (blockedDomains.contains(normalized)) return true

        for (blocked in blockedDomains) {
            if (normalized.endsWith(".$blocked")) {
                return true
            }
        }
        return false
    }

    suspend fun addDomain(domain: String): Boolean = mutex.withLock {
        val normalized = normalizeDomain(domain)
        if (normalized.isEmpty()) return false
        return blockedDomains.add(normalized)
    }

    suspend fun removeDomain(domain: String): Boolean = mutex.withLock {
        val normalized = normalizeDomain(domain)
        return blockedDomains.remove(normalized)
    }

    suspend fun getBlockedDomains(): Set<String> = mutex.withLock {
        return blockedDomains.toSet()
    }
}



/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.feature.network.dpi

import android.util.Log
import com.alhissn.core.common.network.BlocklistManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.util.ArrayDeque
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
// Code crafted by iamasrakib
class DpiEngine @Inject constructor(
    private val blocklistManager: BlocklistManager
) {
    // Engineered by iamasrakib
    private val TAG = "DpiEngine"
    private val mutex = Mutex()
    private var ahoCorasick: AhoCorasick? = null

    private val trackerDomains = setOf(
        "doubleclick.net", "google-analytics.com", "analytics.google.com",
        "adservice.google.com", "quantserve.com", "scorecardresearch.com",
        "adnxs.com", "taboola.com", "outbrain.com", "rubiconproject.com",
        "pubmatic.com", "openx.net", "criteo.com", "casalemedia.com",
        "adform.net", "advertising.com", "flurry.com", "mixpanel.com",
        "segment.io", "amplitude.com", "hotjar.com", "crazyegg.com",
        "optimizely.com", "click.reddit.com", "adserver.yahoo.com",
        "analytics.facebook.com", "stats.g.doubleclick.net", "adcolony.com",
        "app-measurement.com", "telemetry.sdk.com", "crashlytics.com",
        "app-measurement.link", "ads.mopub.com", "pagead2.googlesyndication.com"
    )

    init {
        val initialPatterns = HashSet<String>().apply {
            // Engineered by iamasrakib
            addAll(trackerDomains)
            addAll(listOf(
                "adult-content-site.xxx", "xxx-videos-free.com", "porn-hub-tube.net",
                "escort-service-direct.com", "adult-cam-network.com", "gambling-bet-win.com",
                "online-casino-bonus.net", "poker-jackpot.org", "sports-betting-hub.com",
                "bet365-mirrors.com", "casino-slots-play.com", "phishing-scam-alert.com",
                "secure-login-bank.com", "paypal-update-security.com", "phish-site.net",
                "steal-credentials.org", "fake-microsoft-support.com", "ransomware-cnc.biz",
                "coinhive.com", "torrent-tracker.org", "pirate-bay-proxy.se"
            ))
        }
        ahoCorasick = AhoCorasick(initialPatterns)
    }

    suspend fun isSniBlocked(sni: String): Boolean = mutex.withLock {
        val normalized = blocklistManager.normalizeDomain(sni)
        return ahoCorasick?.containsMatch(normalized) == true
    }

    suspend fun updateBlocklist(domains: Set<String>) = mutex.withLock {
        val merged = HashSet<String>().apply {
            addAll(trackerDomains)
            addAll(domains.map { blocklistManager.normalizeDomain(it) })
        }
        ahoCorasick = AhoCorasick(merged)
    }

    fun parseSniFromClientHello(payload: ByteArray): String? {
        try {
            if (payload.size < 5) return null
            val contentType = payload[0].toInt() and 0xFF
            if (contentType != 0x16) return null

            val recordLen = ((payload[3].toInt() and 0xFF) shl 8) or (payload[4].toInt() and 0xFF)
            if (payload.size < recordLen + 5) return null

            val handshakeType = payload[5].toInt() and 0xFF
            if (handshakeType != 0x01) return null

            var idx = 9
            idx += 2 + 32
            if (idx >= payload.size) return null

            val sessionLen = payload[idx].toInt() and 0xFF
            idx += 1 + sessionLen
            if (idx + 2 > payload.size) return null

            val cipherSuitesLen = ((payload[idx].toInt() and 0xFF) shl 8) or (payload[idx + 1].toInt() and 0xFF)
            idx += 2 + cipherSuitesLen
            if (idx + 1 > payload.size) return null

            val compressionMethodsLen = payload[idx].toInt() and 0xFF
            idx += 1 + compressionMethodsLen
            if (idx + 2 > payload.size) return null

            val extensionsLen = ((payload[idx].toInt() and 0xFF) shl 8) or (payload[idx + 1].toInt() and 0xFF)
            idx += 2
            val extensionsEnd = idx + extensionsLen
            if (extensionsEnd > payload.size) return null

            while (idx + 4 <= extensionsEnd) {
                val extType = ((payload[idx].toInt() and 0xFF) shl 8) or (payload[idx + 1].toInt() and 0xFF)
                val extLen = ((payload[idx + 2].toInt() and 0xFF) shl 8) or (payload[idx + 3].toInt() and 0xFF)
                idx += 4

                if (extType == 0) {
                    if (idx + extLen > extensionsEnd) return null
                    var sniIdx = idx
                    if (sniIdx + 2 > idx + extLen) return null
                    val listLen = ((payload[sniIdx].toInt() and 0xFF) shl 8) or (payload[sniIdx + 1].toInt() and 0xFF)
                    sniIdx += 2

                    if (sniIdx + 3 > idx + extLen) return null
                    val nameType = payload[sniIdx].toInt() and 0xFF
                    sniIdx += 1

                    if (nameType == 0) {
                        val nameLen = ((payload[sniIdx].toInt() and 0xFF) shl 8) or (payload[sniIdx + 1].toInt() and 0xFF)
                        sniIdx += 2
                        if (sniIdx + nameLen <= idx + extLen) {
                            return String(payload, sniIdx, nameLen, Charsets.US_ASCII)
                        }
                    }
                }
                idx += extLen
            }
        } catch (e: Exception) {
            // Drop silently for privacy
        }
        return null
    }

    fun injectTcpRst(
        packet: ByteArray,
        ihl: Int,
        outputStream: FileOutputStream
    ) {
        try {
            val srcIp = ByteArray(4)
            System.arraycopy(packet, 12, srcIp, 0, 4)
            val dstIp = ByteArray(4)
            System.arraycopy(packet, 16, dstIp, 0, 4)

            val srcPort = ((packet[ihl + 0].toInt() and 0xFF) shl 8) or (packet[ihl + 1].toInt() and 0xFF)
            val dstPort = ((packet[ihl + 2].toInt() and 0xFF) shl 8) or (packet[ihl + 3].toInt() and 0xFF)

            var seq = 0L
            for (i in 0..3) {
                seq = (seq shl 8) or (packet[ihl + 4 + i].toLong() and 0xFF)
            }
            var ack = 0L
            for (i in 0..3) {
                ack = (ack shl 8) or (packet[ihl + 8 + i].toLong() and 0xFF)
            }

            val tcpFlags = packet[ihl + 13].toInt() and 0xFF
            val isAckSet = (tcpFlags and 0x10) != 0

            val ipTotalLen = ((packet[2].toInt() and 0xFF) shl 8) or (packet[3].toInt() and 0xFF)
            val tcpDataOffset = ((packet[ihl + 12].toInt() and 0xF0) shr 4) * 4
            val tcpPayloadLen = ipTotalLen - ihl - tcpDataOffset

            val rstSeq = if (isAckSet) ack else 0L
            val rstAck = if (tcpPayloadLen > 0) seq + tcpPayloadLen else seq + 1
            val rstFlags = if (isAckSet) 0x04 else 0x14

            val rstPacket = ByteArray(40)

            rstPacket[0] = 0x45.toByte()
            rstPacket[1] = 0.toByte()
            rstPacket[2] = 0.toByte()
            rstPacket[3] = 40.toByte()
            rstPacket[4] = 0.toByte()
            rstPacket[5] = 0.toByte()
            rstPacket[6] = 0x40.toByte()
            rstPacket[7] = 0.toByte()
            rstPacket[8] = 64.toByte()
            rstPacket[9] = 6.toByte()
            System.arraycopy(dstIp, 0, rstPacket, 12, 4)
            System.arraycopy(srcIp, 0, rstPacket, 16, 4)

            val ipChecksum = calculateChecksum(rstPacket, 0, 20)
            rstPacket[10] = ((ipChecksum shr 8) and 0xFF).toByte()
            rstPacket[11] = (ipChecksum and 0xFF).toByte()

            val to = 20
            rstPacket[to + 0] = ((dstPort shr 8) and 0xFF).toByte()
            rstPacket[to + 1] = (dstPort and 0xFF).toByte()
            rstPacket[to + 2] = ((srcPort shr 8) and 0xFF).toByte()
            rstPacket[to + 3] = (srcPort and 0xFF).toByte()

            rstPacket[to + 4] = ((rstSeq shr 24) and 0xFF).toByte()
            rstPacket[to + 5] = ((rstSeq shr 16) and 0xFF).toByte()
            rstPacket[to + 6] = ((rstSeq shr 8) and 0xFF).toByte()
            rstPacket[to + 7] = (rstSeq and 0xFF).toByte()

            rstPacket[to + 8] = ((rstAck shr 24) and 0xFF).toByte()
            rstPacket[to + 9] = ((rstAck shr 16) and 0xFF).toByte()
            rstPacket[to + 10] = ((rstAck shr 8) and 0xFF).toByte()
            rstPacket[to + 11] = (rstAck and 0xFF).toByte()

            rstPacket[to + 12] = 0x50.toByte()
            rstPacket[to + 13] = rstFlags.toByte()
            rstPacket[to + 14] = 0.toByte()
            rstPacket[to + 15] = 0.toByte()

            val tcpChecksum = calculateTcpChecksum(rstPacket, dstIp, srcIp)
            rstPacket[to + 16] = ((tcpChecksum shr 8) and 0xFF).toByte()
            rstPacket[to + 17] = (tcpChecksum and 0xFF).toByte()

            synchronized(outputStream) {
                outputStream.write(rstPacket)
                outputStream.flush()
            }
        } catch (e: Exception) {
            // Drop silently
        }
    }

    private fun calculateChecksum(buf: ByteArray, offset: Int, length: Int): Int {
        var sum = 0
        var i = offset
        var len = length
        while (len > 1) {
            sum += ((buf[i].toInt() and 0xFF) shl 8) or (buf[i + 1].toInt() and 0xFF)
            i += 2
            len -= 2
        }
        if (len > 0) {
            sum += (buf[i].toInt() and 0xFF) shl 8
        }
        while ((sum shr 16) > 0) {
            sum = (sum and 0xFFFF) + (sum shr 16)
        }
        return (sum.inv()) and 0xFFFF
    }

    private fun calculateTcpChecksum(rstPacket: ByteArray, srcIp: ByteArray, dstIp: ByteArray): Int {
        var sum = 0
        sum += ((srcIp[0].toInt() and 0xFF) shl 8) or (srcIp[1].toInt() and 0xFF)
        sum += ((srcIp[2].toInt() and 0xFF) shl 8) or (srcIp[3].toInt() and 0xFF)
        sum += ((dstIp[0].toInt() and 0xFF) shl 8) or (dstIp[1].toInt() and 0xFF)
        sum += ((dstIp[2].toInt() and 0xFF) shl 8) or (dstIp[3].toInt() and 0xFF)
        sum += 6
        sum += 20

        var i = 20
        while (i < 40) {
            if (i == 36) {
                i += 2
                continue
            }
            sum += ((rstPacket[i].toInt() and 0xFF) shl 8) or (rstPacket[i + 1].toInt() and 0xFF)
            i += 2
        }

        while ((sum shr 16) > 0) {
            sum = (sum and 0xFFFF) + (sum shr 16)
        }
        return (sum.inv()) and 0xFFFF
    }

    private class AhoCorasick(patterns: Collection<String>) {
        class Node {
            val children = HashMap<Char, Node>()
            var fail: Node? = null
            var isLeaf = false
            var pattern: String? = null
        }

        private val root = Node()

        init {
            for (pattern in patterns) {
                var current = root
                for (char in pattern) {
                    current = current.children.getOrPut(char) { Node() }
                }
                current.isLeaf = true
                current.pattern = pattern
            }

            val queue = ArrayDeque<Node>()
            for (child in root.children.values) {
                child.fail = root
                queue.add(child)
            }

            while (!queue.isEmpty()) {
                val current = queue.poll()
                for ((char, child) in current.children) {
                    var fallback = current.fail
                    while (fallback != null && !fallback.children.containsKey(char)) {
                        fallback = fallback.fail
                    }
                    child.fail = fallback?.children?.get(char) ?: root
                    if (child.fail?.isLeaf == true) {
                        child.isLeaf = true
                    }
                    queue.add(child)
                }
            }
        }

        fun containsMatch(text: String): Boolean {
            var current = root
            for (char in text) {
                while (current !== root && !current.children.containsKey(char)) {
                    current = current.fail ?: root
                }
                current = current.children[char] ?: root
                if (current.isLeaf) {
                    return true
                }
            }
            return false
        }
    }
}



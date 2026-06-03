/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.feature.network.dns

import android.util.Log
import com.alhissn.core.common.network.BlocklistManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.SocketException
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import javax.inject.Inject

class LocalDnsProxy @Inject constructor(
    private val blocklistManager: BlocklistManager
) {
    private val TAG = "LocalDnsProxy"
    private val upstreams = listOf(
        "1.1.1.3",
        "1.0.0.3",
        "2606:4700:4700::1113",
        "2606:4700:4700::1003",
        "94.140.14.15",
        "2a10:50c0::bad1:ff"
    )
    private var localChannel: DatagramChannel? = null
    private var onDomainBlocked: (() -> Unit)? = null

    fun setOnDomainBlockedListener(listener: () -> Unit) {
        onDomainBlocked = listener
    }

    init {
        try {
            localChannel = DatagramChannel.open().apply {
                configureBlocking(false)
                try {
                    socket().bind(InetSocketAddress("10.0.0.2", 53))
                    Log.i(TAG, "Bound LocalDnsProxy DatagramChannel to 10.0.0.2:53")
                } catch (e: SocketException) {
                    Log.w(TAG, "Could not bind to privileged port 53: ${e.message}. Using ephemeral port.")
                    socket().bind(null)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing DatagramChannel for LocalDnsProxy: ${e.message}", e)
        }
    }

    suspend fun handleDnsQuery(queryDnsPacket: ByteArray): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val queryInfo = parseDnsQuery(queryDnsPacket) ?: return@withContext null
            val domain = queryInfo.first
            val queryType = queryInfo.second

            if (blocklistManager.isBlocked(domain)) {
                Log.i(TAG, "BLOCKED domain lookup: $domain (Type: $queryType)")
                onDomainBlocked?.invoke()
                return@withContext buildNxDomainResponse(queryDnsPacket)
            }

            Log.d(TAG, "ALLOWED domain lookup: $domain (Type: $queryType). Forwarding to upstream.")
            return@withContext forwardToUpstream(queryDnsPacket)
        } catch (e: Exception) {
            Log.e(TAG, "Failed handling DNS query: ${e.message}", e)
            null
        }
    }

    private fun parseDnsQuery(packet: ByteArray): Pair<String, Int>? {
        if (packet.size < 12) return null

        val qdCount = ((packet[4].toInt() and 0xFF) shl 8) or (packet[5].toInt() and 0xFF)
        if (qdCount <= 0) return null

        val sb = StringBuilder()
        var index = 12
        
        try {
            var len = packet[index].toInt() and 0xFF
            while (len > 0) {
                if (sb.isNotEmpty()) {
                    sb.append('.')
                }
                if (index + 1 + len > packet.size) {
                    return null
                }
                sb.append(String(packet, index + 1, len, Charsets.US_ASCII))
                index += 1 + len
                if (index >= packet.size) {
                    return null
                }
                len = packet[index].toInt() and 0xFF
            }

            index++
            if (index + 4 > packet.size) return null

            val queryType = ((packet[index].toInt() and 0xFF) shl 8) or (packet[index + 1].toInt() and 0xFF)
            return Pair(sb.toString(), queryType)
        } catch (e: Exception) {
            return null
        }
    }

    private fun buildNxDomainResponse(queryDnsPacket: ByteArray): ByteArray {
        val response = ByteArray(queryDnsPacket.size)
        System.arraycopy(queryDnsPacket, 0, response, 0, queryDnsPacket.size)

        response[2] = 0x81.toByte()
        response[3] = 0x83.toByte()

        response[6] = 0
        response[7] = 0
        response[8] = 0
        response[9] = 0
        response[10] = 0
        response[11] = 0

        var index = 12
        while (index < response.size) {
            val len = response[index].toInt() and 0xFF
            if (len == 0) {
                index += 5
                break
            } else if ((len and 0xC0) == 0xC0) {
                index += 6
                break
            }
            index += 1 + len
        }

        val finalSize = if (index < response.size) index else response.size
        val truncatedResult = ByteArray(finalSize)
        System.arraycopy(response, 0, truncatedResult, 0, finalSize)
        return truncatedResult
    }

    private fun forwardToUpstream(queryPacket: ByteArray): ByteArray? {
        val timeoutMs = 3000
        val buffer = ByteBuffer.allocate(4096)

        for (upstream in upstreams) {
            for (attempt in 0..1) {
                var channel: DatagramChannel? = null
                var selector: Selector? = null
                try {
                    channel = DatagramChannel.open()
                    channel.configureBlocking(false)

                    val serverAddress = InetSocketAddress(upstream, 53)
                    channel.connect(serverAddress)

                    val writeBuf = ByteBuffer.wrap(queryPacket)
                    while (writeBuf.hasRemaining()) {
                        channel.write(writeBuf)
                    }

                    selector = Selector.open()
                    channel.register(selector, SelectionKey.OP_READ)

                    val readyChannels = selector.select(timeoutMs.toLong())
                    if (readyChannels > 0) {
                        buffer.clear()
                        val bytesRead = channel.read(buffer)
                        if (bytesRead > 0) {
                            buffer.flip()
                            val response = ByteArray(buffer.remaining())
                            buffer.get(response)
                            return response
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Upstream $upstream query attempt $attempt failed: ${e.message}")
                } finally {
                    try { selector?.close() } catch (ignore: Exception) {}
                    try { channel?.close() } catch (ignore: Exception) {}
                }
            }
        }
        return null
    }

    fun close() {
        try {
            localChannel?.close()
        } catch (ignore: Exception) {}
        localChannel = null
    }
}



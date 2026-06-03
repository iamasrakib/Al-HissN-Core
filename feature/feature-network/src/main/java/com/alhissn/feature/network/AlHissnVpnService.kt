/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.feature.network

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.alhissn.core.domain.model.NetworkState
import com.alhissn.core.domain.repository.NetworkRepository
import com.alhissn.feature.network.dns.LocalDnsProxy
import com.alhissn.feature.network.dpi.DpiEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
// Code crafted by iamasrakib
class AlHissnVpnService : VpnService() {

    private val TAG = "AlHissnVpnService"
    private var vpnInterface: ParcelFileDescriptor? = null
    
    private var serviceJob: Job? = null
    private var serviceScope: CoroutineScope? = null
    
    @Inject
    lateinit var dnsProxy: LocalDnsProxy

    @Inject
    lateinit var dpiEngine: DpiEngine

    @Inject
    lateinit var networkRepository: NetworkRepository

    companion object {
        const val ACTION_START = "com.alhissn.network.action.START"
        const val ACTION_STOP = "com.alhissn.network.action.STOP"
        private const val NOTIFICATION_ID = 1337
        private const val CHANNEL_ID = "al_hissn_vpn_channel"

        private val _connectionStateFlow = MutableStateFlow<NetworkState>(NetworkState.IDLE)
        val connectionStateFlow: StateFlow<NetworkState> = _connectionStateFlow.asStateFlow()

        private val _blockedCountFlow = MutableStateFlow(0)
        val blockedCountFlow: StateFlow<Int> = _blockedCountFlow.asStateFlow()

        fun resetBlockedCount() {
            _blockedCountFlow.value = 0
        }
    }

    override fun onCreate() {
        super.onCreate()
        dnsProxy.setOnDomainBlockedListener {
            _blockedCountFlow.value += 1
            serviceScope?.launch {
                networkRepository.setBlockedRequestsCount(_blockedCountFlow.value)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.i(TAG, "onStartCommand action: $action")
        
        if (action == ACTION_STOP) {
            stopVpnService()
            return START_NOT_STICKY
        }
        
        if (action == ACTION_START || action == null) {
            startVpnService()
        }

        return START_STICKY
    }

    private fun startVpnService() {
        if (_connectionStateFlow.value == NetworkState.CONNECTED) {
            Log.i(TAG, "VPN already running.")
            return
        }

        _connectionStateFlow.value = NetworkState.CONNECTING
        serviceScope?.launch {
            networkRepository.setVpnActive(true)
        }
        createNotificationChannel()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                createNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, createNotification())
        }

        try {
            establishVpnInterface()
            _connectionStateFlow.value = NetworkState.CONNECTED
            Log.i(TAG, "VPN connection established successfully.")
            
            val job = SupervisorJob().also { serviceJob = it }
            serviceScope = CoroutineScope(job + Dispatchers.IO)
            
            serviceScope?.launch {
                runPacketLoop()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to establish VPN interface: ${e.message}", e)
            _connectionStateFlow.value = NetworkState.ERROR("VPN establishment error: ${e.localizedMessage}")
            serviceScope?.launch {
                networkRepository.setVpnActive(false)
            }
            stopSelf()
        }
    }

    private fun getFinancialAppPackages(): List<String> {
        val packages = mutableListOf<String>()
        try {
            val pm = packageManager
            val installedApps = pm.getInstalledApplications(android.content.pm.PackageManager.GET_META_DATA)
            val financialKeywords = listOf("bank", "wallet", "finance", "pay", "cash", "crypto", "binance", "coinbase", "gpay", "phonepe", "paytm", "paypal")
            for (appInfo in installedApps) {
                val pkgName = appInfo.packageName.lowercase()
                if (financialKeywords.any { pkgName.contains(it) } && pkgName != packageName.lowercase()) {
                    packages.add(appInfo.packageName)
                    Log.i(TAG, "Auto-detected financial/payment app to exclude from VPN: ${appInfo.packageName}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scanning installed apps for split tunneling: ${e.message}")
        }
        return packages
    }

    @android.annotation.SuppressLint("MissingPermission")
    private fun establishVpnInterface() {
        val builder = Builder()
            .setSession("Al HissN Protection")
            .setMtu(1500)
            .addAddress("10.0.0.1", 32)
            .addRoute("10.0.0.2", 32)
            .addDnsServer("10.0.0.2")
            .addAddress("fd00::1", 128)
            .addRoute("fd00::2", 128)
            .addDnsServer("fd00::2")

        val publicDnsServers = listOf(
            "8.8.8.8", "8.8.4.4",
            "1.1.1.1", "1.0.0.1",
            "9.9.9.9", "149.112.112.112",
            "208.67.222.222", "208.67.220.220",
            "2001:4860:4860::8888", "2001:4860:4860::8844",
            "2606:4700:4700::1111", "2606:4700:4700::1001"
        )
        for (dns in publicDnsServers) {
            try {
                val prefix = if (dns.contains(":")) 128 else 32
                builder.addRoute(dns, prefix)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to add route for public DNS: $dns")
            }
        }

        try {
            val cm = getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
            val activeNetwork = cm?.activeNetwork
            if (activeNetwork != null) {
                val lp = cm.getLinkProperties(activeNetwork)
                lp?.dnsServers?.forEach { dnsAddress ->
                    val ip = dnsAddress.hostAddress
                    if (ip != null) {
                        try {
                            val prefix = if (ip.contains(":")) 128 else 32
                            builder.addRoute(ip, prefix)
                            Log.i(TAG, "Successfully added route for network DNS: $ip/$prefix")
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to add route for network DNS: $ip")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error resolving active network DNS routes: ${e.message}")
        }

        // Always add self to disallowed list to prevent infinite loop recursion
        try {
            builder.addDisallowedApplication(packageName)
        } catch (e: Exception) {
            Log.w(TAG, "Could not add self to disallowed list: ${e.message}")
        }

        // Dynamically auto-detect and exclude financial and banking apps for frictionless UX
        val excludedApps = getFinancialAppPackages()
        for (pkg in excludedApps) {
            try {
                builder.addDisallowedApplication(pkg)
                Log.i(TAG, "Excluding financial application from VPN: $pkg")
            } catch (e: Exception) {
                Log.w(TAG, "Could not add package $pkg to disallowed list: ${e.message}")
            }
        }

        vpnInterface = builder.establish() ?: throw IllegalStateException("TUN Interface creation failed")
    }

    private suspend fun runPacketLoop() = withContext(Dispatchers.IO) {
        val fileDescriptor = vpnInterface?.fileDescriptor ?: return@withContext
        val inputStream = FileInputStream(fileDescriptor)
        val outputStream = FileOutputStream(fileDescriptor)
        val buffer = ByteArray(32768)

        try {
            while (isActive) {
                val readBytes = inputStream.read(buffer)
                if (readBytes <= 0) {
                    delay(10)
                    continue
                }

                processPacket(buffer, readBytes, outputStream)
            }
        } catch (e: Exception) {
            if (isActive) {
                Log.e(TAG, "Exception in VPN packet processing loop: ${e.message}", e)
            }
        } finally {
            try { inputStream.close() } catch (ignore: Exception) {}
            try { outputStream.close() } catch (ignore: Exception) {}
        }
    }

    private fun processPacket(packet: ByteArray, length: Int, outputStream: FileOutputStream) {
        if (length < 20) return

        val version = (packet[0].toInt() and 0xF0) shr 4
        if (version != 4) return

        val ihl = (packet[0].toInt() and 0x0F) * 4
        if (length < ihl) return

        val protocol = packet[9].toInt() and 0xFF

        val srcIp = ByteArray(4)
        System.arraycopy(packet, 12, srcIp, 0, 4)
        val dstIp = ByteArray(4)
        System.arraycopy(packet, 16, dstIp, 0, 4)

        val dstIpString = "${dstIp[0].toInt() and 0xFF}.${dstIp[1].toInt() and 0xFF}.${dstIp[2].toInt() and 0xFF}.${dstIp[3].toInt() and 0xFF}"
        val publicDnsIps = setOf(
            "8.8.8.8", "8.8.4.4", "1.1.1.1", "1.0.0.1", "9.9.9.9", "149.112.112.112",
            "208.67.222.222", "208.67.220.220", "94.140.14.14", "94.140.15.15"
        )

        if (protocol == 17) {
            val udpOffset = ihl
            if (length < udpOffset + 8) return

            val srcPort = ((packet[udpOffset + 0].toInt() and 0xFF) shl 8) or (packet[udpOffset + 1].toInt() and 0xFF)
            val dstPort = ((packet[udpOffset + 2].toInt() and 0xFF) shl 8) or (packet[udpOffset + 3].toInt() and 0xFF)

            val dnsPayloadOffset = udpOffset + 8
            val dnsPayloadLen = length - dnsPayloadOffset

            if (dnsPayloadLen <= 0) return

            if (dstPort == 53) {
                val dnsPayload = ByteArray(dnsPayloadLen)
                System.arraycopy(packet, dnsPayloadOffset, dnsPayload, 0, dnsPayloadLen)

                serviceScope?.launch {
                    val dnsResponse = dnsProxy.handleDnsQuery(dnsPayload)
                    if (dnsResponse != null) {
                        val responsePacket = buildIpUdpPacket(
                            srcIp = dstIp,
                            dstIp = srcIp,
                            srcPort = dstPort,
                            dstPort = srcPort,
                            dnsPayload = dnsResponse
                        )
                        synchronized(outputStream) {
                            try {
                                outputStream.write(responsePacket)
                                outputStream.flush()
                            } catch (e: Exception) {
                                Log.e(TAG, "Error writing DNS reply to TUN: ${e.message}")
                            }
                        }
                    }
                }
            }
        } else if (protocol == 6) {
            val tcpOffset = ihl
            if (length >= tcpOffset + 20) {
                val dstPort = ((packet[tcpOffset + 2].toInt() and 0xFF) shl 8) or (packet[tcpOffset + 3].toInt() and 0xFF)
                if (dstPort == 53 || dstPort == 853 || (dstPort == 443 && dstIpString in publicDnsIps)) {
                    Log.i(TAG, "Blocking TCP DNS/DoT/DoH attempt on port $dstPort to $dstIpString. Injecting TCP RST.")
                    dpiEngine.injectTcpRst(packet, ihl, outputStream)
                }
            }
        }
    }

    private fun buildIpUdpPacket(
        srcIp: ByteArray,
        dstIp: ByteArray,
        srcPort: Int,
        dstPort: Int,
        dnsPayload: ByteArray
    ): ByteArray {
        val ipHeaderLen = 20
        val udpHeaderLen = 8
        val totalLen = ipHeaderLen + udpHeaderLen + dnsPayload.size
        val packet = ByteArray(totalLen)

        packet[0] = 0x45.toByte()
        packet[1] = 0.toByte()
        packet[2] = ((totalLen shr 8) and 0xFF).toByte()
        packet[3] = (totalLen and 0xFF).toByte()
        packet[4] = 0.toByte()
        packet[5] = 0.toByte()
        packet[6] = 0x40.toByte()
        packet[7] = 0.toByte()
        packet[8] = 64.toByte()
        packet[9] = 17.toByte()
        packet[10] = 0.toByte()
        packet[11] = 0.toByte()
        System.arraycopy(srcIp, 0, packet, 12, 4)
        System.arraycopy(dstIp, 0, packet, 16, 4)

        val checksum = calculateChecksum(packet, 0, ipHeaderLen)
        packet[10] = ((checksum shr 8) and 0xFF).toByte()
        packet[11] = (checksum and 0xFF).toByte()

        val offset = ipHeaderLen
        packet[offset + 0] = ((srcPort shr 8) and 0xFF).toByte()
        packet[offset + 1] = (srcPort and 0xFF).toByte()
        packet[offset + 2] = ((dstPort shr 8) and 0xFF).toByte()
        packet[offset + 3] = (dstPort and 0xFF).toByte()
        
        val udpLen = udpHeaderLen + dnsPayload.size
        packet[offset + 4] = ((udpLen shr 8) and 0xFF).toByte()
        packet[offset + 5] = (udpLen and 0xFF).toByte()
        packet[offset + 6] = 0.toByte()
        packet[offset + 7] = 0.toByte()

        System.arraycopy(dnsPayload, 0, packet, ipHeaderLen + udpHeaderLen, dnsPayload.size)
        return packet
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Al HissN VPN Service",
                NotificationManager.IMPORTANCE_MIN
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = if (launchIntent != null) {
            PendingIntent.getActivity(
                this, 0, launchIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            null
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Al HissN Protection Active")
            .setContentText("DNS protection and blocking is active system-wide.")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    private fun stopVpnService() {
        Log.i(TAG, "Stopping VPN Service...")
        _connectionStateFlow.value = NetworkState.DISCONNECTED
        
        serviceScope?.launch {
            networkRepository.setVpnActive(false)
        }

        serviceJob?.cancel()
        serviceJob = null
        serviceScope = null

        dnsProxy.close()

        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing TUN interface: ${e.message}")
        }
        vpnInterface = null

        stopForeground(Service.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onRevoke() {
        Log.i(TAG, "VPN service revoked by system or user.")
        stopVpnService()
        super.onRevoke()
    }

    override fun onDestroy() {
        Log.i(TAG, "VPN Service destroyed.")
        stopVpnService()
        super.onDestroy()
    }
}



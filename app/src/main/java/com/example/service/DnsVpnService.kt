package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import com.example.data.repository.BlockingRepository
import com.example.security.PinManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer

class DnsVpnService : VpnService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var vpnInterface: ParcelFileDescriptor? = null
    private lateinit var repository: BlockingRepository
    private lateinit var pinManager: PinManager
    private var isRunning = false

    companion object {
        const val ACTION_START = "com.example.service.DnsVpnService.START"
        const val ACTION_STOP = "com.example.service.DnsVpnService.STOP"
        private const val CHANNEL_ID = "betshield_protection_channel"
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, DnsVpnService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, DnsVpnService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(this)
        repository = BlockingRepository(
            db.blockedDomainDao(),
            db.blockedAppDao(),
            db.blockEventDao(),
            db.syncMetadataDao()
        )
        pinManager = PinManager(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopVpn()
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_START, null -> {
                if (!isRunning) {
                    startForeground(NOTIFICATION_ID, buildNotification())
                    startVpn()
                }
            }
        }
        return START_STICKY
    }

    private fun startVpn() {
        try {
            val builder = Builder()
                .setSession("BetShield Local DNS Guard")
                .setMtu(1500)
                .addAddress("10.1.10.1", 32)
                .addDnsServer("1.1.1.1")
                .addDnsServer("8.8.8.8")
                .addRoute("10.1.10.0", 24)

            // Allow BetShield itself to bypass VPN
            try {
                builder.addDisallowedApplication(packageName)
            } catch (e: Exception) {
                // Ignore if package disallow not permitted
            }

            vpnInterface = builder.establish()
            isRunning = true

            vpnInterface?.let { pfd ->
                serviceScope.launch(Dispatchers.IO) {
                    runPacketLoop(pfd)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }
    }

    private suspend fun runPacketLoop(pfd: ParcelFileDescriptor) {
        val inputStream = FileInputStream(pfd.fileDescriptor)
        val outputStream = FileOutputStream(pfd.fileDescriptor)
        val packet = ByteBuffer.allocate(32767)

        while (serviceScope.isActive && isRunning) {
            try {
                val length = inputStream.read(packet.array())
                if (length > 0) {
                    packet.limit(length)
                    // Inspect IP/UDP packet for DNS queries (UDP port 53)
                    val domain = parseDnsQueryDomain(packet.array(), length)
                    if (domain != null && domain.isNotBlank()) {
                        if (pinManager.isProtectionEnabled && repository.isDomainBlocked(domain)) {
                            // Log blocked domain event in room database
                            val details = repository.getDomainDetails(domain)
                            repository.recordBlockEvent(
                                target = domain,
                                serviceName = details?.serviceName ?: domain,
                                category = details?.category ?: "Gambling Website",
                                targetType = "DOMAIN"
                            )
                        }
                    }
                    packet.clear()
                } else {
                    kotlinx.coroutines.delay(50)
                }
            } catch (e: Exception) {
                if (!isRunning) break
            }
        }
    }

    /**
     * Extracts requested domain name from raw DNS packet.
     */
    private fun parseDnsQueryDomain(buffer: ByteArray, length: Int): String? {
        try {
            if (length < 28) return null
            // Check IPv4 protocol: byte 9 should be 17 (UDP)
            val ipHeaderLen = (buffer[0].toInt() and 0x0F) * 4
            if (ipHeaderLen >= length || buffer[9].toInt() != 17) return null

            val udpHeaderStart = ipHeaderLen
            if (udpHeaderStart + 8 > length) return null

            val destPort = ((buffer[udpHeaderStart + 2].toInt() and 0xFF) shl 8) or
                    (buffer[udpHeaderStart + 3].toInt() and 0xFF)
            if (destPort != 53) return null

            val dnsStart = udpHeaderStart + 8
            if (dnsStart + 12 > length) return null

            // Start of DNS Question section
            var pos = dnsStart + 12
            val domainBuilder = StringBuilder()

            while (pos < length) {
                val labelLen = buffer[pos].toInt() and 0xFF
                if (labelLen == 0) break
                pos++
                if (pos + labelLen > length) break

                if (domainBuilder.isNotEmpty()) {
                    domainBuilder.append(".")
                }
                for (i in 0 until labelLen) {
                    domainBuilder.append(buffer[pos + i].toInt().toChar())
                }
                pos += labelLen
            }
            return domainBuilder.toString().lowercase()
        } catch (e: Exception) {
            return null
        }
    }

    private fun stopVpn() {
        isRunning = false
        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        vpnInterface = null
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "BetShield Protection Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifies when active gambling website & app blocking is operating."
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("BetShield Protection Active")
            .setContentText("Safeguarding device against betting and gambling services.")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpn()
        serviceScope.cancel()
    }
}

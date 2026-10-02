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
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer

class DnsVpnService : VpnService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var vpnInterface: ParcelFileDescriptor? = null
    private lateinit var repository: BlockingRepository
    private lateinit var pinManager: PinManager
    private var isRunning = false

    private val upstreamDnsIps = listOf("1.1.1.1", "8.8.8.8")
    private val localDnsIp = "10.1.10.1"

    companion object {
        const val ACTION_START = "com.example.service.DnsVpnService.START"
        const val ACTION_STOP = "com.example.service.DnsVpnService.STOP"
        private const val CHANNEL_ID = "betshield_protection_channel"
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            try {
                val intent = Intent(context, DnsVpnService::class.java).apply {
                    action = ACTION_START
                }
                context.startService(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun stop(context: Context) {
            try {
                val intent = Intent(context, DnsVpnService::class.java).apply {
                    action = ACTION_STOP
                }
                context.startService(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        try {
            val db = AppDatabase.getInstance(this)
            repository = BlockingRepository(
                db.blockedDomainDao(),
                db.blockedAppDao(),
                db.blockEventDao(),
                db.syncMetadataDao()
            )
            pinManager = PinManager(this)
            createNotificationChannel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                    try {
                        startForeground(NOTIFICATION_ID, buildNotification())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
                .addAddress(localDnsIp, 32)
                .addDnsServer(localDnsIp)
                .addRoute(localDnsIp, 32)
                // Intercept queries targeted at popular public DNS resolvers
                .addRoute("1.1.1.1", 32)
                .addRoute("1.0.0.1", 32)
                .addRoute("8.8.8.8", 32)
                .addRoute("8.8.4.4", 32)
                .addRoute("9.9.9.9", 32)
                .addRoute("208.67.222.222", 32)
                .addRoute("208.67.220.220", 32)

            // Allow BetShield itself to bypass VPN
            try {
                builder.addDisallowedApplication(packageName)
            } catch (e: Exception) {
                // Ignore if not permitted
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
        val buffer = ByteArray(32767)

        // Socket for forwarding safe queries to upstream DNS
        var upstreamSocket: DatagramSocket? = null
        try {
            upstreamSocket = DatagramSocket()
            protect(upstreamSocket)
            upstreamSocket.soTimeout = 2500
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val upstreamInetAddresses = upstreamDnsIps.mapNotNull {
            runCatching { InetAddress.getByName(it) }.getOrNull()
        }

        while (serviceScope.isActive && isRunning) {
            try {
                val length = inputStream.read(buffer)
                if (length > 0) {
                    val query = DnsPacketHandler.parseQuery(buffer, length)
                    if (query != null) {
                        val isBlocked = pinManager.isProtectionEnabled && repository.isDomainBlocked(query.domain)

                        if (isBlocked) {
                            // Synthesize DNS answer returning 0.0.0.0
                            val responsePacket = DnsPacketHandler.buildBlockedResponse(query)
                            synchronized(outputStream) {
                                outputStream.write(responsePacket)
                                outputStream.flush()
                            }

                            // Log block event
                            serviceScope.launch {
                                val details = repository.getDomainDetails(query.domain)
                                repository.recordBlockEvent(
                                    target = query.domain,
                                    serviceName = details?.serviceName ?: query.domain,
                                    category = details?.category ?: "Gambling Website",
                                    targetType = "DOMAIN"
                                )
                            }
                        } else {
                            // Forward query to upstream DNS
                            upstreamSocket?.let { sock ->
                                serviceScope.launch(Dispatchers.IO) {
                                    forwardDnsQuery(sock, query, upstreamInetAddresses, outputStream)
                                }
                            }
                        }
                    }
                } else {
                    kotlinx.coroutines.delay(20)
                }
            } catch (e: Exception) {
                if (!isRunning) break
            }
        }

        try {
            upstreamSocket?.close()
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun forwardDnsQuery(
        socket: DatagramSocket,
        query: DnsQuery,
        upstreams: List<InetAddress>,
        outputStream: FileOutputStream
    ) {
        for (upstream in upstreams) {
            try {
                val sendPacket = DatagramPacket(query.dnsPayload, query.dnsPayload.size, upstream, 53)
                synchronized(socket) {
                    socket.send(sendPacket)
                    val recvBuffer = ByteArray(1500)
                    val recvPacket = DatagramPacket(recvBuffer, recvBuffer.size)
                    socket.receive(recvPacket)

                    val responseDnsPayload = recvBuffer.copyOf(recvPacket.length)
                    val ipUdpPacket = DnsPacketHandler.buildForwardResponse(query, responseDnsPayload)

                    synchronized(outputStream) {
                        outputStream.write(ipUdpPacket)
                        outputStream.flush()
                    }
                }
                return
            } catch (e: Exception) {
                // Try next upstream
            }
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

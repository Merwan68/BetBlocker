package com.example.service

import java.nio.ByteBuffer

data class DnsQuery(
    val srcIp: ByteArray,
    val dstIp: ByteArray,
    val srcPort: Int,
    val dstPort: Int,
    val ipHeaderLen: Int,
    val dnsTransactionId: Int,
    val domain: String,
    val qType: Int,
    val qClass: Int,
    val dnsPayload: ByteArray
)

object DnsPacketHandler {

    /**
     * Parses an IPv4 UDP DNS packet. Returns null if not a valid DNS query on port 53.
     */
    fun parseQuery(buffer: ByteArray, length: Int): DnsQuery? {
        if (length < 28) return null

        // Check IPv4 (version 4)
        val version = (buffer[0].toInt() and 0xF0) ushr 4
        if (version != 4) return null

        val ipHeaderLen = (buffer[0].toInt() and 0x0F) * 4
        if (ipHeaderLen < 20 || ipHeaderLen + 8 > length) return null

        // Check UDP protocol (17)
        if (buffer[9].toInt() != 17) return null

        val srcIp = buffer.copyOfRange(12, 16)
        val dstIp = buffer.copyOfRange(16, 20)

        val udpOffset = ipHeaderLen
        val srcPort = ((buffer[udpOffset].toInt() and 0xFF) shl 8) or (buffer[udpOffset + 1].toInt() and 0xFF)
        val dstPort = ((buffer[udpOffset + 2].toInt() and 0xFF) shl 8) or (buffer[udpOffset + 3].toInt() and 0xFF)

        // Only handle queries sent to port 53
        if (dstPort != 53) return null

        val udpLen = ((buffer[udpOffset + 4].toInt() and 0xFF) shl 8) or (buffer[udpOffset + 5].toInt() and 0xFF)
        if (udpOffset + udpLen > length) return null

        val dnsOffset = udpOffset + 8
        val dnsLen = udpLen - 8
        if (dnsLen < 12) return null

        val dnsPayload = buffer.copyOfRange(dnsOffset, dnsOffset + dnsLen)
        val transactionId = ((dnsPayload[0].toInt() and 0xFF) shl 8) or (dnsPayload[1].toInt() and 0xFF)
        val flags = ((dnsPayload[2].toInt() and 0xFF) shl 8) or (dnsPayload[3].toInt() and 0xFF)

        // Check if QR bit is 0 (query)
        if ((flags and 0x8000) != 0) return null

        val qdCount = ((dnsPayload[4].toInt() and 0xFF) shl 8) or (dnsPayload[5].toInt() and 0xFF)
        if (qdCount < 1) return null

        // Parse Question domain name
        var pos = 12
        val domainBuilder = StringBuilder()

        while (pos < dnsLen) {
            val labelLen = dnsPayload[pos].toInt() and 0xFF
            if (labelLen == 0) {
                pos++
                break
            }
            // Check for compression pointer (should not occur in root question, but handle safely)
            if ((labelLen and 0xC0) == 0xC0) {
                pos += 2
                break
            }
            pos++
            if (pos + labelLen > dnsLen) return null

            if (domainBuilder.isNotEmpty()) {
                domainBuilder.append(".")
            }
            for (i in 0 until labelLen) {
                domainBuilder.append(dnsPayload[pos + i].toInt().toChar())
            }
            pos += labelLen
        }

        if (pos + 4 > dnsLen) return null
        val qType = ((dnsPayload[pos].toInt() and 0xFF) shl 8) or (dnsPayload[pos + 1].toInt() and 0xFF)
        val qClass = ((dnsPayload[pos + 2].toInt() and 0xFF) shl 8) or (dnsPayload[pos + 3].toInt() and 0xFF)

        val domain = domainBuilder.toString().lowercase()

        return DnsQuery(
            srcIp = srcIp,
            dstIp = dstIp,
            srcPort = srcPort,
            dstPort = dstPort,
            ipHeaderLen = ipHeaderLen,
            dnsTransactionId = transactionId,
            domain = domain,
            qType = qType,
            qClass = qClass,
            dnsPayload = dnsPayload
        )
    }

    /**
     * Builds a DNS response packet resolving the blocked domain to 0.0.0.0 (A record) or NXDOMAIN.
     */
    fun buildBlockedResponse(query: DnsQuery): ByteArray {
        val questionBytes = query.dnsPayload.copyOfRange(12, query.dnsPayload.size)

        val dnsResponse = ByteBuffer.allocate(512)
        // Transaction ID
        dnsResponse.putShort(query.dnsTransactionId.toShort())
        // Flags: Standard query response, Authoritative, Recursion Desired, Recursion Available
        dnsResponse.putShort(0x8180.toShort())
        // QDCOUNT: 1
        dnsResponse.putShort(1)
        // ANCOUNT: 1 if A record (qType == 1), else 0
        if (query.qType == 1) {
            dnsResponse.putShort(1)
        } else {
            dnsResponse.putShort(0)
        }
        // NSCOUNT: 0
        dnsResponse.putShort(0)
        // ARCOUNT: 0
        dnsResponse.putShort(0)

        // Question section
        dnsResponse.put(questionBytes)

        // Answer section (if type A IPv4)
        if (query.qType == 1) {
            // Pointer to domain name in Question section (offset 12 = 0xC00C)
            dnsResponse.putShort(0xC00C.toShort())
            // Type: A (1)
            dnsResponse.putShort(1)
            // Class: IN (1)
            dnsResponse.putShort(1)
            // TTL: 60 seconds
            dnsResponse.putInt(60)
            // RDLENGTH: 4 bytes
            dnsResponse.putShort(4)
            // RDATA: 0.0.0.0 (Sinkhole loopback address)
            dnsResponse.put(byteArrayOf(0, 0, 0, 0))
        }

        dnsResponse.flip()
        val dnsBytes = ByteArray(dnsResponse.remaining())
        dnsResponse.get(dnsBytes)

        return wrapInIpUdp(query.dstIp, query.srcIp, query.dstPort, query.srcPort, dnsBytes)
    }

    /**
     * Wraps an upstream DNS response payload in IPv4 + UDP packet directed back to the original client.
     */
    fun buildForwardResponse(query: DnsQuery, upstreamDnsResponse: ByteArray): ByteArray {
        return wrapInIpUdp(query.dstIp, query.srcIp, query.dstPort, query.srcPort, upstreamDnsResponse)
    }

    private fun wrapInIpUdp(
        srcIp: ByteArray,
        dstIp: ByteArray,
        srcPort: Int,
        dstPort: Int,
        payload: ByteArray
    ): ByteArray {
        val udpLen = 8 + payload.size
        val totalIpLen = 20 + udpLen
        val packet = ByteBuffer.allocate(totalIpLen)

        // --- IPv4 Header (20 bytes) ---
        packet.put(0x45.toByte()) // Version 4, IHL 5
        packet.put(0x00.toByte()) // DSCP / ECN
        packet.putShort(totalIpLen.toShort()) // Total Length
        packet.putShort(0x0000.toShort()) // Identification
        packet.putShort(0x4000.toShort()) // Flags (Don't Fragment), Fragment Offset 0
        packet.put(64.toByte()) // TTL
        packet.put(17.toByte()) // Protocol (UDP)
        packet.putShort(0x0000.toShort()) // Checksum placeholder
        packet.put(srcIp) // Source IP
        packet.put(dstIp) // Destination IP

        // Compute and insert IPv4 Checksum
        val ipChecksum = computeIpChecksum(packet.array(), 0, 20)
        packet.putShort(10, ipChecksum.toShort())

        // --- UDP Header (8 bytes) ---
        packet.putShort(srcPort.toShort())
        packet.putShort(dstPort.toShort())
        packet.putShort(udpLen.toShort())
        packet.putShort(0x0000.toShort()) // Zero checksum is valid in IPv4 UDP (RFC 768)

        // --- Payload ---
        packet.put(payload)

        return packet.array()
    }

    private fun computeIpChecksum(data: ByteArray, offset: Int, length: Int): Int {
        var sum = 0
        var i = offset
        while (i < offset + length) {
            val high = data[i].toInt() and 0xFF
            val low = data[i + 1].toInt() and 0xFF
            sum += (high shl 8) or low
            i += 2
        }
        while (sum shr 16 != 0) {
            sum = (sum and 0xFFFF) + (sum shr 16)
        }
        return (sum.inv()) and 0xFFFF
    }
}

package com.example

import com.example.service.DnsPacketHandler
import com.example.service.DnsQuery
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer

class DnsPacketHandlerTest {

    @Test
    fun testBuildAndParseBlockedResponse() {
        val srcIp = byteArrayOf(10, 1, 10, 2)
        val dstIp = byteArrayOf(10, 1, 10, 1)
        val srcPort = 54321
        val dstPort = 53
        val txnId = 0x1234
        val domain = "bet365.com"

        // Mock DNS query payload
        val dnsBuffer = ByteBuffer.allocate(64)
        dnsBuffer.putShort(txnId.toShort())
        dnsBuffer.putShort(0x0100.toShort()) // Standard query
        dnsBuffer.putShort(1) // QDCOUNT
        dnsBuffer.putShort(0)
        dnsBuffer.putShort(0)
        dnsBuffer.putShort(0)

        // Question: bet365.com -> 6bet3653com0
        dnsBuffer.put(6.toByte())
        dnsBuffer.put("bet365".toByteArray())
        dnsBuffer.put(3.toByte())
        dnsBuffer.put("com".toByteArray())
        dnsBuffer.put(0.toByte())
        dnsBuffer.putShort(1) // Type A
        dnsBuffer.putShort(1) // Class IN

        dnsBuffer.flip()
        val dnsPayload = ByteArray(dnsBuffer.remaining())
        dnsBuffer.get(dnsPayload)

        val query = DnsQuery(
            srcIp = srcIp,
            dstIp = dstIp,
            srcPort = srcPort,
            dstPort = dstPort,
            ipHeaderLen = 20,
            dnsTransactionId = txnId,
            domain = domain,
            qType = 1,
            qClass = 1,
            dnsPayload = dnsPayload
        )

        val blockedPacket = DnsPacketHandler.buildBlockedResponse(query)
        assertNotNull(blockedPacket)
        assertTrue("Packet should have IP, UDP, and DNS headers", blockedPacket.size > 28)

        // Verify destination port of response is original source port
        val respUdpOffset = 20
        val respDstPort = ((blockedPacket[respUdpOffset + 2].toInt() and 0xFF) shl 8) or
                (blockedPacket[respUdpOffset + 3].toInt() and 0xFF)
        assertEquals(srcPort, respDstPort)

        // Verify transaction ID matches
        val respDnsOffset = respUdpOffset + 8
        val respTxnId = ((blockedPacket[respDnsOffset].toInt() and 0xFF) shl 8) or
                (blockedPacket[respDnsOffset + 1].toInt() and 0xFF)
        assertEquals(txnId, respTxnId)

        // Verify ANCOUNT is 1 (resolved to 0.0.0.0)
        val respAnCount = ((blockedPacket[respDnsOffset + 6].toInt() and 0xFF) shl 8) or
                (blockedPacket[respDnsOffset + 7].toInt() and 0xFF)
        assertEquals(1, respAnCount)
    }
}

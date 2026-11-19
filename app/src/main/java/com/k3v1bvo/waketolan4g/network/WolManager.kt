package com.k3v1bvo.waketolan4g.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class WolManager(private val context: Context) {

    suspend fun sendMagicPacket(
        macAddress: String,
        targetHost: String,
        port: Int = 9
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val cleanMac = macAddress.replace(":", "").replace("-", "").trim()
            require(cleanMac.length == 12) { "Dirección MAC inválida: debe tener 12 caracteres hexadecimales" }

            val macBytes = ByteArray(6) { i ->
                cleanMac.substring(i * 2, (i + 1) * 2).toInt(16).toByte()
            }

            // Paquete Mágico: 6 bytes de 0xFF seguidos de 16 repeticiones de la dirección MAC
            val packetBytes = ByteArray(102)
            for (i in 0 until 6) {
                packetBytes[i] = 0xFF.toByte()
            }
            for (i in 0 until 16) {
                System.arraycopy(macBytes, 0, packetBytes, 6 + i * 6, 6)
            }

            val address = InetAddress.getByName(targetHost)
            val socket = DatagramSocket()
            socket.broadcast = true

            val packet = DatagramPacket(packetBytes, packetBytes.size, address, port)
            socket.send(packet)
            socket.close()

            Result.success("Paquete mágico enviado a $targetHost:$port")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isConnectedToHomeWifi(homeSsid: String): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val activeNetwork = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }
}

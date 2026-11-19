package com.k3v1bvo.waketolan4g.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("waketolan_prefs", Context.MODE_PRIVATE)

    var macAddress: String
        get() = prefs.getString("mac_address", "70:4D:7B:87:92:53") ?: "70:4D:7B:87:92:53"
        set(value) = prefs.edit().putString("mac_address", value.trim()).apply()

    var broadcastIp: String
        get() = prefs.getString("broadcast_ip", "192.168.1.255") ?: "192.168.1.255"
        set(value) = prefs.edit().putString("broadcast_ip", value.trim()).apply()

    var localPcIp: String
        get() = prefs.getString("local_pc_ip", "192.168.1.91") ?: "192.168.1.91"
        set(value) = prefs.edit().putString("local_pc_ip", value.trim()).apply()

    var remoteHost: String
        get() = prefs.getString("remote_host", "k3v1bvopici.duckdns.org") ?: "k3v1bvopici.duckdns.org"
        set(value) = prefs.edit().putString("remote_host", value.trim()).apply()

    var wolPort: Int
        get() = prefs.getInt("wol_port", 9)
        set(value) = prefs.edit().putInt("wol_port", value).apply()

    var agentPort: Int
        get() = prefs.getInt("agent_port", 8080)
        set(value) = prefs.edit().putInt("agent_port", value).apply()

    var secretToken: String
        get() = prefs.getString("secret_token", "kevin2026") ?: "kevin2026"
        set(value) = prefs.edit().putString("secret_token", value.trim()).apply()

    var homeSsid: String
        get() = prefs.getString("home_ssid", "Xiaomi_911") ?: "Xiaomi_911"
        set(value) = prefs.edit().putString("home_ssid", value.trim()).apply()
}

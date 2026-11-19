package com.k3v1bvo.waketolan4g

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.k3v1bvo.waketolan4g.data.PcStatus
import com.k3v1bvo.waketolan4g.data.PreferencesManager
import com.k3v1bvo.waketolan4g.network.PcClient
import com.k3v1bvo.waketolan4g.network.WolManager
import com.k3v1bvo.waketolan4g.ui.screens.HomeScreen
import com.k3v1bvo.waketolan4g.ui.theme.BgDark
import com.k3v1bvo.waketolan4g.ui.theme.WakeToLan4GTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var prefs: PreferencesManager
    private lateinit var wolManager: WolManager
    private val pcClient = PcClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = PreferencesManager(applicationContext)
        wolManager = WolManager(applicationContext)

        setContent {
            WakeToLan4GTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDark
                ) {
                    var pcStatus by remember { mutableStateOf(PcStatus()) }
                    val coroutineScope = rememberCoroutineScope()

                    fun refreshStatus() {
                        coroutineScope.launch {
                            val isHomeWifi = wolManager.isConnectedToHomeWifi(prefs.homeSsid)
                            val host = if (isHomeWifi) prefs.localPcIp else prefs.remoteHost
                            val result = pcClient.getStatus(host, prefs.agentPort, prefs.secretToken)
                            result.fold(
                                onSuccess = { status ->
                                    pcStatus = status
                                },
                                onFailure = {
                                    pcStatus = pcStatus.copy(online = false, latencyMs = 0)
                                }
                            )
                        }
                    }

                    // Polling periódico cada 4 segundos
                    LaunchedEffect(Unit) {
                        while (isActive) {
                            val isHomeWifi = wolManager.isConnectedToHomeWifi(prefs.homeSsid)
                            val host = if (isHomeWifi) prefs.localPcIp else prefs.remoteHost
                            val result = pcClient.getStatus(host, prefs.agentPort, prefs.secretToken)
                            result.fold(
                                onSuccess = { status ->
                                    pcStatus = status
                                },
                                onFailure = {
                                    pcStatus = pcStatus.copy(online = false, latencyMs = 0)
                                }
                            )
                            delay(4000)
                        }
                    }

                    HomeScreen(
                        prefs = prefs,
                        wolManager = wolManager,
                        pcClient = pcClient,
                        status = pcStatus,
                        onRefreshStatus = { refreshStatus() }
                    )
                }
            }
        }
    }
}

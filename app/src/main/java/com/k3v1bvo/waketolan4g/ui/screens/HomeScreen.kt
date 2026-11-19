package com.k3v1bvo.waketolan4g.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.k3v1bvo.waketolan4g.data.PcStatus
import com.k3v1bvo.waketolan4g.data.PreferencesManager
import com.k3v1bvo.waketolan4g.network.PcClient
import com.k3v1bvo.waketolan4g.network.WolManager
import com.k3v1bvo.waketolan4g.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    prefs: PreferencesManager,
    wolManager: WolManager,
    pcClient: PcClient,
    status: PcStatus,
    onRefreshStatus: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSettings by remember { mutableStateOf(false) }
    var showShutdownConfirm by remember { mutableStateOf(false) }

    fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(50)
        }
    }

    fun showMsg(msg: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(msg)
        }
    }

    val isHomeWifi = wolManager.isConnectedToHomeWifi(prefs.homeSsid)
    val activeHost = if (isHomeWifi) prefs.localPcIp else prefs.remoteHost

    Scaffold(
        containerColor = BgDark,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WAKETOLAN 4G",
                        style = MaterialTheme.typography.titleLarge,
                        color = CyanGlow,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "PC COMMAND CENTER",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        letterSpacing = 2.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Badge Online / Offline
                    Surface(
                        color = if (status.online) GreenGlow.copy(alpha = 0.15f) else RedGlow.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, if (status.online) GreenGlow else RedGlow),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (status.online) GreenGlow else RedGlow, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (status.online) "ONLINE ${status.latencyMs}ms" else "OFFLINE",
                                color = if (status.online) GreenGlow else RedGlow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuración", tint = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Network Mode Pill
            Surface(
                color = CardDark,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isHomeWifi) Icons.Default.Wifi else Icons.Default.CellTower,
                            contentDescription = null,
                            tint = CyanGlow
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isHomeWifi) "Modo Red Local (Wi-Fi Casa)" else "Modo Red Remota (4G / Internet)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isHomeWifi) "Destino: ${prefs.broadcastIp}" else "Destino: ${prefs.remoteHost}",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Hero Power Button
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.08f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseScale"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer Glow Circle
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .scale(if (!status.online) pulseScale else 1f)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    (if (status.online) GreenGlow else CyanGlow).copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Interactive Center Button
                Surface(
                    shape = CircleShape,
                    color = CardDark,
                    border = BorderStroke(3.dp, if (status.online) GreenGlow else CyanGlow),
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .size(130.dp)
                        .clickable {
                            vibrate()
                            coroutineScope.launch {
                                val target = if (isHomeWifi) prefs.broadcastIp else prefs.remoteHost
                                val res = wolManager.sendMagicPacket(prefs.macAddress, target, prefs.wolPort)
                                res.fold(
                                    onSuccess = { showMsg("⚡ Paquete mágico enviado a $target") },
                                    onFailure = { showMsg("❌ Error: ${it.localizedMessage}") }
                                )
                                onRefreshStatus()
                            }
                        }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Encender",
                            tint = if (status.online) GreenGlow else CyanGlow,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (status.online) "EN LÍNEA" else "ENCENDER",
                            color = if (status.online) GreenGlow else CyanGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Actions Grid
            Text(
                text = "Acciones Rápidas",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionCard(
                    title = "WOL Local",
                    subtitle = "192.168.1.255",
                    icon = Icons.Default.Wifi,
                    color = CyanGlow,
                    modifier = Modifier.weight(1f)
                ) {
                    vibrate()
                    coroutineScope.launch {
                        val res = wolManager.sendMagicPacket(prefs.macAddress, prefs.broadcastIp, prefs.wolPort)
                        showMsg(res.getOrDefault("Enviado por red local"))
                    }
                }

                ActionCard(
                    title = "WOL 4G (DuckDNS)",
                    subtitle = "Puerto 9",
                    icon = Icons.Default.Public,
                    color = PurpleGlow,
                    modifier = Modifier.weight(1f)
                ) {
                    vibrate()
                    coroutineScope.launch {
                        val res = wolManager.sendMagicPacket(prefs.macAddress, prefs.remoteHost, prefs.wolPort)
                        showMsg(res.getOrDefault("Enviado por Internet (4G)"))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionCard(
                    title = "Suspender",
                    subtitle = "Modo reposo (S3)",
                    icon = Icons.Default.Bedtime,
                    color = YellowGlow,
                    modifier = Modifier.weight(1f)
                ) {
                    vibrate()
                    val host = activeHost
                    coroutineScope.launch {
                        pcClient.executeCommand(host, prefs.agentPort, prefs.secretToken, "power/sleep")
                            .fold(
                                onSuccess = { showMsg("💤 PC suspendida"); onRefreshStatus() },
                                onFailure = { showMsg("Error: Asegúrate que el agente esté abierto en la PC") }
                            )
                    }
                }

                ActionCard(
                    title = "Bloquear",
                    subtitle = "Win + L",
                    icon = Icons.Default.Lock,
                    color = CyanGlow,
                    modifier = Modifier.weight(1f)
                ) {
                    vibrate()
                    val host = activeHost
                    coroutineScope.launch {
                        pcClient.executeCommand(host, prefs.agentPort, prefs.secretToken, "power/lock")
                            .fold(
                                onSuccess = { showMsg("🔒 PC bloqueada") },
                                onFailure = { showMsg("Error al conectar con agente") }
                            )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionCard(
                    title = "Reiniciar",
                    subtitle = "Reboot seguro",
                    icon = Icons.Default.Refresh,
                    color = PurpleGlow,
                    modifier = Modifier.weight(1f)
                ) {
                    vibrate()
                    val host = activeHost
                    coroutineScope.launch {
                        pcClient.executeCommand(host, prefs.agentPort, prefs.secretToken, "power/restart")
                            .fold(
                                onSuccess = { showMsg("🔄 Reiniciando PC..."); onRefreshStatus() },
                                onFailure = { showMsg("Error al conectar") }
                            )
                    }
                }

                ActionCard(
                    title = "Apagar PC",
                    subtitle = "Cierre completo",
                    icon = Icons.Default.PowerOff,
                    color = RedGlow,
                    modifier = Modifier.weight(1f)
                ) {
                    vibrate()
                    showShutdownConfirm = true
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Telemetry Card (Hardware Monitor)
            Text(
                text = "Telemetría en Vivo",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = CardDark,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Procesador (CPU)", color = TextSecondary, fontSize = 13.sp)
                        Text("${status.cpuPercent.toInt()}%", color = CyanGlow, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (status.cpuPercent / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = CyanGlow,
                        trackColor = CardBorder
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Memoria RAM", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            "${status.ramUsedGb} GB / ${status.ramTotalGb} GB",
                            color = PurpleGlow,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = {
                            if (status.ramTotalGb > 0) (status.ramUsedGb / status.ramTotalGb).coerceIn(0f, 1f) else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = PurpleGlow,
                        trackColor = CardBorder
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Almacenamiento SSD (C:)", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            "${status.diskFreeGb} GB libres de ${status.diskTotalGb} GB",
                            color = GreenGlow,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = {
                            if (status.diskTotalGb > 0) ((status.diskTotalGb - status.diskFreeGb) / status.diskTotalGb).coerceIn(0f, 1f) else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = GreenGlow,
                        trackColor = CardBorder
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Volume & Media Card
            Text(
                text = "Control de Audio y Medios",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = CardDark,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (status.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = CyanGlow
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Volumen de Windows", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                        Text("${status.masterVolume}%", color = CyanGlow, fontWeight = FontWeight.Bold)
                    }

                    var sliderValue by remember(status.masterVolume) { mutableFloatStateOf(status.masterVolume.toFloat()) }

                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        onValueChangeFinished = {
                            val host = activeHost
                            coroutineScope.launch {
                                pcClient.executeCommand(
                                    host,
                                    prefs.agentPort,
                                    prefs.secretToken,
                                    "volume",
                                    "{\"volume\": ${sliderValue.toInt()}}"
                                )
                            }
                        },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanGlow,
                            activeTrackColor = CyanGlow,
                            inactiveTrackColor = CardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilledTonalButton(
                            onClick = {
                                val host = activeHost
                                coroutineScope.launch {
                                    pcClient.executeCommand(host, prefs.agentPort, prefs.secretToken, "media/playpause")
                                }
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = SurfaceOverlay)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play/Pause", tint = CyanGlow)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play / Pausa", color = TextPrimary)
                        }

                        FilledTonalButton(
                            onClick = {
                                val host = activeHost
                                coroutineScope.launch {
                                    pcClient.executeCommand(host, prefs.agentPort, prefs.secretToken, "volume/mute")
                                }
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = SurfaceOverlay)
                        ) {
                            Icon(Icons.Default.VolumeMute, contentDescription = "Mute", tint = RedGlow)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Silenciar", color = TextPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }

    if (showSettings) {
        SettingsDialog(prefs = prefs, onDismiss = { showSettings = false })
    }

    if (showShutdownConfirm) {
        AlertDialog(
            onDismissRequest = { showShutdownConfirm = false },
            title = { Text("¿Apagar Computadora?", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Esta acción cerrará todos los programas y apagará tu PC por completo.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showShutdownConfirm = false
                        val host = activeHost
                        coroutineScope.launch {
                            pcClient.executeCommand(host, prefs.agentPort, prefs.secretToken, "power/shutdown")
                                .fold(
                                    onSuccess = { showMsg("🛑 Apagando PC..."); onRefreshStatus() },
                                    onFailure = { showMsg("Error al conectar") }
                                )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedGlow)
                ) {
                    Text("Apagar Ahora", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showShutdownConfirm = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = CardDark,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = CardDark,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
            }
        }
    }
}

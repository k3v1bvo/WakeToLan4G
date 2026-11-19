package com.k3v1bvo.waketolan4g.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.k3v1bvo.waketolan4g.data.PreferencesManager
import com.k3v1bvo.waketolan4g.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    prefs: PreferencesManager,
    onDismiss: () -> Unit
) {
    var mac by remember { mutableStateOf(prefs.macAddress) }
    var broadcastIp by remember { mutableStateOf(prefs.broadcastIp) }
    var localPcIp by remember { mutableStateOf(prefs.localPcIp) }
    var remoteHost by remember { mutableStateOf(prefs.remoteHost) }
    var wolPort by remember { mutableStateOf(prefs.wolPort.toString()) }
    var agentPort by remember { mutableStateOf(prefs.agentPort.toString()) }
    var secretToken by remember { mutableStateOf(prefs.secretToken) }
    var homeSsid by remember { mutableStateOf(prefs.homeSsid) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CardDark,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Configuración",
                        style = MaterialTheme.typography.titleLarge,
                        color = CyanGlow,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = mac,
                    onValueChange = { mac = it },
                    label = { Text("Dirección MAC de la PC") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        focusedLabelColor = CyanGlow,
                        cursorColor = CyanGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = broadcastIp,
                    onValueChange = { broadcastIp = it },
                    label = { Text("IP de Difusión Local (Broadcast)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        focusedLabelColor = CyanGlow,
                        cursorColor = CyanGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = localPcIp,
                    onValueChange = { localPcIp = it },
                    label = { Text("IP Local de la PC (Ej: 192.168.1.91)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        focusedLabelColor = CyanGlow,
                        cursorColor = CyanGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = remoteHost,
                    onValueChange = { remoteHost = it },
                    label = { Text("Dominio Remoto / 4G (DuckDNS)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanGlow,
                        focusedLabelColor = CyanGlow,
                        cursorColor = CyanGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = wolPort,
                        onValueChange = { wolPort = it },
                        label = { Text("Puerto WOL") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = agentPort,
                        onValueChange = { agentPort = it },
                        label = { Text("Puerto Agente") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = secretToken,
                    onValueChange = { secretToken = it },
                    label = { Text("Token Secreto de Seguridad") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = homeSsid,
                    onValueChange = { homeSsid = it },
                    label = { Text("Nombre del Wi-Fi de Casa (SSID)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        prefs.macAddress = mac
                        prefs.broadcastIp = broadcastIp
                        prefs.localPcIp = localPcIp
                        prefs.remoteHost = remoteHost
                        prefs.wolPort = wolPort.toIntOrNull() ?: 9
                        prefs.agentPort = agentPort.toIntOrNull() ?: 8080
                        prefs.secretToken = secretToken
                        prefs.homeSsid = homeSsid
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanGlow),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = BgDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Cambios", color = BgDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

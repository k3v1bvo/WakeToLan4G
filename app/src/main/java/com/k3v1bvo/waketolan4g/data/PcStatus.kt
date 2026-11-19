package com.k3v1bvo.waketolan4g.data

data class PcStatus(
    val online: Boolean = false,
    val hostname: String = "PCK3V1N",
    val cpuPercent: Float = 0f,
    val ramUsedGb: Float = 0f,
    val ramTotalGb: Float = 16f,
    val diskFreeGb: Float = 0f,
    val diskTotalGb: Float = 0f,
    val masterVolume: Int = 50,
    val isMuted: Boolean = false,
    val latencyMs: Long = 0
)

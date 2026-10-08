package dev.aplominski.openbatterytrack

data class BatteryUiState(
    val healthPercent: Int? = null,
    val healthLabel: String = "Unknown",
    val levelPercent: Int = 0,
    val isCharging: Boolean = false,
    val pluggedText: String = "Unplugged",
    val sourceText: String = "—",
    val cyclesText: String = "Unknown",
    val temperatureText: String = "—",
    val voltageText: String = "—",
    val currentText: String = "—",
    val capacityText: String = "—",
    val technologyText: String = "—",
    val statusText: String = "—"
)

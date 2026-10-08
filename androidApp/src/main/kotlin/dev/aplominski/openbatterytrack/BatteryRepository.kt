package dev.aplominski.openbatterytrack

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build

class BatteryRepository(private val context: Context) {

    private val batteryManager =
        context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    fun readCurrent(): BatteryUiState {
        val intent: Intent? = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        return map(intent)
    }

    fun map(intent: Intent?): BatteryUiState {
        if (intent == null) return BatteryUiState()

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val isPlugged = plugged != 0
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
        val isHoldLimit = isPlugged && !isCharging
        val pluggedText = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "Plugged"
            BatteryManager.BATTERY_PLUGGED_USB -> "Plugged"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Plugged"
            0 -> "Unplugged"
            else -> "Unplugged"
        }
        val sourceText = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            0 -> "—"
            else -> "—"
        }

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
        val levelPercent = if (level >= 0 && scale > 0) (level * 100 / scale) else 0

        val statusText = if (isHoldLimit) {
            "Charge limited"
        } else when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
            else -> "Unknown"
        }

        val healthInt = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        val healthLabel = mapHealth(healthInt)

        val tempTenths = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        val temperatureText = if (tempTenths != Int.MIN_VALUE) {
            String.format("%.1f°C", tempTenths / 10.0)
        } else "—"

        val voltageMv = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        val voltageText = if (voltageMv > 0) String.format("%.2f V", voltageMv / 1000.0) else "—"

        val currentNowUa = try {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        } catch (_: Exception) { Int.MIN_VALUE }
        val currentText = if (currentNowUa != Int.MIN_VALUE) {
            String.format("%+.1f A", currentNowUa / 1_000_000.0)
        } else "—"

        val chargeCounterUah = try {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        } catch (_: Exception) { Int.MIN_VALUE }
        val capacityPercent = try {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } catch (_: Exception) { Int.MIN_VALUE }
        val capacityText = if (chargeCounterUah != Int.MIN_VALUE && chargeCounterUah > 0) {
            "${chargeCounterUah / 1000} / — mAh"
        } else if (capacityPercent != Int.MIN_VALUE) {
            "— / $capacityPercent%"
        } else "—"

        val cyclesText = readCycleCount(intent)

        val technologyText = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "—"

        return BatteryUiState(
            healthPercent = null,
            healthLabel = healthLabel,
            levelPercent = levelPercent,
            isCharging = isCharging || isHoldLimit,
            pluggedText = pluggedText,
            sourceText = sourceText,
            cyclesText = cyclesText,
            temperatureText = temperatureText,
            voltageText = voltageText,
            currentText = currentText,
            capacityText = capacityText,
            technologyText = technologyText,
            statusText = statusText
        )
    }

    private fun readCycleCount(intent: Intent?): String {
        val fromIntent = intent?.getIntExtra("android.os.extra.CYCLE_COUNT", -1) ?: -1
        if (fromIntent >= 0) return fromIntent.toString()
        val names = listOf("cycle_count", "cyclecount", "battery_cycle_count", "charge_cycle_count", "cycle")
        try {
            val supplies = java.io.File("/sys/class/power_supply").listFiles()?.map { it.name } ?: emptyList()
            for (supply in supplies) {
                for (name in names) {
                    val f = java.io.File("/sys/class/power_supply/$supply/$name")
                    try {
                        if (!f.exists() || !f.canRead()) continue
                        val raw = f.readText().trim()
                        val n = raw.toIntOrNull()
                        if (n != null && n >= 0) return n.toString()
                    } catch (_: Exception) {
                    }
                }
            }
        } catch (_: Exception) {
        }
        return "Unknown"
    }

    companion object {
        fun mapHealth(health: Int): String = when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "Unknown"
        }
    }
}

package dev.aplominski.openbatterytrack

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF121417)
private val Secondary = Color(0xFF9AA0A6)
private val Green = Color(0xFF22C55E)
private val Red = Color(0xFFEF4444)
private val Divider = Color(0xFF24282E)

@Composable
fun BatteryScreen(state: BatteryUiState) {
    val dotColor = if (state.isCharging) Green else Secondary
    val statusLine = if (state.sourceText == "—") {
        "${state.statusText} · ${state.pluggedText} · ${state.levelPercent}%"
    } else {
        "${state.statusText} · ${state.sourceText} · ${state.levelPercent}%"
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .safeContentPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Spacer(Modifier.height(32.dp))
        Text(
            text = "HEALTH",
            fontFamily = InterFamily,
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            color = Secondary
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = state.healthLabel,
                fontFamily = InterFamily,
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraLight,
                letterSpacing = (-2).sp,
                color = Color.White
            )
        }
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Text(
                text = "  $statusLine",
                fontFamily = InterFamily,
                fontSize = 13.sp,
                color = Secondary
            )
        }
        if (!state.isCharging && state.statusText != "Full") {
            Text(
                text = "Discharging",
                fontFamily = InterFamily,
                fontSize = 13.sp,
                color = Red,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().height(2.dp).background(Divider))
        Spacer(Modifier.height(8.dp))
        MetricRow("Level", "${state.levelPercent}%")
        MetricRow("Cycles", state.cyclesText)
        MetricRow("Status", state.statusText)
        MetricRow("Source", state.sourceText)
        MetricRow("Temperature", state.temperatureText)
        MetricRow("Voltage", state.voltageText)
        MetricRow("Current", state.currentText)
        MetricRow("Capacity", state.capacityText)
        MetricRow("Technology", state.technologyText)
        Spacer(Modifier.weight(1f))
        val uriHandler = LocalUriHandler.current
        Text(
            text = "Aleksander Płomiński 2026, GPL-3.0 License",
            fontFamily = InterFamily,
            fontSize = 11.sp,
            color = Secondary.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { uriHandler.openUri("https://github.com/aplominski/openbatterytrack") }
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = InterFamily,
            fontSize = 14.sp,
            color = Secondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontFamily = InterFamily,
            fontSize = 14.sp,
            color = Color.White
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121417)
@Composable
private fun BatteryScreenPreview() {
    MaterialTheme {
        BatteryScreen(
            BatteryUiState(
                healthLabel = "Good",
                levelPercent = 78,
                isCharging = true,
                pluggedText = "Plugged",
                sourceText = "USB",
                cyclesText = "312",
                temperatureText = "28.4°C",
                voltageText = "4.12 V",
                currentText = "+1.2 A",
                capacityText = "3850 / — mAh",
                technologyText = "Li-ion",
                statusText = "Charging"
            )
        )
    }
}

package com.mojbroj.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mojbroj.app.BuildConfig
import com.mojbroj.app.data.PlayerStats
import com.mojbroj.app.ui.theme.MojBrojType
import com.mojbroj.core.DifficultyMode
import java.util.Locale

@Composable
internal fun SettingsScreen(
    roundDurationSec: Int,
    onRoundDurationChange: (Int) -> Unit,
    difficultyMode: DifficultyMode,
    onDifficultyChange: (DifficultyMode) -> Unit,
    stats: PlayerStats,
    onBack: () -> Unit,
    onRules: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = "Podešavanja",
            titleColor = cs.primary,
            leading = { IconCircleButton(Icons.AutoMirrored.Filled.ArrowBack, "Nazad", onBack) }
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionLabel("IGRA")
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Timer, contentDescription = null, tint = cs.onSurface, modifier = Modifier.size(22.dp))
                            Text("Trajanje igre", style = MaterialTheme.typography.titleMedium, color = cs.onSurface)
                        }
                        Text("${roundDurationSec}s", style = MojBrojType.headlineLgMobile.copy(fontSize = 22.sp), color = cs.tertiary)
                    }
                    val options = listOf(60, 90, 120, 180)
                    SegmentedRow(
                        options = options.map { "${it}s" },
                        selectedIndex = options.indexOf(roundDurationSec).let { if (it < 0) 1 else it },
                        onSelect = { onRoundDurationChange(options[it]) }
                    )
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mod igre", style = MaterialTheme.typography.titleMedium, color = cs.onSurface, modifier = Modifier.weight(1f))
                    SegmentedRow(
                        options = listOf("Standard", "Dečiji"),
                        selectedIndex = if (difficultyMode == DifficultyMode.STANDARD) 0 else 1,
                        onSelect = { onDifficultyChange(if (it == 0) DifficultyMode.STANDARD else DifficultyMode.KIDS) },
                        compact = true
                    )
                }
            }

            SectionLabel("STATISTIKA")
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatRow("Odigrano partija", stats.totalGames.toString())
                    StatRow("Tačnih rešenja", stats.exactSolutions.toString())
                    StatRow("Prosečna razlika", "%.1f".format(Locale.US, stats.avgDistance))
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                "Moj Broj • verzija ${BuildConfig.VERSION_NAME}",
                style = MojBrojType.labelCaps,
                color = cs.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
        }
        BottomNav(AppScreen.SETTINGS, onPlay = onBack, onRules = onRules, onSettings = {})
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MojBrojType.labelCaps,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SegmentedRow(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    compact: Boolean = false
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(cs.surfaceContainerLow)
            .border(1.dp, cs.outlineVariant.copy(alpha = 0.3f), CircleShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (selected) cs.primaryContainer else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(horizontal = if (compact) 16.dp else 18.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    style = MojBrojType.labelCaps,
                    color = if (selected) cs.onPrimaryContainer else cs.onSurfaceVariant
                )
            }
        }
    }
}

package com.mojbroj.app.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mojbroj.app.data.currentEpochDay
import com.mojbroj.app.ui.theme.MojBrojType
import com.mojbroj.core.DifficultyMode

@Composable
internal fun HomeScreen(
    state: UiState,
    onStartGame: (DifficultyMode) -> Unit,
    onDailyChallenge: () -> Unit,
    onRules: () -> Unit,
    onSettings: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            timerSec = state.roundDurationSec,
            title = "Moj broj",
            titleAtStart = true,
            trailing = {
                Pill(text = "${state.stats.totalGames}", leadingIcon = Icons.Filled.Star)
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Izaberi mod igre",
                style = MojBrojType.headlineLg,
                color = cs.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Spremni za matematički izazov?",
                style = MojBrojType.bodyMd,
                color = cs.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))

            ModeCard(
                title = "Standardni mod",
                subtitle = "Klasično pravilo, 6 brojeva",
                icon = Icons.Filled.Psychology,
                container = cs.primaryContainer,
                content = cs.onPrimaryContainer,
                corner = 22,
                onClick = { onStartGame(DifficultyMode.STANDARD) }
            )
            Spacer(Modifier.height(16.dp))
            ModeCard(
                title = "Dečiji mod",
                subtitle = "Zabavno učenje bez žurbe",
                icon = Icons.Filled.ChildCare,
                container = cs.tertiary,
                content = cs.onTertiary,
                corner = 32,
                onClick = { onStartGame(DifficultyMode.KIDS) }
            )
            Spacer(Modifier.height(16.dp))
            DailyChallengeCard(state = state, onClick = onDailyChallenge)

            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onRules)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = cs.onSurfaceVariant, modifier = Modifier.size(18.dp))
                Text("Pravila igre", style = MojBrojType.labelCaps, color = cs.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
        }

        BottomNav(AppScreen.HOME, onPlay = {}, onRules = onRules, onSettings = onSettings)
    }
}

@Composable
private fun DailyChallengeCard(state: UiState, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val playedToday = state.daily.playedToday(currentEpochDay())

    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 22) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !playedToday, onClick = onClick)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Dnevni izazov", style = MojBrojType.headlineLgMobile, color = cs.onSurface)
                Spacer(Modifier.height(2.dp))
                if (playedToday) {
                    val resultText = when {
                        state.daily.lastExact -> "Danas: tačno rešenje!"
                        state.daily.lastDistance >= 0 -> "Danas: razlika ${state.daily.lastDistance}"
                        else -> "Danas: bez važećeg rešenja"
                    }
                    Text(resultText, style = MojBrojType.bodyMd.copy(fontSize = 14.sp), color = cs.onSurfaceVariant)
                } else {
                    Text(
                        "Ista slagalica za sve, jednom dnevno",
                        style = MojBrojType.bodyMd.copy(fontSize = 14.sp),
                        color = cs.onSurfaceVariant
                    )
                }
                if (state.daily.dailyStreak > 0) {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.Whatshot, contentDescription = null, tint = cs.tertiary, modifier = Modifier.size(16.dp))
                        Text("Niz: ${state.daily.dailyStreak} ${dayWord(state.daily.dailyStreak)}", style = MojBrojType.labelCaps, color = cs.tertiary)
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (playedToday) cs.primary.copy(alpha = 0.12f) else cs.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (playedToday) Icons.Filled.CheckCircle else Icons.Filled.Today,
                    contentDescription = null,
                    tint = if (playedToday) cs.primary else cs.onPrimaryContainer,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

private fun dayWord(count: Int): String = when {
    count % 10 == 1 && count % 100 != 11 -> "dan"
    else -> "dana"
}

@Composable
private fun ModeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    container: Color,
    content: Color,
    corner: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(corner.dp))
            .background(container)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MojBrojType.headlineLgMobile, color = content)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, style = MojBrojType.bodyMd.copy(fontSize = 14.sp), color = content.copy(alpha = 0.8f))
        }
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(content.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(30.dp))
        }
    }
}
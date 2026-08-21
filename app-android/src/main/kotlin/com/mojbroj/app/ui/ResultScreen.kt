package com.mojbroj.app.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HighlightOff
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mojbroj.app.ui.theme.MojBrojType
import com.mojbroj.core.model.EvaluationStatus
import java.util.Locale

@Composable
internal fun ResultScreen(state: UiState, onPlayAgain: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val context = LocalContext.current
    val round = state.currentRound ?: return
    val submitted = state.submitted
    val solver = state.solverResult

    val status = submitted?.status ?: EvaluationStatus.INVALID
    val (headline, ringColor, ringIcon) = when (status) {
        EvaluationStatus.EXACT -> Triple("Odlično!", cs.primary, Icons.Outlined.CheckCircle)
        EvaluationStatus.CLOSEST -> Triple("Blizu!", cs.secondary, Icons.Outlined.TrackChanges)
        EvaluationStatus.INVALID -> Triple("Probaj ponovo", cs.error, Icons.Outlined.HighlightOff)
    }
    val resultValue = submitted?.result

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            timerSec = 0,
            title = "REZULTAT",
            trailing = { Pill(text = "Cilj ${round.target}", leadingIcon = Icons.Filled.Star) }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            // Success ring
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(ringColor.copy(alpha = 0.20f), Color.Transparent)))
                )
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .background(cs.surfaceContainer)
                        .border(4.dp, ringColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(ringIcon, contentDescription = null, tint = ringColor, modifier = Modifier.size(60.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(headline, style = MojBrojType.headlineLgMobile, color = cs.onBackground)
            Spacer(Modifier.height(12.dp))
            Text("VAŠ REZULTAT", style = MojBrojType.labelCaps, color = cs.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(
                resultValue?.toString() ?: "—",
                style = MojBrojType.displayTarget,
                color = if (status == EvaluationStatus.INVALID) cs.onSurfaceVariant else cs.primary
            )

            Spacer(Modifier.height(28.dp))

            // Solution card
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("NAJBOLJE REŠENJE", style = MojBrojType.labelCaps, color = cs.tertiary)
                        Text("${solver?.result ?: round.target}", style = MojBrojType.headlineLgMobile, color = cs.tertiary)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(cs.outlineVariant.copy(alpha = 0.3f))
                    )
                    SolutionRow(label = "Rešenje", expression = solver?.expression ?: "—")
                    if (submitted != null && submitted.isValid) {
                        SolutionRow(label = "Tvoj izraz", expression = submitted.expression)
                        Text(
                            "Razlika od cilja: ${submitted.distance}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = cs.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 14) {
                Text(
                    "Odigrano ${state.stats.totalGames} • Tačnih ${state.stats.exactSolutions} • " +
                        "Prosečna razlika ${"%.1f".format(Locale.US, state.stats.avgDistance)}",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = cs.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(24.dp))
            PrimaryButton(
                text = "NOVA IGRA",
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Filled.PlayArrow
            )
            Spacer(Modifier.height(12.dp))
            OutlineButton(
                text = "PODELI",
                onClick = {
                    val text = "Moj Broj — cilj ${round.target}, moj rezultat ${resultValue ?: "-"}. Probaj i ti!"
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    context.startActivity(Intent.createChooser(intent, "Podeli rezultat"))
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Filled.Share
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SolutionRow(label: String, expression: String) {
    val cs = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label.uppercase(), style = MojBrojType.labelCaps, color = cs.onSurfaceVariant.copy(alpha = 0.7f))
        Text(styledExpression(expression), style = MojBrojType.operatorTile.copy(fontSize = 22.sp))
    }
}

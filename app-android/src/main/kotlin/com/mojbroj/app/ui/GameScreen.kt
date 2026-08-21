package com.mojbroj.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mojbroj.app.ui.theme.MojBrojType
import com.mojbroj.core.DifficultyMode

@Composable
internal fun GameScreen(
    state: UiState,
    onToken: (String) -> Unit,
    onBackspace: () -> Unit,
    onSubmit: () -> Unit,
    onExitGame: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val round = state.currentRound ?: return
    val danger = state.timerSec < 10

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            timerSec = state.timerSec,
            timerDanger = danger,
            title = "",
            leading = {
                IconCircleButton(Icons.Filled.Close, "Izađi iz partije", onExitGame)
            },
            trailing = {
                Pill(
                    text = if (state.difficultyMode == DifficultyMode.KIDS) "Dečiji" else "Standard",
                    leadingIcon = Icons.Filled.Star,
                    iconTint = cs.tertiary
                )
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            // Target panel
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 18) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("TRAŽENI BROJ", style = MojBrojType.labelCaps, color = cs.onSurfaceVariant.copy(alpha = 0.7f))
                    Spacer(Modifier.height(4.dp))
                    Text("${round.target}", style = MojBrojType.displayTarget, color = cs.tertiary)
                }
            }

            // Equation display
            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 14) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (state.expression.isEmpty()) {
                        Text("Sastavi izraz…", style = MojBrojType.operatorTile, color = cs.onSurfaceVariant.copy(alpha = 0.5f))
                    } else {
                        Text(styledExpression(state.expression), style = MojBrojType.operatorTile)
                    }
                }
            }

            state.validationError?.let {
                Text(it, color = cs.error, style = MaterialTheme.typography.bodyMedium)
            }

            // Number board
            NumberBoard(numbers = round.numbers, onToken = onToken)
            Spacer(Modifier.height(4.dp))
        }

        // Operator + submit panel (fixed bottom, glass)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color.White.copy(alpha = 0.04f))
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.08f),
                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OperatorTile("+", "+", onToken, Modifier.weight(1f))
                OperatorTile("−", "-", onToken, Modifier.weight(1f))
                OperatorTile("×", "*", onToken, Modifier.weight(1f))
                OperatorTile("÷", "/", onToken, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OperatorTile("(", "(", onToken, Modifier.weight(1f), neutral = true)
                OperatorTile(")", ")", onToken, Modifier.weight(1f), neutral = true)
                ClearTile(onBackspace, Modifier.weight(2f))
            }
            PrimaryButton(
                text = "POTVRDI",
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                container = cs.tertiaryContainer,
                content = cs.onTertiaryContainer
            )
        }
    }
}

@Composable
private fun NumberBoard(numbers: List<Int>, onToken: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        numbers.chunked(3).forEach { rowNumbers ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowNumbers.forEach { number ->
                    val big = number >= 25
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (big) cs.primaryContainer else cs.surfaceContainerHigh)
                            .then(
                                if (big) Modifier.border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(16.dp))
                                else Modifier
                            )
                            .clickable { onToken(number.toString()) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            number.toString(),
                            style = MojBrojType.numberTile,
                            color = if (big) cs.onPrimaryContainer else cs.onSurface
                        )
                    }
                }
                // pad incomplete row to keep tile sizing consistent
                repeat(3 - rowNumbers.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun RowScope.OperatorTile(
    label: String,
    token: String,
    onToken: (String) -> Unit,
    modifier: Modifier = Modifier,
    neutral: Boolean = false
) {
    val cs = MaterialTheme.colorScheme
    val container = if (neutral) cs.surfaceContainerHighest else cs.secondaryContainer
    val contentColor = if (neutral) cs.onSurfaceVariant else cs.onSecondaryContainer
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(container)
            .clickable { onToken(token) },
        contentAlignment = Alignment.Center
    ) {
        Text(label, style = MojBrojType.operatorTile, color = contentColor)
    }
}

@Composable
private fun RowScope.ClearTile(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(cs.errorContainer.copy(alpha = 0.20f))
            .border(1.dp, cs.errorContainer.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("OBRIŠI", style = MojBrojType.labelCaps, color = cs.error)
    }
}

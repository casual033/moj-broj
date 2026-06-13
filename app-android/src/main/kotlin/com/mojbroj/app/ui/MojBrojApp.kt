package com.mojbroj.app.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HighlightOff
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mojbroj.app.ui.theme.MojBrojType
import com.mojbroj.app.ui.theme.SpaceGrotesk
import com.mojbroj.core.DifficultyMode
import com.mojbroj.core.model.EvaluationStatus
import java.util.Locale

private fun formatTime(totalSec: Int): String {
    val s = totalSec.coerceAtLeast(0)
    return String.format(Locale.US, "%02d:%02d", s / 60, s % 60)
}

@Composable
fun MojBrojApp(viewModel: MojBrojViewModel) {
    val state by viewModel.uiState.collectAsState()

    AmbientBackground {
        when (state.screen) {
            AppScreen.HOME -> HomeScreen(
                state = state,
                onStartGame = viewModel::startGame,
                onRules = viewModel::openRules,
                onSettings = viewModel::openSettings
            )
            AppScreen.RULES -> RulesScreen(
                onBack = viewModel::openHome,
                onStart = { viewModel.startGame(state.difficultyMode) },
                onSettings = viewModel::openSettings
            )
            AppScreen.SETTINGS -> SettingsScreen(
                roundDurationSec = state.roundDurationSec,
                onRoundDurationChange = viewModel::updateRoundDuration,
                difficultyMode = state.difficultyMode,
                onDifficultyChange = viewModel::updateDifficultyMode,
                stats = state.stats,
                onBack = viewModel::openHome,
                onRules = viewModel::openRules
            )
            AppScreen.GAME -> GameScreen(
                state = state,
                onToken = viewModel::appendToken,
                onBackspace = viewModel::removeLastToken,
                onSubmit = viewModel::submit,
                onExitGame = viewModel::exitCurrentGame
            )
            AppScreen.RESULT -> ResultScreen(state = state, onPlayAgain = viewModel::playAgain)
        }
    }
}

/* ------------------------------------------------------------------ */
/* Shared building blocks                                             */
/* ------------------------------------------------------------------ */

@Composable
private fun AmbientBackground(content: @Composable BoxScope.() -> Unit) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
    ) {
        // primary glow (top)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(cs.primary.copy(alpha = 0.16f), Color.Transparent),
                        radius = 760f
                    )
                )
        )
        // tertiary glow (bottom-right)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(cs.tertiary.copy(alpha = 0.08f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(900f, 2200f),
                        radius = 620f
                    )
                )
        )
        content()
    }
}

typealias BoxScope = androidx.compose.foundation.layout.BoxScope

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 18,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(cornerRadius.dp)),
        content = content
    )
}

@Composable
private fun Pill(
    text: String,
    leadingIcon: ImageVector? = null,
    iconTint: Color = MaterialTheme.colorScheme.tertiary,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
        }
        Text(text, style = MojBrojType.labelCaps, color = textColor)
    }
}

@Composable
private fun TopBar(
    timerSec: Int? = null,
    timerDanger: Boolean = false,
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.tertiary,
    titleAtStart: Boolean = false,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .heightIn(min = 44.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leading?.invoke()
            if (timerSec != null) {
                val tint = if (timerDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                Icon(Icons.Filled.Timer, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
                Text(formatTime(timerSec), style = MojBrojType.headlineLgMobile, color = tint)
            }
            if (titleAtStart && title.isNotBlank()) {
                Text(title, style = MojBrojType.headlineLgMobile, color = titleColor)
            }
        }
        if (!titleAtStart && title.isNotBlank()) {
            Text(
                title,
                style = MojBrojType.headlineLgMobile,
                color = titleColor,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            trailing?.invoke()
        }
    }
}

@Composable
private fun IconCircleButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun BottomNav(current: AppScreen, onPlay: () -> Unit, onRules: () -> Unit, onSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.95f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem("Igraj", Icons.Filled.SportsEsports, current == AppScreen.HOME, onPlay)
        NavItem("Pravila", Icons.AutoMirrored.Filled.MenuBook, current == AppScreen.RULES, onRules)
        NavItem("Podešavanja", Icons.Filled.Settings, current == AppScreen.SETTINGS, onSettings)
    }
}

@Composable
private fun NavItem(label: String, icon: ImageVector, active: Boolean, onClick: () -> Unit) {
    if (active) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable(onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
            Text(label, style = MojBrojType.labelCaps, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    } else {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            Text(label, style = MojBrojType.labelCaps, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    container: Color = MaterialTheme.colorScheme.primary,
    content: Color = MaterialTheme.colorScheme.onPrimary
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(container)
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, tint = content, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MojBrojType.headlineLgMobile.copy(fontSize = 20.sp), color = content)
    }
}

@Composable
private fun OutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, color, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.titleMedium, color = color)
    }
}

/* ------------------------------------------------------------------ */
/* HOME                                                               */
/* ------------------------------------------------------------------ */

@Composable
private fun HomeScreen(
    state: UiState,
    onStartGame: (DifficultyMode) -> Unit,
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

/* ------------------------------------------------------------------ */
/* GAME                                                               */
/* ------------------------------------------------------------------ */

@Composable
private fun GameScreen(
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

@Composable
private fun styledExpression(expr: String): AnnotatedString {
    val cs = MaterialTheme.colorScheme
    return buildAnnotatedString {
        expr.forEach { ch ->
            when (ch) {
                '+', '-', '*', '/' -> {
                    val shown = when (ch) {
                        '*' -> "×"
                        '/' -> "÷"
                        '-' -> "−"
                        else -> ch.toString()
                    }
                    withStyle(SpanStyle(color = cs.primary)) { append(shown) }
                }
                '(', ')' -> withStyle(SpanStyle(color = cs.onSurfaceVariant)) { append(ch) }
                else -> withStyle(SpanStyle(color = cs.onSurface)) { append(ch) }
            }
        }
    }
}

/* ------------------------------------------------------------------ */
/* RESULT                                                             */
/* ------------------------------------------------------------------ */

@Composable
private fun ResultScreen(state: UiState, onPlayAgain: () -> Unit) {
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

/* ------------------------------------------------------------------ */
/* RULES                                                              */
/* ------------------------------------------------------------------ */

@Composable
private fun RulesScreen(onBack: () -> Unit, onStart: () -> Unit, onSettings: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            title = "Pravila",
            leading = { IconCircleButton(Icons.AutoMirrored.Filled.ArrowBack, "Nazad", onBack) }
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Kako se igra?", style = MojBrojType.headlineLg, color = cs.onBackground)
            Text(
                "Postani majstor matematike u par koraka.",
                style = MojBrojType.bodyMd,
                color = cs.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))

            RuleCard(
                icon = Icons.Filled.Calculate,
                accent = cs.primary,
                title = "CILJ I BROJEVI",
                body = "Dobićeš ciljni broj i 6 ponuđenih brojeva (u dečijem modu 5)."
            )
            RuleCard(
                icon = Icons.Filled.Calculate,
                accent = cs.secondary,
                title = "OPERACIJE",
                body = "Koristi +, −, ×, ÷ i zagrade da sastaviš izraz."
            )
            RuleCard(
                icon = Icons.Filled.Lock,
                accent = cs.tertiary,
                title = "OGRANIČENJE",
                body = "Svaki broj možeš iskoristiti najviše jednom. Deljenje mora biti celobrojno, bez negativnih međurezultata."
            )
            RuleCard(
                icon = Icons.Filled.Timer,
                accent = cs.error,
                title = "VREMENSKI LIMIT",
                body = "Dođi što bliže traženom broju pre nego što istekne vreme."
            )

            Spacer(Modifier.height(8.dp))
            PrimaryButton(
                text = "Započni igru",
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Filled.PlayArrow,
                container = cs.primaryContainer,
                content = cs.onPrimaryContainer
            )
            Spacer(Modifier.height(12.dp))
        }
        BottomNav(AppScreen.RULES, onPlay = onBack, onRules = {}, onSettings = onSettings)
    }
}

@Composable
private fun RuleCard(icon: ImageVector, accent: Color, title: String, body: String) {
    val cs = MaterialTheme.colorScheme
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(26.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MojBrojType.labelCaps, color = accent)
                Text(body, style = MaterialTheme.typography.bodyMedium, color = cs.onSurface)
            }
        }
    }
}

/* ------------------------------------------------------------------ */
/* SETTINGS                                                           */
/* ------------------------------------------------------------------ */

@Composable
private fun SettingsScreen(
    roundDurationSec: Int,
    onRoundDurationChange: (Int) -> Unit,
    difficultyMode: DifficultyMode,
    onDifficultyChange: (DifficultyMode) -> Unit,
    stats: com.mojbroj.app.data.PlayerStats,
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
                "Moj Broj • verzija ${com.mojbroj.app.BuildConfig.VERSION_NAME}",
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

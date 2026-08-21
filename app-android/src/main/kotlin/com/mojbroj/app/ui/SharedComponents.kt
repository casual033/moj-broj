package com.mojbroj.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mojbroj.app.ui.theme.MojBrojType
import java.util.Locale

internal fun formatTime(totalSec: Int): String {
    val s = totalSec.coerceAtLeast(0)
    return String.format(Locale.US, "%02d:%02d", s / 60, s % 60)
}

@Composable
internal fun AmbientBackground(content: @Composable BoxScope.() -> Unit) {
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

@Composable
internal fun GlassCard(
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
internal fun Pill(
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
internal fun TopBar(
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
internal fun IconCircleButton(
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
internal fun BottomNav(current: AppScreen, onPlay: () -> Unit, onRules: () -> Unit, onSettings: () -> Unit) {
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
internal fun PrimaryButton(
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
internal fun OutlineButton(
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

@Composable
internal fun styledExpression(expr: String): AnnotatedString {
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
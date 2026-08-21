package com.mojbroj.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mojbroj.app.ui.theme.MojBrojType

@Composable
internal fun RulesScreen(onBack: () -> Unit, onStart: () -> Unit, onSettings: () -> Unit) {
    val cs = MaterialTheme.colorScheme

    BackHandler(onBack = onBack)

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

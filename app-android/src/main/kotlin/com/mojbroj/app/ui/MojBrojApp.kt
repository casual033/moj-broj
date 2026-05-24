package com.mojbroj.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mojbroj.core.DifficultyMode
import com.mojbroj.core.model.EvaluationStatus
import java.util.Locale

@Composable
fun MojBrojApp(viewModel: MojBrojViewModel) {
    val state by viewModel.uiState.collectAsState()

    when (state.screen) {
        AppScreen.HOME -> HomeScreen(
            onNewGame = viewModel::startNewGame,
            onRules = viewModel::openRules,
            onSettings = viewModel::openSettings,
            statsLine = "Partije: ${state.stats.totalGames} | Tačnih: ${state.stats.exactSolutions}",
            difficultyMode = state.difficultyMode,
            onDifficultyChange = viewModel::updateDifficultyMode
        )
        AppScreen.RULES -> RulesScreen(onBack = viewModel::openHome)
        AppScreen.SETTINGS -> SettingsScreen(
            roundDurationSec = state.roundDurationSec,
            onRoundDurationChange = viewModel::updateRoundDuration,
            difficultyMode = state.difficultyMode,
            onDifficultyChange = viewModel::updateDifficultyMode,
            onBack = viewModel::openHome
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

@Composable
private fun HomeScreen(
    onNewGame: () -> Unit,
    onRules: () -> Unit,
    onSettings: () -> Unit,
    statsLine: String,
    difficultyMode: DifficultyMode,
    onDifficultyChange: (DifficultyMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Moj Broj", style = MaterialTheme.typography.headlineMedium)
        Text(statsLine, style = MaterialTheme.typography.bodyMedium)
        Text("Režim igre", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DifficultyButton(
                label = "Standard",
                selected = difficultyMode == DifficultyMode.STANDARD,
                onClick = { onDifficultyChange(DifficultyMode.STANDARD) }
            )
            DifficultyButton(
                label = "Za decu",
                selected = difficultyMode == DifficultyMode.KIDS,
                onClick = { onDifficultyChange(DifficultyMode.KIDS) }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNewGame, modifier = Modifier.fillMaxWidth()) { Text("Nova igra") }
        OutlinedButton(onClick = onRules, modifier = Modifier.fillMaxWidth()) { Text("Pravila") }
        OutlinedButton(onClick = onSettings, modifier = Modifier.fillMaxWidth()) { Text("Podešavanja") }
    }
}

@Composable
private fun RulesScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Pravila", style = MaterialTheme.typography.headlineMedium)
        Text("Cilj je da od ponuđenih 6 brojeva dobiješ ciljani broj.")
        Text("Set brojeva: 4 mala (1-9), 1 srednji (10/15/20), 1 veliki (25/50/75/100).")
        Text("Dozvoljeno: +, -, *, / i zagrade.")
        Text("Svaki broj koristiš najviše jednom.")
        Text("Deljenje mora biti celobrojno, a međurezultati ne-negativni.")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBack) { Text("Nazad") }
    }
}

@Composable
private fun SettingsScreen(
    roundDurationSec: Int,
    onRoundDurationChange: (Int) -> Unit,
    difficultyMode: DifficultyMode,
    onDifficultyChange: (DifficultyMode) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Podešavanja", style = MaterialTheme.typography.headlineMedium)
        Text("Težina", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DifficultyButton(
                label = "Standard",
                selected = difficultyMode == DifficultyMode.STANDARD,
                onClick = { onDifficultyChange(DifficultyMode.STANDARD) }
            )
            DifficultyButton(
                label = "Za decu",
                selected = difficultyMode == DifficultyMode.KIDS,
                onClick = { onDifficultyChange(DifficultyMode.KIDS) }
            )
        }
        Text("Trajanje partije: ${roundDurationSec}s")
        Slider(
            value = roundDurationSec.toFloat(),
            onValueChange = { onRoundDurationChange(it.toInt()) },
            valueRange = 75f..180f,
            steps = 6
        )
        val modeRulesText = if (difficultyMode == DifficultyMode.STANDARD) {
            "Standard: 4 mala (1-9), 1 srednji (10/15/20), 1 veliki (25/50/75/100), cilj 100-999."
        } else {
            "Za decu: 3 mala (1-9) + 1 srednji (10/15/20), cilj do 100."
        }
        Text(modeRulesText)
        Button(onClick = onBack) { Text("Sačuvaj i nazad") }
    }
}

@Composable
private fun RowScope.DifficultyButton(label: String, selected: Boolean, onClick: () -> Unit) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = containerColor)
    ) {
        Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
private fun GameScreen(
    state: UiState,
    onToken: (String) -> Unit,
    onBackspace: () -> Unit,
    onSubmit: () -> Unit,
    onExitGame: () -> Unit
) {
    val round = state.currentRound ?: return
    val operatorButtons = listOf("+", "-", "*", "/", "(", ")")
    val numbersColumns = if (round.numbers.size <= 4) 2 else 3
    val numbersGridHeight = if (round.numbers.size <= 4) 130.dp else 170.dp
    val numbersButtonAspect = if (round.numbers.size <= 4) 3.0f else 1.9f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            OutlinedButton(onClick = onExitGame) { Text("Izađi iz partije") }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Cilj: ${round.target}", style = MaterialTheme.typography.headlineSmall)
                Text("Preostalo vreme: ${state.timerSec}s", style = MaterialTheme.typography.titleMedium)
                Text(
                    if (state.difficultyMode == DifficultyMode.KIDS) "Mod: Za decu" else "Mod: Standard",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "Brojevi: ${round.numbers.joinToString("  ")}",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors()) {
            Text(
                text = if (state.expression.isEmpty()) "Unesi izraz..." else state.expression,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
        state.validationError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Text("Brojevi", style = MaterialTheme.typography.titleMedium)
        LazyVerticalGrid(
            columns = GridCells.Fixed(numbersColumns),
            modifier = Modifier.height(numbersGridHeight),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(round.numbers) { number ->
                Button(
                    onClick = { onToken(number.toString()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(numbersButtonAspect),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(number.toString())
                }
            }
        }

        Text("Operatori", style = MaterialTheme.typography.titleMedium)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(150.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(operatorButtons) { op ->
                OutlinedButton(
                    onClick = { onToken(op) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2.2f),
                    shape = RoundedCornerShape(10.dp)
                ) { Text(op) }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onBackspace, modifier = Modifier.weight(1f)) { Text("Obriši") }
            Button(onClick = onSubmit, modifier = Modifier.weight(1f)) { Text("Proveri") }
        }
    }
}

@Composable
private fun ResultScreen(state: UiState, onPlayAgain: () -> Unit) {
    val round = state.currentRound ?: return
    val submitted = state.submitted
    val solver = state.solverResult
    val statusText = when (submitted?.status) {
        EvaluationStatus.EXACT -> "Tačno"
        EvaluationStatus.CLOSEST -> "Najbliže"
        EvaluationStatus.INVALID, null -> "Nevalidan unos"
    }
    val statusColor = when (submitted?.status) {
        EvaluationStatus.EXACT -> Color(0xFF2E7D32)
        EvaluationStatus.CLOSEST -> Color(0xFFEF6C00)
        EvaluationStatus.INVALID, null -> Color(0xFFC62828)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Rezultat", style = MaterialTheme.typography.headlineMedium)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Cilj: ${round.target}", style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Status:", style = MaterialTheme.typography.titleMedium)
                    Text(
                        statusText,
                        style = MaterialTheme.typography.titleMedium,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        ResultInfoCard(
            title = "Tvoje rešenje",
            expression = submitted?.expression ?: "-",
            result = submitted?.result?.toString() ?: "-",
            distance = if (submitted != null && submitted.distance != Int.MAX_VALUE) submitted.distance.toString() else "-"
        )
        ResultInfoCard(
            title = "Najbolje moguće",
            expression = solver?.expression ?: "-",
            result = solver?.result?.toString() ?: "-",
            distance = solver?.distance?.toString() ?: "-"
        )

        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) { Text(
            "Statistika: Partije ${state.stats.totalGames}, Tačnih ${state.stats.exactSolutions}, " +
                "Prosečna razlika ${"%.2f".format(Locale.US, state.stats.avgDistance)}",
            modifier = Modifier.padding(12.dp)
        ) }
        Button(onClick = onPlayAgain, modifier = Modifier.fillMaxWidth()) { Text("Nova igra") }
    }
}

@Composable
private fun ResultInfoCard(
    title: String,
    expression: String,
    result: String,
    distance: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("Izraz: $expression")
            Text("Rezultat: $result")
            Text("Razlika: $distance")
        }
    }
}

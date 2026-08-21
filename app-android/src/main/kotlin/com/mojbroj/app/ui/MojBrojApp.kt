package com.mojbroj.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

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

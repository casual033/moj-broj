package com.mojbroj.app.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mojbroj.app.data.PlayerStats
import com.mojbroj.core.DifficultyMode
import com.mojbroj.app.data.StatsRepository
import com.mojbroj.core.GameRoundGenerator
import com.mojbroj.core.SolutionEvaluator
import com.mojbroj.core.Solver
import com.mojbroj.core.model.EvaluationStatus
import com.mojbroj.core.model.GameRound
import com.mojbroj.core.model.SolverResult
import com.mojbroj.core.model.SubmittedSolution
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    RULES,
    SETTINGS,
    GAME,
    RESULT
}

enum class RoundPhase {
    IDLE,
    IN_PROGRESS,
    SUBMITTED,
    TIME_UP,
    EVALUATED,
    RESULT_SHOWN
}

data class UiState(
    val screen: AppScreen = AppScreen.HOME,
    val phase: RoundPhase = RoundPhase.IDLE,
    val currentRound: GameRound? = null,
    val expression: String = "",
    val roundDurationSec: Int = 90,
    val timerSec: Int = 90,
    val difficultyMode: DifficultyMode = DifficultyMode.STANDARD,
    val submitted: SubmittedSolution? = null,
    val solverResult: SolverResult? = null,
    val stats: PlayerStats = PlayerStats(),
    val validationError: String? = null
)

class MojBrojViewModel(
    private val statsRepository: StatsRepository,
    private val roundGenerator: GameRoundGenerator = GameRoundGenerator(),
    private val solutionEvaluator: SolutionEvaluator = SolutionEvaluator(),
    private val solver: Solver = Solver(maxDurationMs = 1800L)
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            statsRepository.stats.collect { stats ->
                _uiState.update { it.copy(stats = stats) }
            }
        }
    }

    fun openHome() {
        _uiState.update { it.copy(screen = AppScreen.HOME) }
    }

    fun openRules() {
        _uiState.update { it.copy(screen = AppScreen.RULES) }
    }

    fun openSettings() {
        _uiState.update { it.copy(screen = AppScreen.SETTINGS) }
    }

    fun updateRoundDuration(value: Int) {
        val normalized = value.coerceIn(30, 300)
        _uiState.update { it.copy(roundDurationSec = normalized, timerSec = normalized) }
    }

    fun updateDifficultyMode(mode: DifficultyMode) {
        _uiState.update { it.copy(difficultyMode = mode) }
    }

    fun startGame(mode: DifficultyMode) {
        _uiState.update { it.copy(difficultyMode = mode) }
        startNewGame()
    }

    fun startNewGame() {
        timerJob?.cancel()
        val state = _uiState.value
        val round = roundGenerator.createRound(state.difficultyMode)
        _uiState.update {
            it.copy(
                screen = AppScreen.GAME,
                phase = RoundPhase.IN_PROGRESS,
                currentRound = round,
                expression = "",
                timerSec = state.roundDurationSec,
                submitted = null,
                solverResult = null,
                validationError = null
            )
        }
        startTimer()
    }

    fun appendToken(token: String) {
        if (_uiState.value.phase != RoundPhase.IN_PROGRESS) return
        _uiState.update { it.copy(expression = it.expression + token, validationError = null) }
    }

    fun removeLastToken() {
        if (_uiState.value.phase != RoundPhase.IN_PROGRESS) return
        _uiState.update { current ->
            val next = if (current.expression.isNotEmpty()) current.expression.dropLast(1) else ""
            current.copy(expression = next, validationError = null)
        }
    }

    fun submit() {
        val state = _uiState.value
        if (state.phase != RoundPhase.IN_PROGRESS) return
        evaluateRound(state.timerSec, RoundPhase.SUBMITTED)
    }

    fun playAgain() {
        _uiState.update { it.copy(screen = AppScreen.HOME, phase = RoundPhase.IDLE) }
    }

    fun exitCurrentGame() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                screen = AppScreen.HOME,
                phase = RoundPhase.IDLE,
                currentRound = null,
                expression = "",
                timerSec = it.roundDurationSec,
                submitted = null,
                solverResult = null,
                validationError = null
            )
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_uiState.value.timerSec > 0 && _uiState.value.phase == RoundPhase.IN_PROGRESS) {
                delay(1000)
                _uiState.update { it.copy(timerSec = it.timerSec - 1) }
            }
            if (_uiState.value.phase == RoundPhase.IN_PROGRESS && _uiState.value.timerSec <= 0) {
                evaluateRound(0, RoundPhase.TIME_UP)
            }
        }
    }

    private fun evaluateRound(timeLeftSec: Int, submitPhase: RoundPhase) {
        timerJob?.cancel()
        val state = _uiState.value
        val round = state.currentRound ?: return

        _uiState.update { it.copy(phase = submitPhase) }

        val submitted = solutionEvaluator.evaluate(round, state.expression)
        val solverResult = solver.solve(round)

        val effectiveDistance = if (submitted.isValid) submitted.distance else solverResult.distance
        val solvedInSec = state.roundDurationSec - timeLeftSec
        viewModelScope.launch {
            statsRepository.recordGame(
                distance = effectiveDistance,
                isExact = submitted.status == EvaluationStatus.EXACT,
                solveTimeSec = solvedInSec
            )
        }

        _uiState.update {
            it.copy(
                phase = RoundPhase.RESULT_SHOWN,
                screen = AppScreen.RESULT,
                submitted = submitted,
                solverResult = solverResult,
                validationError = if (!submitted.isValid) "Nevalidan izraz ili nedozvoljeni brojevi." else null
            )
        }
    }
}

class MojBrojViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MojBrojViewModel(
            statsRepository = StatsRepository(context)
        ) as T
    }
}

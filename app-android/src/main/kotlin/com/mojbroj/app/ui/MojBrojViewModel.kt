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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    RESULT_SHOWN
}

data class UiState(
    val screen: AppScreen = AppScreen.HOME,
    val phase: RoundPhase = RoundPhase.IDLE,
    val currentRound: GameRound? = null,
    val expressionTokens: List<String> = emptyList(),
    val roundDurationSec: Int = 90,
    val timerSec: Int = 90,
    val elapsedSec: Int = 0,
    val difficultyMode: DifficultyMode = DifficultyMode.STANDARD,
    val submitted: SubmittedSolution? = null,
    val solverResult: SolverResult? = null,
    val stats: PlayerStats = PlayerStats(),
    val validationError: String? = null
) {
    val expression: String get() = expressionTokens.joinToString("")

    /** Kids mode has no countdown; the round ends only on submit. */
    val isTimedRound: Boolean get() = difficultyMode != DifficultyMode.KIDS

    /** True while the solver is computing the best solution off the main thread. */
    val isEvaluating: Boolean get() = phase == RoundPhase.SUBMITTED || phase == RoundPhase.TIME_UP

    /** Numbers already consumed by the current expression. */
    val usedNumbers: List<Int> get() = expressionTokens.mapNotNull { it.toIntOrNull() }
}

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
                expressionTokens = emptyList(),
                timerSec = state.roundDurationSec,
                elapsedSec = 0,
                submitted = null,
                solverResult = null,
                validationError = null
            )
        }
        startTimer()
    }

    fun appendToken(token: String) {
        val state = _uiState.value
        if (state.phase != RoundPhase.IN_PROGRESS) return
        val number = token.toIntOrNull()
        if (number != null) {
            val round = state.currentRound ?: return
            val available = round.numbers.count { it == number }
            val used = state.usedNumbers.count { it == number }
            if (used >= available) return
        }
        _uiState.update { it.copy(expressionTokens = it.expressionTokens + token, validationError = null) }
    }

    fun removeLastToken() {
        if (_uiState.value.phase != RoundPhase.IN_PROGRESS) return
        _uiState.update { current ->
            current.copy(expressionTokens = current.expressionTokens.dropLast(1), validationError = null)
        }
    }

    fun submit() {
        val state = _uiState.value
        if (state.phase != RoundPhase.IN_PROGRESS) return
        evaluateRound(RoundPhase.SUBMITTED)
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
                expressionTokens = emptyList(),
                timerSec = it.roundDurationSec,
                elapsedSec = 0,
                submitted = null,
                solverResult = null,
                validationError = null
            )
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_uiState.value.phase == RoundPhase.IN_PROGRESS) {
                delay(1000)
                if (_uiState.value.phase != RoundPhase.IN_PROGRESS) break
                _uiState.update { state ->
                    if (state.isTimedRound) {
                        state.copy(timerSec = state.timerSec - 1, elapsedSec = state.elapsedSec + 1)
                    } else {
                        state.copy(elapsedSec = state.elapsedSec + 1)
                    }
                }
                val state = _uiState.value
                if (state.isTimedRound && state.timerSec <= 0 && state.phase == RoundPhase.IN_PROGRESS) {
                    evaluateRound(RoundPhase.TIME_UP)
                    break
                }
            }
        }
    }

    private fun evaluateRound(submitPhase: RoundPhase) {
        timerJob?.cancel()
        val state = _uiState.value
        val round = state.currentRound ?: return
        val expression = state.expression

        _uiState.update { it.copy(phase = submitPhase) }

        viewModelScope.launch {
            val (submitted, solverResult) = withContext(Dispatchers.Default) {
                val submitted = solutionEvaluator.evaluate(round, expression)
                val solverResult = solver.solve(round)
                submitted to solverResult
            }

            val isExact = submitted.status == EvaluationStatus.EXACT
            statsRepository.recordGame(
                distance = if (submitted.isValid) submitted.distance else 0,
                isExact = isExact,
                isValid = submitted.isValid,
                solveTimeSec = _uiState.value.elapsedSec
            )

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
}

class MojBrojViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MojBrojViewModel(
            statsRepository = StatsRepository(context)
        ) as T
    }
}

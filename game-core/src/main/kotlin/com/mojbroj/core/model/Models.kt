package com.mojbroj.core.model

data class GameRound(
    val target: Int,
    val numbers: List<Int>
)

enum class EvaluationStatus {
    EXACT,
    CLOSEST,
    INVALID
}

data class SubmittedSolution(
    val expression: String,
    val result: Int?,
    val isValid: Boolean,
    val distance: Int,
    val status: EvaluationStatus
)

data class SolverResult(
    val expression: String,
    val result: Int,
    val distance: Int
)

data class EvaluatedExpression(
    val expression: String,
    val result: Int,
    val tokens: List<String>
)

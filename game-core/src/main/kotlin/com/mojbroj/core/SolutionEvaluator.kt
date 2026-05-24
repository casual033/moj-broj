package com.mojbroj.core

import com.mojbroj.core.model.EvaluationStatus
import com.mojbroj.core.model.GameRound
import com.mojbroj.core.model.SubmittedSolution

class SolutionEvaluator(
    private val expressionEvaluator: ExpressionEvaluator = ExpressionEvaluator(),
    private val expressionValidator: ExpressionValidator = ExpressionValidator(),
    private val scoringService: ScoringService = ScoringService()
) {
    fun evaluate(round: GameRound, expression: String): SubmittedSolution {
        val evaluated = expressionEvaluator.evaluate(expression).getOrNull()
        if (evaluated == null) {
            return SubmittedSolution(
                expression = expression,
                result = null,
                isValid = false,
                distance = Int.MAX_VALUE,
                status = EvaluationStatus.INVALID
            )
        }

        val isValid = expressionValidator.validateUsedNumbers(evaluated.tokens, round.numbers).isSuccess
        val distance = if (isValid) scoringService.distance(round.target, evaluated.result) else Int.MAX_VALUE
        val status = scoringService.status(round.target, evaluated.result, isValid)

        return SubmittedSolution(
            expression = expression,
            result = evaluated.result,
            isValid = isValid,
            distance = distance,
            status = status
        )
    }
}

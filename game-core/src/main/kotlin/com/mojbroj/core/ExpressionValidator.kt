package com.mojbroj.core

class ExpressionValidator {
    fun validateUsedNumbers(tokens: List<String>, availableNumbers: List<Int>): Result<Unit> {
        return runCatching {
            val numbersInExpression = tokens.mapNotNull { it.toIntOrNull() }
            val availableCount = availableNumbers.groupingBy { it }.eachCount().toMutableMap()

            numbersInExpression.forEach { n ->
                val current = availableCount[n] ?: 0
                require(current > 0) { "Number $n not available or already used" }
                availableCount[n] = current - 1
            }
        }
    }
}

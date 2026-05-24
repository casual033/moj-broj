package com.mojbroj.core

import org.junit.Assert.assertTrue
import org.junit.Test

class ExpressionValidatorTest {
    private val validator = ExpressionValidator()

    @Test
    fun `validator accepts allowed numbers used once`() {
        val result = validator.validateUsedNumbers(
            tokens = listOf("(", "25", "+", "75", ")", "*", "8"),
            availableNumbers = listOf(25, 75, 8, 1, 2, 3)
        )
        assertTrue(result.isSuccess)
    }

    @Test
    fun `validator rejects duplicate number usage`() {
        val result = validator.validateUsedNumbers(
            tokens = listOf("25", "+", "25"),
            availableNumbers = listOf(25, 75, 8, 1, 2, 3)
        )
        assertTrue(result.isFailure)
    }
}

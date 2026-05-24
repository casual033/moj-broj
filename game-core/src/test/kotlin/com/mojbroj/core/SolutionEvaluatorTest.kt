package com.mojbroj.core

import com.mojbroj.core.model.EvaluationStatus
import com.mojbroj.core.model.GameRound
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SolutionEvaluatorTest {
    private val solutionEvaluator = SolutionEvaluator()

    @Test
    fun `evaluate returns exact when target is reached`() {
        val round = GameRound(target = 800, numbers = listOf(25, 75, 8, 1, 2, 3))
        val result = solutionEvaluator.evaluate(round, "(25+75)*8")

        assertTrue(result.isValid)
        assertEquals(EvaluationStatus.EXACT, result.status)
        assertEquals(0, result.distance)
        assertEquals(800, result.result)
    }

    @Test
    fun `evaluate returns invalid for disallowed expression`() {
        val round = GameRound(target = 800, numbers = listOf(25, 75, 8, 1, 2, 3))
        val result = solutionEvaluator.evaluate(round, "(25+75)*99")

        assertFalse(result.isValid)
        assertEquals(EvaluationStatus.INVALID, result.status)
    }
}

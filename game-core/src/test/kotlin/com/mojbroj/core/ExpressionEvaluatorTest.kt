package com.mojbroj.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpressionEvaluatorTest {
    private val evaluator = ExpressionEvaluator()

    @Test
    fun `evaluate handles precedence and parentheses`() {
        val result = evaluator.evaluate("(25+75)*8")
        assertTrue(result.isSuccess)
        assertEquals(800, result.getOrThrow().result)
    }

    @Test
    fun `evaluate rejects non-integer division`() {
        val result = evaluator.evaluate("5/2")
        assertTrue(result.isFailure)
    }

    @Test
    fun `evaluate rejects negative intermediate result`() {
        val result = evaluator.evaluate("3-5")
        assertTrue(result.isFailure)
    }
}

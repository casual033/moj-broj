package com.mojbroj.core

import com.mojbroj.core.model.GameRound
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SolverTest {
    @Test
    fun `solver finds exact solution when one exists`() {
        val solver = Solver(maxDurationMs = 3000)
        val round = GameRound(target = 800, numbers = listOf(25, 75, 8, 1, 2, 3))

        val result = solver.solve(round)
        assertEquals(0, result.distance)
        assertEquals(800, result.result)
    }

    @Test
    fun `solver always returns some candidate`() {
        val solver = Solver(maxDurationMs = 1)
        val round = GameRound(target = 999, numbers = listOf(1, 1, 1, 1, 1, 1))

        val result = solver.solve(round)
        assertTrue(result.expression.isNotBlank())
    }
}

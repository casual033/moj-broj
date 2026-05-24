package com.mojbroj.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameRoundGeneratorTest {
    @Test
    fun `createRound standard produces valid size and target range`() {
        val generator = GameRoundGenerator(Random(123))
        val round = generator.createRound(DifficultyMode.STANDARD)

        assertEquals(6, round.numbers.size)
        assertTrue(round.target in 100..999)
    }

    @Test
    fun `createRound produces fixed small medium large composition`() {
        val generator = GameRoundGenerator(Random(456))
        val round = generator.createRound(DifficultyMode.STANDARD)

        val largeCount = round.numbers.count { it in listOf(25, 50, 75, 100) }
        val mediumCount = round.numbers.count { it in listOf(10, 15, 20) }
        val smallCount = round.numbers.count { it in 1..9 }
        assertEquals(1, largeCount)
        assertEquals(1, mediumCount)
        assertEquals(4, smallCount)
    }

    @Test
    fun `createRound kids mode produces easier set and target`() {
        val generator = GameRoundGenerator(Random(789))
        val round = generator.createRound(DifficultyMode.KIDS)

        val mediumCount = round.numbers.count { it in listOf(10, 15, 20) }
        val smallCount = round.numbers.count { it in 1..9 }
        val largeCount = round.numbers.count { it in listOf(25, 50, 75, 100) }

        assertEquals(4, round.numbers.size)
        assertEquals(1, mediumCount)
        assertEquals(3, smallCount)
        assertEquals(0, largeCount)
        assertTrue(round.target in 10..100)
    }
}

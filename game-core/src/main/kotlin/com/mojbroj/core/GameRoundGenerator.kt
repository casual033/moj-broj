package com.mojbroj.core

import com.mojbroj.core.model.GameRound
import kotlin.random.Random

private val LARGE_NUMBERS = listOf(25, 50, 75, 100)
private val MEDIUM_NUMBERS = listOf(10, 15, 20)
private val SMALL_NUMBERS = (1..9).toList()

enum class DifficultyMode {
    STANDARD,
    KIDS
}

class GameRoundGenerator(
    private val random: Random = Random.Default
) {
    fun createRound(mode: DifficultyMode = DifficultyMode.STANDARD): GameRound {
        val numbers = when (mode) {
            DifficultyMode.STANDARD -> {
                val large = listOf(LARGE_NUMBERS.random(random))
                val medium = listOf(MEDIUM_NUMBERS.random(random))
                val small = buildList {
                    repeat(4) { add(SMALL_NUMBERS.random(random)) }
                }
                (small + medium + large).shuffled(random)
            }
            DifficultyMode.KIDS -> {
                val medium = listOf(MEDIUM_NUMBERS.random(random))
                val small = buildList {
                    repeat(3) { add(SMALL_NUMBERS.random(random)) }
                }
                (small + medium).shuffled(random)
            }
        }

        val target = when (mode) {
            DifficultyMode.STANDARD -> random.nextInt(100, 1000)
            DifficultyMode.KIDS -> generateKidsTarget(numbers)
        }
        return GameRound(target = target, numbers = numbers)
    }

    private fun generateKidsTarget(numbers: List<Int>): Int {
        val count = random.nextInt(2, numbers.size + 1)
        return numbers.shuffled(random).take(count).sum()
    }
}

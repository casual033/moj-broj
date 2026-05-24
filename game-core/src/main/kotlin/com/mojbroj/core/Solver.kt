package com.mojbroj.core

import com.mojbroj.core.model.GameRound
import com.mojbroj.core.model.SolverResult
import kotlin.math.abs

class Solver(
    private val maxDurationMs: Long = 150L
) {
    private data class Node(val value: Int, val expression: String)

    fun solve(round: GameRound): SolverResult {
        val start = System.nanoTime()
        val deadline = start + maxDurationMs * 1_000_000

        var best = SolverResult(
            expression = round.numbers.first().toString(),
            result = round.numbers.first(),
            distance = abs(round.target - round.numbers.first())
        )

        fun updateBest(value: Int, expression: String) {
            val distance = abs(round.target - value)
            if (distance < best.distance) {
                best = SolverResult(expression = expression, result = value, distance = distance)
            }
        }

        fun search(nodes: List<Node>) {
            if (System.nanoTime() > deadline || best.distance == 0) return
            if (nodes.isEmpty()) return

            if (nodes.size == 1) {
                updateBest(nodes[0].value, nodes[0].expression)
                return
            }

            for (i in nodes.indices) {
                for (j in i + 1 until nodes.size) {
                    val a = nodes[i]
                    val b = nodes[j]

                    val rest = nodes.filterIndexed { idx, _ -> idx != i && idx != j }.toMutableList()
                    val candidates = buildCandidates(a, b)
                    for (candidate in candidates) {
                        updateBest(candidate.value, candidate.expression)
                        rest.add(candidate)
                        search(rest)
                        rest.removeLast()
                        if (System.nanoTime() > deadline || best.distance == 0) return
                    }
                }
            }
        }

        val initialNodes = round.numbers.map { Node(it, it.toString()) }
        search(initialNodes)
        return best
    }

    private fun buildCandidates(a: Node, b: Node): List<Node> {
        val out = mutableListOf<Node>()

        out += Node(a.value + b.value, "(${a.expression}+${b.expression})")
        out += Node(a.value * b.value, "(${a.expression}*${b.expression})")

        if (a.value >= b.value) {
            out += Node(a.value - b.value, "(${a.expression}-${b.expression})")
        }
        if (b.value >= a.value) {
            out += Node(b.value - a.value, "(${b.expression}-${a.expression})")
        }
        if (b.value != 0 && a.value % b.value == 0) {
            out += Node(a.value / b.value, "(${a.expression}/${b.expression})")
        }
        if (a.value != 0 && b.value % a.value == 0) {
            out += Node(b.value / a.value, "(${b.expression}/${a.expression})")
        }

        return out
    }
}

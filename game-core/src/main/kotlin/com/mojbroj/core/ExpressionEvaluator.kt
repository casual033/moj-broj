package com.mojbroj.core

import com.mojbroj.core.model.EvaluatedExpression

class ExpressionEvaluator {
    fun evaluate(expression: String): Result<EvaluatedExpression> {
        return runCatching {
            val tokens = tokenize(expression)
            val postfix = toPostfix(tokens)
            val result = evaluatePostfix(postfix)
            EvaluatedExpression(expression = expression, result = result, tokens = tokens)
        }
    }

    private fun tokenize(expression: String): List<String> {
        val cleaned = expression.replace("\\s+".toRegex(), "")
        require(cleaned.isNotEmpty()) { "Expression is empty" }

        val tokens = mutableListOf<String>()
        var idx = 0
        while (idx < cleaned.length) {
            val c = cleaned[idx]
            when {
                c.isDigit() -> {
                    var end = idx + 1
                    while (end < cleaned.length && cleaned[end].isDigit()) end++
                    tokens += cleaned.substring(idx, end)
                    idx = end
                }
                c in setOf('+', '-', '*', '/', '(', ')') -> {
                    tokens += c.toString()
                    idx++
                }
                else -> error("Unsupported token: $c")
            }
        }
        return tokens
    }

    private fun toPostfix(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val operators = ArrayDeque<String>()
        val precedence = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2)

        tokens.forEach { token ->
            when {
                token.toIntOrNull() != null -> output += token
                token == "(" -> operators.addLast(token)
                token == ")" -> {
                    while (operators.isNotEmpty() && operators.last() != "(") {
                        output += operators.removeLast()
                    }
                    require(operators.isNotEmpty() && operators.last() == "(") { "Mismatched parenthesis" }
                    operators.removeLast()
                }
                precedence.containsKey(token) -> {
                    while (operators.isNotEmpty()) {
                        val last = operators.last()
                        val shouldPop = precedence[last] != null && precedence[last]!! >= precedence[token]!!
                        if (!shouldPop) break
                        output += operators.removeLast()
                    }
                    operators.addLast(token)
                }
                else -> error("Unknown token: $token")
            }
        }

        while (operators.isNotEmpty()) {
            val op = operators.removeLast()
            require(op != "(") { "Mismatched parenthesis" }
            output += op
        }

        return output
    }

    private fun evaluatePostfix(postfix: List<String>): Int {
        val stack = ArrayDeque<Int>()
        postfix.forEach { token ->
            val n = token.toIntOrNull()
            if (n != null) {
                stack.addLast(n)
            } else {
                require(stack.size >= 2) { "Invalid expression" }
                val right = stack.removeLast()
                val left = stack.removeLast()
                val value = when (token) {
                    "+" -> left + right
                    "-" -> {
                        val r = left - right
                        require(r >= 0) { "Negative intermediate result is not allowed" }
                        r
                    }
                    "*" -> left * right
                    "/" -> {
                        require(right != 0) { "Division by zero" }
                        require(left % right == 0) { "Division must be integer" }
                        left / right
                    }
                    else -> error("Unknown operator: $token")
                }
                stack.addLast(value)
            }
        }
        require(stack.size == 1) { "Invalid expression" }
        return stack.last()
    }
}

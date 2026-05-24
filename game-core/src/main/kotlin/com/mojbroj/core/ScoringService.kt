package com.mojbroj.core

import com.mojbroj.core.model.EvaluationStatus

class ScoringService {
    fun distance(target: Int, result: Int): Int = kotlin.math.abs(target - result)

    fun status(target: Int, result: Int, isValid: Boolean): EvaluationStatus {
        if (!isValid) return EvaluationStatus.INVALID
        return if (target == result) EvaluationStatus.EXACT else EvaluationStatus.CLOSEST
    }
}

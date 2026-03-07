/*
 * Copyright (c) 2024-2025 Jakub Jirak. All rights reserved.
 * Licensed under the terms of the project license.
 */
package com.jakubjirak.metalshader.analysis

import com.jakubjirak.metalshader.analysis.MetalShaderComplexityAnalyzer.ComplexityLevel
import com.jakubjirak.metalshader.analysis.MetalShaderComplexityAnalyzer.ComplexityReport
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MetalShaderComplexityAnalyzerTest {

    private val analyzer = MetalShaderComplexityAnalyzer()

    @Test
    fun `generateOptimizationSuggestions returns optimal for LOW complexity`() {
        val report = ComplexityReport(
            instructionCount = 10, textureReads = 1, textureWrites = 1,
            mathOperations = 5, branchCount = 1, loopCount = 0,
            threadgroupSyncCount = 0, estimatedCyclesMin = 10,
            estimatedCyclesMax = 30, complexityLevel = ComplexityLevel.LOW,
            warnings = emptyList()
        )
        val suggestions = analyzer.generateOptimizationSuggestions(report)
        assertTrue(suggestions.any { it.contains("optimal") })
    }

    @Test
    fun `generateOptimizationSuggestions returns monitoring message for MEDIUM complexity`() {
        val report = ComplexityReport(
            instructionCount = 50, textureReads = 5, textureWrites = 1,
            mathOperations = 30, branchCount = 3, loopCount = 1,
            threadgroupSyncCount = 0, estimatedCyclesMin = 80,
            estimatedCyclesMax = 150, complexityLevel = ComplexityLevel.MEDIUM,
            warnings = emptyList()
        )
        val suggestions = analyzer.generateOptimizationSuggestions(report)
        assertTrue(suggestions.any { it.contains("Moderate complexity") })
        assertTrue(suggestions.any { it.contains("texture reads") })
    }

    @Test
    fun `generateOptimizationSuggestions returns optimization needed for HIGH complexity`() {
        val report = ComplexityReport(
            instructionCount = 200, textureReads = 3, textureWrites = 1,
            mathOperations = 60, branchCount = 8, loopCount = 2,
            threadgroupSyncCount = 1, estimatedCyclesMin = 250,
            estimatedCyclesMax = 450, complexityLevel = ComplexityLevel.HIGH,
            warnings = emptyList()
        )
        val suggestions = analyzer.generateOptimizationSuggestions(report)
        assertTrue(suggestions.any { it.contains("High complexity") })
        assertTrue(suggestions.any { it.contains("Profile on target hardware") })
        assertTrue(suggestions.any { it.contains("Reduce branches") })
    }

    @Test
    fun `generateOptimizationSuggestions returns significant optimization for VERY_HIGH complexity`() {
        val report = ComplexityReport(
            instructionCount = 600, textureReads = 10, textureWrites = 2,
            mathOperations = 100, branchCount = 15, loopCount = 5,
            threadgroupSyncCount = 3, estimatedCyclesMin = 600,
            estimatedCyclesMax = 1200, complexityLevel = ComplexityLevel.VERY_HIGH,
            warnings = emptyList()
        )
        val suggestions = analyzer.generateOptimizationSuggestions(report)
        assertTrue(suggestions.any { it.contains("Very high complexity") })
        assertTrue(suggestions.any { it.contains("splitting into multiple passes") })
        assertTrue(suggestions.any { it.contains("Unroll loops") })
    }

    @Test
    fun `generateOptimizationSuggestions includes read-heavy suggestion`() {
        val report = ComplexityReport(
            instructionCount = 10, textureReads = 10, textureWrites = 1,
            mathOperations = 5, branchCount = 1, loopCount = 0,
            threadgroupSyncCount = 0, estimatedCyclesMin = 10,
            estimatedCyclesMax = 30, complexityLevel = ComplexityLevel.LOW,
            warnings = emptyList()
        )
        val suggestions = analyzer.generateOptimizationSuggestions(report)
        assertTrue(suggestions.any { it.contains("Read-heavy workload") })
    }

    @Test
    fun `generateOptimizationSuggestions includes threadgroup sync suggestion`() {
        val report = ComplexityReport(
            instructionCount = 10, textureReads = 1, textureWrites = 1,
            mathOperations = 5, branchCount = 1, loopCount = 0,
            threadgroupSyncCount = 2, estimatedCyclesMin = 10,
            estimatedCyclesMax = 30, complexityLevel = ComplexityLevel.LOW,
            warnings = emptyList()
        )
        val suggestions = analyzer.generateOptimizationSuggestions(report)
        assertTrue(suggestions.any { it.contains("threadgroup_barrier") })
    }

    @Test
    fun `ComplexityReport data class properties are accessible`() {
        val report = ComplexityReport(
            instructionCount = 42, textureReads = 5, textureWrites = 3,
            mathOperations = 20, branchCount = 7, loopCount = 2,
            threadgroupSyncCount = 1, estimatedCyclesMin = 100,
            estimatedCyclesMax = 300, complexityLevel = ComplexityLevel.HIGH,
            warnings = listOf("test warning")
        )
        assertEquals(42, report.instructionCount)
        assertEquals(5, report.textureReads)
        assertEquals(3, report.textureWrites)
        assertEquals(ComplexityLevel.HIGH, report.complexityLevel)
        assertEquals(1, report.warnings.size)
    }

    @Test
    fun `ComplexityLevel enum has four levels`() {
        val levels = ComplexityLevel.entries
        assertEquals(4, levels.size)
        assertTrue(levels.contains(ComplexityLevel.LOW))
        assertTrue(levels.contains(ComplexityLevel.MEDIUM))
        assertTrue(levels.contains(ComplexityLevel.HIGH))
        assertTrue(levels.contains(ComplexityLevel.VERY_HIGH))
    }
}

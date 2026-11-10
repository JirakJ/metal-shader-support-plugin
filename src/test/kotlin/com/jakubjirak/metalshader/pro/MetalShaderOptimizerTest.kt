/*
 * Copyright (c) 2024-2025 Jakub Jirak.
 * Licensed under the Apache License, Version 2.0. See LICENSE for details.
 */
package com.jakubjirak.metalshader.pro

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MetalShaderOptimizerTest {

    private val optimizer = MetalShaderOptimizer()

    @Test
    fun `optimizeShader detects pow x 2 and suggests multiplication`() {
        val shader = "float result = pow(x, 2.0);"
        val optimizations = optimizer.optimizeShader(shader)!!
        assertTrue(optimizations.any { it.reason.contains("pow(x,2)") })
        assertTrue(optimizations.any { it.optimized.contains("x * x") })
    }

    @Test
    fun `optimizeShader detects pow x 3 and suggests triple multiplication`() {
        val shader = "float result = pow(y, 3.0);"
        val optimizations = optimizer.optimizeShader(shader)!!
        assertTrue(optimizations.any { it.reason.contains("pow(x,3)") })
        assertTrue(optimizations.any { it.optimized.contains("y * y * y") })
    }

    @Test
    fun `optimizeShader detects pow x 0_5 and suggests sqrt`() {
        val shader = "float result = pow(v, 0.5);"
        val optimizations = optimizer.optimizeShader(shader)!!
        assertTrue(optimizations.any { it.reason.contains("sqrt") })
        assertTrue(optimizations.any { it.optimized.contains("sqrt(v)") })
    }

    @Test
    fun `optimizeShader detects inverse sqrt and suggests rsqrt`() {
        val shader = "float result = 1.0/sqrt(value);"
        val optimizations = optimizer.optimizeShader(shader)!!
        assertTrue(optimizations.any { it.reason.contains("rsqrt") })
        assertTrue(optimizations.any { it.optimized.contains("rsqrt(") })
    }

    @Test
    fun `optimizeShader detects division by constant`() {
        val shader = "float result = value /3.14;"
        val optimizations = optimizer.optimizeShader(shader)!!
        assertTrue(optimizations.any { it.reason.contains("division by constant") })
    }

    @Test
    fun `optimizeShader detects float to half precision opportunity`() {
        val shader = "float4 color = float4(1.0, 0.0, 0.0, 1.0);"
        val optimizations = optimizer.optimizeShader(shader)!!
        assertTrue(optimizations.any { it.reason.contains("half precision") })
    }

    @Test
    fun `optimizeShader skips half precision for raytracing code`() {
        val shader = "float4 color = raytracing;"
        val optimizations = optimizer.optimizeShader(shader)!!
        assertFalse(optimizations.any { it.reason.contains("half precision") })
    }

    @Test
    fun `optimizeShader returns empty list for clean shader`() {
        val shader = "int a = 1;\nint b = 2;\nint c = a + b;"
        val optimizations = optimizer.optimizeShader(shader)!!
        assertTrue(optimizations.isEmpty())
    }

    @Test
    fun `formatOptimizations shows already optimized for empty list`() {
        val result = optimizer.formatOptimizations(emptyList())
        assertTrue(result.contains("already well optimized"))
    }

    @Test
    fun `formatOptimizations includes optimization count and speedup`() {
        val opts = listOf(
            MetalShaderOptimizer.Optimization(1, "pow(x, 2.0)", "x * x", "Replace pow", 40.0),
            MetalShaderOptimizer.Optimization(5, "1.0/sqrt(y)", "rsqrt(y)", "Use rsqrt", 25.0)
        )
        val result = optimizer.formatOptimizations(opts)
        assertTrue(result.contains("2 optimization(s)"))
        assertTrue(result.contains("Estimated total speedup"))
        assertTrue(result.contains("Replace pow"))
    }

    @Test
    fun `applyOptimizations replaces correct lines`() {
        val code = "line0\nline1\nline2"
        val opts = listOf(
            MetalShaderOptimizer.Optimization(2, "line1", "REPLACED", "test", 10.0)
        )
        val result = optimizer.applyOptimizations(code, opts)
        val lines = result.lines()
        assertEquals("line0", lines[0])
        assertEquals("REPLACED", lines[1])
        assertEquals("line2", lines[2])
    }

    @Test
    fun `Optimization data class stores all fields correctly`() {
        val opt = MetalShaderOptimizer.Optimization(
            line = 10, original = "orig", optimized = "opt",
            reason = "because", estimatedSpeedup = 42.5
        )
        assertEquals(10, opt.line)
        assertEquals("orig", opt.original)
        assertEquals("opt", opt.optimized)
        assertEquals("because", opt.reason)
        assertEquals(42.5, opt.estimatedSpeedup)
    }
}

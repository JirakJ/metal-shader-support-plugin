/*
 * Copyright (c) 2024-2025 Jakub Jirak.
 * Licensed under the Apache License, Version 2.0. See LICENSE for details.
 */
package com.jakubjirak.metalshader.pro

import com.jakubjirak.metalshader.pro.MetalShaderProfiler.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MetalShaderProfilerTest {

    private val profiler = MetalShaderProfiler()

    @Test
    fun `profileShader returns minimal cycles for trivial shader`() {
        val shader = "int a = 1;"
        val result = profiler.profileShader(shader, null)!!
        assertTrue(result.estimatedGPUCycles >= 0)
        assertTrue(result.recommendations.contains("Shader is well optimized!"))
    }

    @Test
    fun `profileShader detects texture sampling bottleneck`() {
        val shader = (1..15).joinToString("\n") { "tex.sample(s, uv$it);" }
        val result = profiler.profileShader(shader, null)!!
        assertTrue(result.bottlenecks.any { it.type == BottleneckType.TEXTURE_SAMPLING })
        assertTrue(result.recommendations.any { it.contains("texture") })
    }

    @Test
    fun `profileShader detects ALU bottleneck from divisions`() {
        val shader = (1..8).joinToString("\n") { "float r$it = a / b$it;" }
        val result = profiler.profileShader(shader, null)!!
        assertTrue(result.bottlenecks.any {
            it.type == BottleneckType.ALU_BOUND && it.location.contains("Division")
        })
    }

    @Test
    fun `profileShader detects pow bottleneck`() {
        val shader = (1..5).joinToString("\n") { "float r$it = pow(x, $it.0);" }
        val result = profiler.profileShader(shader, null)!!
        assertTrue(result.bottlenecks.any {
            it.type == BottleneckType.ALU_BOUND && it.description.contains("pow()")
        })
    }

    @Test
    fun `profileShader detects thread divergence from many branches`() {
        val shader = (1..8).joinToString("\n") { "if (cond$it) { result += $it; }" }
        val result = profiler.profileShader(shader, null)!!
        assertTrue(result.bottlenecks.any { it.type == BottleneckType.THREAD_DIVERGENCE })
    }

    @Test
    fun `profileShader calculates latency from cycles`() {
        val shader = (1..5).joinToString("\n") { "float r$it = a * b$it;" }
        val result = profiler.profileShader(shader, null)!!
        assertTrue(result.estimatedLatency > 0.0)
        assertEquals(result.estimatedGPUCycles.toDouble() / 1500.0, result.estimatedLatency, 0.001)
    }

    @Test
    fun `formatProfileResult includes cycle count and latency`() {
        val result = ProfileResult(
            estimatedGPUCycles = 5000,
            estimatedLatency = 3.33,
            bottlenecks = listOf(
                Bottleneck(BottleneckType.ALU_BOUND, "test", Impact.HIGH, "Many ops")
            ),
            recommendations = listOf("Optimize divisions")
        )
        val output = profiler.formatProfileResult(result)
        assertTrue(output.contains("5000"))
        assertTrue(output.contains("Estimated Latency"))
        assertTrue(output.contains("ALU_BOUND"))
        assertTrue(output.contains("Optimize divisions"))
    }

    @Test
    fun `BottleneckType enum has six values`() {
        assertEquals(6, BottleneckType.entries.size)
    }

    @Test
    fun `Impact enum has four values`() {
        assertEquals(4, Impact.entries.size)
        assertTrue(Impact.entries.contains(Impact.CRITICAL))
    }
}

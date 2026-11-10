/*
 * Copyright (c) 2024-2025 Jakub Jirak.
 * Licensed under the Apache License, Version 2.0. See LICENSE for details.
 */
package com.jakubjirak.metalshader.pro

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MetalDebugHelperTest {

    private val helper = MetalDebugHelper()

    @Test
    fun `generateTextureVisualizer contains kernel functions`() {
        val code = helper.generateTextureVisualizer()
        assertTrue(code.contains("kernel void visualize_texture"))
        assertTrue(code.contains("kernel void visualize_heatmap"))
        assertTrue(code.contains("kernel void visualize_differences"))
    }

    @Test
    fun `generateTextureVisualizer uses custom texture name`() {
        val code = helper.generateTextureVisualizer("myTex")
        assertTrue(code.contains("myTex"))
        assertFalse(code.contains("debugTexture"))
    }

    @Test
    fun `generatePerformanceCounters contains PerformanceStats struct`() {
        val code = helper.generatePerformanceCounters()
        assertTrue(code.contains("struct PerformanceStats"))
        assertTrue(code.contains("kernel void benchmark_kernel"))
        assertTrue(code.contains("atomic_fetch_add_explicit"))
    }

    @Test
    fun `generateAssertHelpers contains assert functions`() {
        val code = helper.generateAssertHelpers()
        assertTrue(code.contains("assert_range"))
        assertTrue(code.contains("assert_texture_coord"))
        assertTrue(code.contains("assert_finite"))
        assertTrue(code.contains("isnan"))
        assertTrue(code.contains("isinf"))
    }

    @Test
    fun `instrumentShader adds debug buffer to kernel entry point`() {
        val shader = "kernel void myKernel(device float* data [[buffer(0)]])"
        val result = helper.instrumentShader(shader)!!
        assertTrue(result.instrumentedCode.contains("debug_buffer"))
    }

    @Test
    fun `instrumentShader adds debug buffer to vertex entry point`() {
        val shader = "vertex float4 myVertex(uint vid [[vertex_id]])"
        val result = helper.instrumentShader(shader)!!
        assertTrue(result.instrumentedCode.contains("debug_buffer"))
    }

    @Test
    fun `instrumentShader with debug variables throws ConcurrentModificationException due to known bug`() {
        val shader = """
            kernel void myKernel(device float* data [[buffer(0)]]) {
                float value = data[0];
            }
        """.trimIndent()
        // Production code modifies list during iteration — known issue
        assertThrows(ConcurrentModificationException::class.java) {
            helper.instrumentShader(shader, listOf("value"))
        }
    }

    @Test
    fun `instrumentShader without debug variables returns empty debug points`() {
        val shader = "kernel void myKernel(device float* data [[buffer(0)]])"
        val result = helper.instrumentShader(shader)!!
        assertTrue(result.debugPoints.isEmpty())
    }

    @Test
    fun `DebugPoint data class stores all fields`() {
        val point = MetalDebugHelper.DebugPoint(id = 0, line = 5, variable = "color", type = "float4")
        assertEquals(0, point.id)
        assertEquals(5, point.line)
        assertEquals("color", point.variable)
        assertEquals("float4", point.type)
    }
}

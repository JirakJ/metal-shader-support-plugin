/*
 * Copyright (c) 2024-2026 Jakub Jirak.
 * Licensed under the Apache License, Version 2.0. See LICENSE for details.
 */
package com.jakubjirak.metalshader.annotator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MetalAnnotatorTest {

    @Test
    fun `no warnings when different functions reuse buffer indices`() {
        val code = """
            kernel void kernel_a(device const float* input [[buffer(0)]], device float* output [[buffer(1)]], uint id [[thread_position_in_grid]]) {
                output[id] = input[id] * 2.0;
            }
            kernel void kernel_b(device const float* input [[buffer(0)]], device float* output [[buffer(1)]], uint id [[thread_position_in_grid]]) {
                output[id] = input[id] + 1.0;
            }
        """.trimIndent()

        val warnings = MetalAnnotator.findDuplicateBindings(code)
        assertTrue(warnings.isEmpty(), "Should not warn when buffer indices are reused across different functions, got: $warnings")
    }

    @Test
    fun `no warnings when different functions reuse texture indices`() {
        val code = """
            fragment float4 fragment_a(texture2d<float> tex [[texture(0)]], float2 uv [[stage_in]]) {
                constexpr sampler s;
                return tex.sample(s, uv);
            }
            fragment float4 fragment_b(texture2d<float> tex [[texture(0)]], float2 uv [[stage_in]]) {
                constexpr sampler s;
                return tex.sample(s, uv) * 0.5;
            }
        """.trimIndent()

        val warnings = MetalAnnotator.findDuplicateBindings(code)
        assertTrue(warnings.isEmpty(), "Should not warn when texture indices are reused across different functions, got: $warnings")
    }

    @Test
    fun `no warnings when mixing kernel vertex and fragment with same indices`() {
        val code = """
            kernel void compute(device float* buf [[buffer(0)]], uint id [[thread_position_in_grid]]) {}
            vertex float4 vert(device const float* verts [[buffer(0)]], uint vid [[vertex_id]]) { return float4(0); }
            fragment float4 frag(texture2d<float> tex [[texture(0)]]) { return float4(0); }
        """.trimIndent()

        val warnings = MetalAnnotator.findDuplicateBindings(code)
        assertTrue(warnings.isEmpty(), "Different function types can reuse indices, got: $warnings")
    }

    @Test
    fun `warns when same function has duplicate buffer indices`() {
        val code = """
            kernel void bad_kernel(device float* a [[buffer(0)]], device float* b [[buffer(0)]], uint id [[thread_position_in_grid]]) {}
        """.trimIndent()

        val warnings = MetalAnnotator.findDuplicateBindings(code)
        assertEquals(1, warnings.size)
        assertEquals("Duplicate buffer binding indices detected", warnings[0])
    }

    @Test
    fun `warns when same function has duplicate texture indices`() {
        val code = """
            fragment float4 bad_frag(texture2d<float> t1 [[texture(0)]], texture2d<float> t2 [[texture(0)]]) { return float4(0); }
        """.trimIndent()

        val warnings = MetalAnnotator.findDuplicateBindings(code)
        assertEquals(1, warnings.size)
        assertEquals("Duplicate texture binding indices detected", warnings[0])
    }

    @Test
    fun `no warnings for unique indices within a single function`() {
        val code = """
            kernel void good_kernel(device float* a [[buffer(0)]], device float* b [[buffer(1)]], device float* c [[buffer(2)]], uint id [[thread_position_in_grid]]) {}
        """.trimIndent()

        val warnings = MetalAnnotator.findDuplicateBindings(code)
        assertTrue(warnings.isEmpty())
    }

    @Test
    fun `no warnings for empty text`() {
        val warnings = MetalAnnotator.findDuplicateBindings("")
        assertTrue(warnings.isEmpty())
    }

    @Test
    fun `no warnings for non-function code`() {
        val code = """
            #include <metal_stdlib>
            using namespace metal;
            struct VertexOut { float4 position; };
        """.trimIndent()

        val warnings = MetalAnnotator.findDuplicateBindings(code)
        assertTrue(warnings.isEmpty())
    }

    @Test
    fun `exact reproduction of reported bug produces no warnings`() {
        // This is the exact code from the bug report
        val code = """
            #include <metal_stdlib>
            using namespace metal;
            kernel void kernel_a(
                device const float* input [[buffer(0)]],
                device float* output [[buffer(1)]],
                uint id [[thread_position_in_grid]]
            ) {
                output[id] = input[id] * 2.0;
            }
            kernel void kernel_b(
                device const float* input [[buffer(0)]],
                device float* output [[buffer(1)]],
                uint id [[thread_position_in_grid]]
            ) {
                output[id] = input[id] + 1.0;
            }
            fragment float4 fragment_a(
                texture2d<float> tex [[texture(0)]],
                float2 uv [[stage_in]]
            ) {
                constexpr sampler s(coord::normalized, address::clamp_to_edge, filter::linear);
                return tex.sample(s, uv);
            }
            fragment float4 fragment_b(
                texture2d<float> tex [[texture(0)]],
                float2 uv [[stage_in]]
            ) {
                constexpr sampler s(coord::normalized, address::clamp_to_edge, filter::nearest);
                return tex.sample(s, uv) * 0.5;
            }
        """.trimIndent()

        val warnings = MetalAnnotator.findDuplicateBindings(code)
        assertTrue(warnings.isEmpty(), "Bug report sample code should produce zero warnings, got: $warnings")
    }
}

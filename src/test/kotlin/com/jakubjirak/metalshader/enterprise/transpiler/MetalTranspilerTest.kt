/*
 * Copyright (c) 2026 Jakub Jirák.
 * Licensed under the Apache License, Version 2.0. See LICENSE for details.
 */
package com.jakubjirak.metalshader.enterprise.transpiler

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MetalTranspilerTest {

    private val shader = """
        #include <metal_stdlib>
        using namespace metal;

        struct VertexOut {
            float4 position [[position]];
            float2 uv;
        };

        vertex VertexOut vertexMain(uint vid [[vertex_id]]) {
            VertexOut out;
            out.position = float4(0.0, 0.0, 0.0, 1.0);
            return out;
        }

        fragment float4 fragmentMain(VertexOut in [[stage_in]]) {
            return float4(in.uv, 0.0, 1.0);
        }

        kernel void addArrays(device const float* a [[buffer(0)]],
                              device float* result [[buffer(1)]],
                              uint gid [[thread_position_in_grid]]) {
            result[gid] = a[gid] * 2.0;
        }
    """.trimIndent()

    @Test
    fun `spirv skeleton declares every entry point with its execution model`() {
        val spirv = MetalTranspiler.convertMetalToSPIRV(shader, "demo.metal")
        assertTrue(spirv.startsWith("; SPIR-V assembly transpiled from Metal source: demo.metal"))
        assertTrue(spirv.contains("OpCapability Shader"))
        assertTrue(spirv.contains("OpEntryPoint Vertex %vertexMain \"vertexMain\""), spirv)
        assertTrue(spirv.contains("OpEntryPoint Fragment %fragmentMain \"fragmentMain\""))
        assertTrue(spirv.contains("OpEntryPoint GLCompute %addArrays \"addArrays\""))
        assertTrue(spirv.contains("OpExecutionMode %fragmentMain OriginUpperLeft"))
        assertTrue(spirv.contains("; Struct: VertexOut"))
        assertTrue(spirv.contains("OpFunctionEnd"))
    }

    @Test
    fun `hlsl output maps types and semantics`() {
        val hlsl = MetalTranspiler.convertMetalToHLSL(shader, "demo.metal")
        assertTrue(hlsl.contains("fragmentMain"), hlsl)
        assertTrue(hlsl.contains(": SV_Target"), hlsl)
        assertTrue(hlsl.contains("addArrays"))
        assertFalse(hlsl.contains("[[stage_in]]"), "Metal attributes must not leak into HLSL")
    }

    @Test
    fun `wgsl output uses wgsl vector types and stage attributes`() {
        val wgsl = MetalTranspiler.convertMetalToWGSL(shader, "demo.metal")
        assertTrue(wgsl.contains("vec4<f32>"), wgsl)
        assertTrue(wgsl.contains("@vertex") || wgsl.contains("@fragment") || wgsl.contains("@compute"), wgsl)
        assertFalse(wgsl.contains("float4("), "Metal vector constructors are converted")
    }

    @Test
    fun `source without entry points still produces a valid header`() {
        val spirv = MetalTranspiler.convertMetalToSPIRV("// nothing here", "empty.metal")
        assertTrue(spirv.contains("OpMemoryModel Logical GLSL450"))
        assertFalse(spirv.contains("OpEntryPoint"))
        assertTrue(MetalTranspiler.convertMetalToHLSL("", "empty.metal").isNotEmpty())
        assertTrue(MetalTranspiler.convertMetalToWGSL("", "empty.metal").isNotEmpty())
    }
}

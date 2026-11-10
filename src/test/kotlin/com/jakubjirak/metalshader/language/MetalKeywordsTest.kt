/*
 * Copyright (c) 2024-2025 Jakub Jirak.
 * Licensed under the Apache License, Version 2.0. See LICENSE for details.
 */
package com.jakubjirak.metalshader.language

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MetalKeywordsTest {

    @Test
    fun `KEYWORDS contains shader function qualifiers`() {
        assertTrue(MetalKeywords.KEYWORDS.containsAll(listOf("kernel", "vertex", "fragment")))
    }

    @Test
    fun `KEYWORDS contains control flow keywords`() {
        assertTrue(MetalKeywords.KEYWORDS.containsAll(listOf("if", "else", "for", "while", "switch", "return")))
    }

    @Test
    fun `KEYWORDS contains Metal 4 mesh shader qualifiers`() {
        assertTrue(MetalKeywords.KEYWORDS.containsAll(listOf("object", "mesh")))
    }

    @Test
    fun `TYPES contains scalar types`() {
        assertTrue(MetalKeywords.TYPES.containsAll(listOf("void", "bool", "int", "uint", "half", "float")))
    }

    @Test
    fun `TYPES contains vector types`() {
        assertTrue(MetalKeywords.TYPES.containsAll(listOf("float2", "float3", "float4", "half4", "int4")))
    }

    @Test
    fun `TYPES contains matrix types`() {
        assertTrue(MetalKeywords.TYPES.containsAll(listOf("float4x4", "float3x3", "half4x4")))
    }

    @Test
    fun `TYPES contains texture types`() {
        assertTrue(MetalKeywords.TYPES.containsAll(listOf("texture2d", "texture3d", "texturecube", "sampler")))
    }

    @Test
    fun `QUALIFIERS contains address space qualifiers`() {
        assertTrue(MetalKeywords.QUALIFIERS.containsAll(listOf("device", "threadgroup", "constant", "thread")))
    }

    @Test
    fun `QUALIFIERS contains access qualifiers`() {
        assertTrue(MetalKeywords.QUALIFIERS.containsAll(listOf("read", "write", "read_write")))
    }

    @Test
    fun `BUILT_IN_FUNCTIONS contains math functions`() {
        assertTrue(MetalKeywords.BUILT_IN_FUNCTIONS.containsAll(
            listOf("sin", "cos", "tan", "pow", "sqrt", "abs", "clamp", "mix", "normalize")
        ))
    }

    @Test
    fun `BUILT_IN_FUNCTIONS contains texture functions`() {
        assertTrue(MetalKeywords.BUILT_IN_FUNCTIONS.containsAll(
            listOf("sample", "read", "write", "gather", "get_width", "get_height")
        ))
    }

    @Test
    fun `BUILT_IN_FUNCTIONS contains synchronization functions`() {
        assertTrue(MetalKeywords.BUILT_IN_FUNCTIONS.containsAll(
            listOf("threadgroup_barrier", "simdgroup_barrier")
        ))
    }

    @Test
    fun `BUILT_IN_FUNCTIONS contains Metal 4 raytracing functions`() {
        assertTrue(MetalKeywords.BUILT_IN_FUNCTIONS.containsAll(
            listOf("trace_ray", "intersect_ray", "get_world_ray_origin")
        ))
    }

    @Test
    fun `all keyword sets are non-empty`() {
        assertTrue(MetalKeywords.KEYWORDS.isNotEmpty())
        assertTrue(MetalKeywords.TYPES.isNotEmpty())
        assertTrue(MetalKeywords.QUALIFIERS.isNotEmpty())
        assertTrue(MetalKeywords.BUILT_IN_FUNCTIONS.isNotEmpty())
    }

    @Test
    fun `KEYWORDS and TYPES do not overlap except for address space qualifiers`() {
        val keywordsOnlyOverlap = MetalKeywords.KEYWORDS.intersect(MetalKeywords.TYPES)
        // These sets should be mostly distinct
        assertFalse(keywordsOnlyOverlap.contains("float"))
        assertFalse(keywordsOnlyOverlap.contains("kernel"))
    }
}

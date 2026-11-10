/*
 * Copyright (c) 2024-2025 Jakub Jirak.
 * Licensed under the Apache License, Version 2.0. See LICENSE for details.
 */
package com.jakubjirak.metalshader.pro

import com.jakubjirak.metalshader.pro.MetalShaderValidator.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MetalShaderValidatorTest {

    private val validator = MetalShaderValidator()

    @Test
    fun `validate detects threadgroup size exceeding 1024`() {
        val shader = "threads_per_threadgroup 64 32"
        val result = validator.validate(shader)!!
        assertTrue(result.errors.any { it.message.contains("exceeds maximum") })
        assertFalse(result.isValid)
    }

    @Test
    fun `validate warns on large threadgroup size above 512`() {
        val shader = "threads_per_threadgroup 600"
        val result = validator.validate(shader)!!
        assertTrue(result.warnings.any {
            it.message.contains("reduce occupancy") && it.category == WarningCategory.PERFORMANCE
        })
    }

    @Test
    fun `validate warns on missing buffer binding attribute`() {
        val shader = "device float* buffer"
        val result = validator.validate(shader)!!
        assertTrue(result.warnings.any {
            it.message.contains("[[buffer(N)]]") && it.category == WarningCategory.BEST_PRACTICE
        })
    }

    @Test
    fun `validate warns on missing texture binding attribute`() {
        val shader = "texture2d<float> myTexture"
        val result = validator.validate(shader)!!
        assertTrue(result.warnings.any {
            it.message.contains("[[texture(N)]]") && it.category == WarningCategory.BEST_PRACTICE
        })
    }

    @Test
    fun `validate warns on deprecated get_global_id`() {
        val shader = "uint id = get_global_id(0);"
        val result = validator.validate(shader)!!
        assertTrue(result.warnings.any {
            it.message.contains("thread_position_in_grid") && it.category == WarningCategory.DEPRECATED
        })
    }

    @Test
    fun `validate warns on pow x 2 performance`() {
        val shader = "float r = pow(x, 2.0);"
        val result = validator.validate(shader)!!
        assertTrue(result.warnings.any {
            it.message.contains("pow(x, 2.0)") && it.category == WarningCategory.PERFORMANCE
        })
    }

    @Test
    fun `validate warns on pow x 0_5 performance`() {
        val shader = "float r = pow(x, 0.5);"
        val result = validator.validate(shader)!!
        assertTrue(result.warnings.any {
            it.message.contains("sqrt(x)") && it.category == WarningCategory.PERFORMANCE
        })
    }

    @Test
    fun `validate warns on inverse sqrt pattern`() {
        val shader = "float r = 1.0 / sqrt(x);"
        val result = validator.validate(shader)!!
        assertTrue(result.warnings.any {
            it.message.contains("rsqrt") && it.category == WarningCategory.PERFORMANCE
        })
    }

    @Test
    fun `validate warns on mesh shader without Metal 3_2`() {
        val shader = "mesh<void> myMesh;"
        val result = validator.validate(shader)!!
        assertTrue(result.warnings.any {
            it.message.contains("Metal 3.2+") && it.category == WarningCategory.PORTABILITY
        })
    }

    @Test
    fun `validate returns valid for clean shader`() {
        val shader = "int a = 1;\nint b = 2;"
        val result = validator.validate(shader)!!
        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `formatValidationResult shows no errors for valid result`() {
        val result = ValidationResult(errors = emptyList(), warnings = emptyList(), isValid = true)
        val output = validator.formatValidationResult(result)
        assertTrue(output.contains("No errors found"))
        assertTrue(output.contains("No warnings"))
    }

    @Test
    fun `formatValidationResult shows error count for invalid result`() {
        val result = ValidationResult(
            errors = listOf(
                ValidationError(1, "Test error", Severity.ERROR),
                ValidationError(5, "Another error", Severity.ERROR)
            ),
            warnings = emptyList(),
            isValid = false
        )
        val output = validator.formatValidationResult(result)
        assertTrue(output.contains("2 error(s) found"))
        assertTrue(output.contains("Test error"))
    }

    @Test
    fun `formatValidationResult groups warnings by category`() {
        val result = ValidationResult(
            errors = emptyList(),
            warnings = listOf(
                ValidationWarning(1, "Perf issue", WarningCategory.PERFORMANCE),
                ValidationWarning(2, "Compat issue", WarningCategory.PORTABILITY)
            ),
            isValid = true
        )
        val output = validator.formatValidationResult(result)
        assertTrue(output.contains("2 warning(s)"))
        assertTrue(output.contains("PERFORMANCE"))
        assertTrue(output.contains("PORTABILITY"))
    }

    @Test
    fun `Severity enum has three values`() {
        assertEquals(3, Severity.entries.size)
    }

    @Test
    fun `WarningCategory enum has four values`() {
        assertEquals(4, WarningCategory.entries.size)
    }
}

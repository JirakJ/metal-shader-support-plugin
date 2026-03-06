package com.jakubjirak.metalshader.pro

/**
 * Advanced shader validation and error detection
 */
class MetalShaderValidator {
    
    data class ValidationResult(
        val errors: List<ValidationError>,
        val warnings: List<ValidationWarning>,
        val isValid: Boolean
    )
    
    data class ValidationError(
        val line: Int,
        val message: String,
        val severity: Severity
    )
    
    data class ValidationWarning(
        val line: Int,
        val message: String,
        val category: WarningCategory
    )
    
    enum class Severity {
        ERROR, WARNING, INFO
    }
    
    enum class WarningCategory {
        PERFORMANCE,
        PORTABILITY,
        DEPRECATED,
        BEST_PRACTICE
    }
    
    fun validate(shaderCode: String): ValidationResult? {
        val errors = mutableListOf<ValidationError>()
        val warnings = mutableListOf<ValidationWarning>()
        val lines = shaderCode.lines()
        
        lines.forEachIndexed { lineIdx, line ->
            val lineNum = lineIdx + 1
            
            // Check threadgroup size limits
            if (line.contains("threads_per_threadgroup")) {
                val numbers = Regex("\\d+").findAll(line).map { it.value.toInt() }.toList()
                if (numbers.isNotEmpty()) {
                    val total = numbers.reduce { acc, n -> acc * n }
                    if (total > 1024) {
                        errors.add(ValidationError(
                            lineNum,
                            "Threadgroup size ($total) exceeds maximum (1024)",
                            Severity.ERROR
                        ))
                    }
                    if (total > 512) {
                        warnings.add(ValidationWarning(
                            lineNum,
                            "Large threadgroup size ($total) may reduce occupancy",
                            WarningCategory.PERFORMANCE
                        ))
                    }
                }
            }
            
            // Check for missing buffer bindings
            if (line.contains("device") || line.contains("constant")) {
                if (!line.contains("[[buffer(")) {
                    warnings.add(ValidationWarning(
                        lineNum,
                        "Buffer parameter missing [[buffer(N)]] attribute",
                        WarningCategory.BEST_PRACTICE
                    ))
                }
            }
            
            // Check for texture binding
            if (line.contains("texture2d") || line.contains("texture3d")) {
                if (!line.contains("[[texture(")) {
                    warnings.add(ValidationWarning(
                        lineNum,
                        "Texture parameter missing [[texture(N)]] attribute",
                        WarningCategory.BEST_PRACTICE
                    ))
                }
            }
            
            // Check for deprecated functions
            if (line.contains("get_global_id") || line.contains("get_local_id")) {
                warnings.add(ValidationWarning(
                    lineNum,
                    "Consider using thread_position_in_grid/threadgroup instead",
                    WarningCategory.DEPRECATED
                ))
            }
            
            // Performance warnings
            if (line.contains("pow(") && line.contains(", 2")) {
                warnings.add(ValidationWarning(
                    lineNum,
                    "Replace pow(x, 2.0) with x*x for better performance",
                    WarningCategory.PERFORMANCE
                ))
            }
            
            if (line.contains("pow(") && line.contains(", 0.5")) {
                warnings.add(ValidationWarning(
                    lineNum,
                    "Replace pow(x, 0.5) with sqrt(x) for better performance",
                    WarningCategory.PERFORMANCE
                ))
            }
            
            if (line.contains("1.0 / sqrt")) {
                warnings.add(ValidationWarning(
                    lineNum,
                    "Use rsqrt(x) instead of 1.0/sqrt(x)",
                    WarningCategory.PERFORMANCE
                ))
            }
            
            // Check for uninitialized threadgroup memory
            if (line.contains("threadgroup") && !line.contains("=")) {
                warnings.add(ValidationWarning(
                    lineNum,
                    "Threadgroup memory may be uninitialized",
                    WarningCategory.BEST_PRACTICE
                ))
            }
            
            // Portability warnings
            if (line.contains("half") && line.contains("raytracing")) {
                warnings.add(ValidationWarning(
                    lineNum,
                    "half precision in raytracing may not be supported on all GPUs",
                    WarningCategory.PORTABILITY
                ))
            }
            
            // Metal 4 feature detection
            if (line.contains("mesh<") || line.contains("object<")) {
                if (!shaderCode.contains("metal_version") || !shaderCode.contains("3.2")) {
                    warnings.add(ValidationWarning(
                        lineNum,
                        "Mesh shaders require Metal 3.2+ (iOS 18+, macOS 15+)",
                        WarningCategory.PORTABILITY
                    ))
                }
            }
        }
        
        return ValidationResult(
            errors = errors,
            warnings = warnings,
            isValid = errors.isEmpty()
        )
    }
    
    fun formatValidationResult(result: ValidationResult): String {
        val sb = StringBuilder()
        sb.append("=== Metal Shader Validation ===\n\n")
        
        if (result.isValid) {
            sb.append("✅ No errors found\n\n")
        } else {
            sb.append("❌ ${result.errors.size} error(s) found\n\n")
            result.errors.forEach { error ->
                sb.append("Line ${error.line}: [${error.severity}] ${error.message}\n")
            }
            sb.append("\n")
        }
        
        if (result.warnings.isNotEmpty()) {
            sb.append("⚠️  ${result.warnings.size} warning(s):\n\n")
            result.warnings.groupBy { it.category }.forEach { (category, warnings) ->
                sb.append("$category:\n")
                warnings.forEach { warning ->
                    sb.append("  Line ${warning.line}: ${warning.message}\n")
                }
                sb.append("\n")
            }
        } else {
            sb.append("✅ No warnings\n")
        }
        
        return sb.toString()
    }
}

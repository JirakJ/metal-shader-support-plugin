package com.jakubjirak.metalshader.pro

import com.jakubjirak.metalshader.licensing.MetalLicenseManager

/**
 * Automatic shader optimization suggestions and rewrites
 * PRO FEATURE
 */
class MetalShaderOptimizer {
    
    data class Optimization(
        val line: Int,
        val original: String,
        val optimized: String,
        val reason: String,
        val estimatedSpeedup: Double // percentage
    )
    
    fun optimizeShader(shaderCode: String): List<Optimization>? {
        if (!MetalLicenseManager.getInstance().isProFeatureEnabled()) {
            return null
        }
        
        val optimizations = mutableListOf<Optimization>()
        val lines = shaderCode.lines()
        
        lines.forEachIndexed { lineIdx, line ->
            val lineNum = lineIdx + 1
            
            // Optimize pow(x, 2.0) -> x*x
            if (line.contains(Regex("pow\\s*\\(\\s*(\\w+)\\s*,\\s*2\\.0?\\s*\\)"))) {
                val match = Regex("pow\\s*\\(\\s*(\\w+)\\s*,\\s*2\\.0?\\s*\\)").find(line)
                if (match != null) {
                    val variable = match.groupValues[1]
                    val optimized = line.replace(match.value, "$variable * $variable")
                    optimizations.add(Optimization(
                        lineNum,
                        line.trim(),
                        optimized.trim(),
                        "Replace pow(x,2) with multiplication",
                        40.0
                    ))
                }
            }
            
            // Optimize pow(x, 3.0) -> x*x*x
            if (line.contains(Regex("pow\\s*\\(\\s*(\\w+)\\s*,\\s*3\\.0?\\s*\\)"))) {
                val match = Regex("pow\\s*\\(\\s*(\\w+)\\s*,\\s*3\\.0?\\s*\\)").find(line)
                if (match != null) {
                    val variable = match.groupValues[1]
                    val optimized = line.replace(match.value, "$variable * $variable * $variable")
                    optimizations.add(Optimization(
                        lineNum,
                        line.trim(),
                        optimized.trim(),
                        "Replace pow(x,3) with multiplication",
                        50.0
                    ))
                }
            }
            
            // Optimize pow(x, 0.5) -> sqrt(x)
            if (line.contains(Regex("pow\\s*\\(\\s*(\\w+)\\s*,\\s*0\\.5\\s*\\)"))) {
                val match = Regex("pow\\s*\\(\\s*(\\w+)\\s*,\\s*0\\.5\\s*\\)").find(line)
                if (match != null) {
                    val variable = match.groupValues[1]
                    val optimized = line.replace(match.value, "sqrt($variable)")
                    optimizations.add(Optimization(
                        lineNum,
                        line.trim(),
                        optimized.trim(),
                        "Replace pow(x,0.5) with sqrt",
                        30.0
                    ))
                }
            }
            
            // Optimize 1.0/sqrt(x) -> rsqrt(x)
            if (line.contains(Regex("1\\.0?\\s*/\\s*sqrt\\s*\\("))) {
                val optimized = line.replace(Regex("1\\.0?\\s*/\\s*sqrt\\s*\\("), "rsqrt(")
                optimizations.add(Optimization(
                    lineNum,
                    line.trim(),
                    optimized.trim(),
                    "Use rsqrt instead of 1/sqrt",
                    25.0
                ))
            }
            
            // Optimize division by constant -> multiply by reciprocal
            val divPattern = Regex("/(\\s*\\d+\\.\\d+)")
            if (divPattern.containsMatchIn(line)) {
                divPattern.findAll(line).forEach { match ->
                    val divisor = match.groupValues[1].trim().toDoubleOrNull()
                    if (divisor != null && divisor != 0.0) {
                        val reciprocal = 1.0 / divisor
                        val optimized = line.replace(match.value, " * ${"%.6f".format(reciprocal)}")
                        optimizations.add(Optimization(
                            lineNum,
                            line.trim(),
                            optimized.trim(),
                            "Replace division by constant with multiplication",
                            15.0
                        ))
                    }
                }
            }
            
            // Optimize normalize(vec) when length is already known
            if (line.contains("normalize(") && lines.any { it.contains("length(") }) {
                // Suggest manual normalization if length is computed nearby
                optimizations.add(Optimization(
                    lineNum,
                    line.trim(),
                    line.trim() + " // Consider: vec * rsqrt(dot(vec, vec)) if length is needed",
                    "Manual normalization can save computation if length is reused",
                    10.0
                ))
            }
            
            // Optimize float -> half conversions where precision allows
            if (line.contains("float4") || line.contains("float3") || line.contains("float2")) {
                if (!line.contains("raytracing") && !line.contains("precision")) {
                    optimizations.add(Optimization(
                        lineNum,
                        line.trim(),
                        line.trim().replace("float", "half"),
                        "Consider half precision for 2x bandwidth/memory savings",
                        100.0
                    ))
                }
            }
            
            // Suggest vector operations instead of scalar
            if (line.contains("x.r") && line.contains("x.g") && line.contains("x.b")) {
                optimizations.add(Optimization(
                    lineNum,
                    line.trim(),
                    "// Use vector operations: x.rgb instead of .r, .g, .b separately",
                    "Vector operations are more efficient than scalar",
                    20.0
                ))
            }
            
            // Optimize texture sampling with constant coordinates
            if (line.contains(".sample(") && line.contains("float2(")) {
                optimizations.add(Optimization(
                    lineNum,
                    line.trim(),
                    "// Consider: precalculate texture coordinates if constant",
                    "Move constant calculations outside loops",
                    5.0
                ))
            }
        }
        
        return optimizations
    }
    
    fun applyOptimizations(shaderCode: String, optimizations: List<Optimization>): String {
        var optimized = shaderCode
        val lines = optimized.lines().toMutableList()
        
        // Apply optimizations from bottom to top to preserve line numbers
        optimizations.sortedByDescending { it.line }.forEach { opt ->
            if (opt.line - 1 < lines.size) {
                lines[opt.line - 1] = opt.optimized
            }
        }
        
        return lines.joinToString("\n")
    }
    
    fun formatOptimizations(optimizations: List<Optimization>): String {
        val sb = StringBuilder()
        sb.append("=== Metal Shader Optimizations ===\n\n")
        
        if (optimizations.isEmpty()) {
            sb.append("✅ No optimizations found - shader is already well optimized!\n")
            return sb.toString()
        }
        
        sb.append("Found ${optimizations.size} optimization(s):\n\n")
        
        val totalSpeedup = optimizations.sumOf { it.estimatedSpeedup } / optimizations.size
        sb.append("Estimated total speedup: ${"%.1f".format(totalSpeedup)}%\n\n")
        
        optimizations.sortedByDescending { it.estimatedSpeedup }.forEach { opt ->
            sb.append("─────────────────────────────────────\n")
            sb.append("Line ${opt.line} [+${"%.0f".format(opt.estimatedSpeedup)}% faster]\n")
            sb.append("Reason: ${opt.reason}\n\n")
            sb.append("Original:\n  ${opt.original}\n\n")
            sb.append("Optimized:\n  ${opt.optimized}\n\n")
        }
        
        return sb.toString()
    }
}

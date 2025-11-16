package com.jakubjirak.metalshader.analysis

import com.intellij.psi.PsiFile
import com.jakubjirak.metalshader.language.MetalFile

class MetalShaderComplexityAnalyzer {
    
    data class ComplexityReport(
        val instructionCount: Int,
        val textureReads: Int,
        val textureWrites: Int,
        val mathOperations: Int,
        val branchCount: Int,
        val loopCount: Int,
        val threadgroupSyncCount: Int,
        val estimatedCyclesMin: Int,
        val estimatedCyclesMax: Int,
        val complexityLevel: ComplexityLevel,
        val warnings: List<String>
    )
    
    enum class ComplexityLevel {
        LOW,      // < 50 cycles
        MEDIUM,   // 50-200 cycles
        HIGH,     // 200-500 cycles
        VERY_HIGH // > 500 cycles
    }
    
    fun analyze(file: PsiFile): ComplexityReport? {
        if (file !is MetalFile) return null
        
        val text = file.text
        val warnings = mutableListOf<String>()
        
        // Count operations
        val textureReads = text.split(".read(").size - 1
        val textureWrites = text.split(".write(").size - 1
        val sampleOps = text.split(".sample(").size - 1
        
        // Math operations
        val additions = text.count { it == '+' }
        val multiplications = text.count { it == '*' }
        val divisions = text.count { it == '/' }
        val mathFuncs = countMathFunctions(text)
        
        val mathOperations = additions + multiplications + divisions * 4 + mathFuncs * 8
        
        // Control flow
        val branchCount = countOccurrences(text, listOf("if", "switch"))
        val loopCount = countOccurrences(text, listOf("for", "while", "do"))
        
        // Synchronization
        val threadgroupSyncCount = countOccurrences(text, listOf("threadgroup_barrier", "simdgroup_barrier"))
        
        // Estimate cycles
        val baseInstructions = mathOperations + branchCount * 2 + loopCount * 10
        val textureLatency = (textureReads + sampleOps) * 100 + textureWrites * 20
        val syncLatency = threadgroupSyncCount * 50
        
        val estimatedCyclesMin = baseInstructions + textureLatency / 4 + syncLatency
        val estimatedCyclesMax = baseInstructions * 2 + textureLatency + syncLatency * 2 + loopCount * 100
        
        // Complexity level
        val complexityLevel = when {
            estimatedCyclesMax < 50 -> ComplexityLevel.LOW
            estimatedCyclesMax < 200 -> ComplexityLevel.MEDIUM
            estimatedCyclesMax < 500 -> ComplexityLevel.HIGH
            else -> ComplexityLevel.VERY_HIGH
        }
        
        // Generate warnings
        if (textureReads > 8) {
            warnings.add("High texture read count ($textureReads) - consider caching")
        }
        
        if (divisions > 5) {
            warnings.add("Many division operations ($divisions) - consider using multiplication by reciprocal")
        }
        
        if (mathFuncs > 10) {
            warnings.add("Many expensive math functions ($mathFuncs) - consider approximations")
        }
        
        if (loopCount > 3 && textureReads > 0) {
            warnings.add("Texture reads in loops - high memory bandwidth usage")
        }
        
        if (branchCount > 10) {
            warnings.add("High branch count ($branchCount) - potential thread divergence")
        }
        
        if (threadgroupSyncCount > 5) {
            warnings.add("Frequent synchronization ($threadgroupSyncCount) - may limit performance")
        }
        
        return ComplexityReport(
            instructionCount = baseInstructions,
            textureReads = textureReads + sampleOps,
            textureWrites = textureWrites,
            mathOperations = mathOperations,
            branchCount = branchCount,
            loopCount = loopCount,
            threadgroupSyncCount = threadgroupSyncCount,
            estimatedCyclesMin = estimatedCyclesMin,
            estimatedCyclesMax = estimatedCyclesMax,
            complexityLevel = complexityLevel,
            warnings = warnings
        )
    }
    
    private fun countMathFunctions(text: String): Int {
        val mathFunctions = listOf(
            "sin", "cos", "tan", "asin", "acos", "atan", "atan2",
            "sinh", "cosh", "tanh", "asinh", "acosh", "atanh",
            "exp", "exp2", "log", "log2", "log10",
            "pow", "sqrt", "rsqrt", "cbrt",
            "ceil", "floor", "round", "trunc", "fract",
            "fma", "fmod", "modf", "frexp", "ldexp"
        )
        
        return mathFunctions.sumOf { func ->
            text.split("$func(").size - 1
        }
    }
    
    private fun countOccurrences(text: String, keywords: List<String>): Int {
        return keywords.sumOf { keyword ->
            Regex("""\b$keyword\b""").findAll(text).count()
        }
    }
    
    fun generateOptimizationSuggestions(report: ComplexityReport): List<String> {
        val suggestions = mutableListOf<String>()
        
        when (report.complexityLevel) {
            ComplexityLevel.LOW -> {
                suggestions.add("✅ Shader complexity is optimal")
            }
            ComplexityLevel.MEDIUM -> {
                suggestions.add("⚠️ Moderate complexity - monitor performance")
                if (report.textureReads > 4) {
                    suggestions.add("Consider reducing texture reads through caching")
                }
            }
            ComplexityLevel.HIGH -> {
                suggestions.add("⚠️ High complexity - optimization recommended")
                suggestions.add("Profile on target hardware")
                if (report.mathOperations > 50) {
                    suggestions.add("Consider using lookup tables for expensive math")
                }
                if (report.branchCount > 5) {
                    suggestions.add("Reduce branches or use predication")
                }
            }
            ComplexityLevel.VERY_HIGH -> {
                suggestions.add("🔴 Very high complexity - significant optimization needed")
                suggestions.add("Consider splitting into multiple passes")
                suggestions.add("Use compute shaders for heavy workloads")
                if (report.loopCount > 2) {
                    suggestions.add("Unroll loops or reduce iterations")
                }
            }
        }
        
        // Specific suggestions based on operations
        if (report.textureReads > report.textureWrites * 3) {
            suggestions.add("Read-heavy workload - ensure texture cache efficiency")
        }
        
        if (report.threadgroupSyncCount > 0) {
            suggestions.add("Minimize threadgroup_barrier() calls - they're expensive")
        }
        
        return suggestions
    }
}

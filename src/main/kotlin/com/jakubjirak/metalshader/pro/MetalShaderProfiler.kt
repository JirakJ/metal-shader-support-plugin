package com.jakubjirak.metalshader.pro


/**
 * Shader profiling and GPU timing estimation
 */
class MetalShaderProfiler {
    
    data class ProfileResult(
        val estimatedGPUCycles: Long,
        val estimatedLatency: Double, // microseconds
        val bottlenecks: List<Bottleneck>,
        val recommendations: List<String>
    )
    
    data class Bottleneck(
        val type: BottleneckType,
        val location: String,
        val impact: Impact,
        val description: String
    )
    
    enum class BottleneckType {
        MEMORY_BANDWIDTH,
        ALU_BOUND,
        TEXTURE_SAMPLING,
        THREAD_DIVERGENCE,
        REGISTER_PRESSURE,
        OCCUPANCY
    }
    
    enum class Impact {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    fun profileShader(shaderCode: String): ProfileResult? {
        var cycles = 0L
        val bottlenecks = mutableListOf<Bottleneck>()
        val recommendations = mutableListOf<String>()
        
        // Memory operations
        val textureReads = countPattern(shaderCode, "\\.(read|sample)\\(")
        cycles += textureReads * 400 // ~400 cycles per texture read
        
        if (textureReads > 10) {
            bottlenecks.add(Bottleneck(
                BottleneckType.TEXTURE_SAMPLING,
                "Multiple texture operations",
                Impact.HIGH,
                "Found $textureReads texture operations - consider texture caching"
            ))
            recommendations.add("Use texture prefetching: precalculate texture coordinates")
            recommendations.add("Consider texture atlasing to reduce texture switches")
        }
        
        // ALU operations
        val muls = countPattern(shaderCode, "\\*")
        val divs = countPattern(shaderCode, "/")
        val sqrts = countPattern(shaderCode, "\\bsqrt\\b")
        val pows = countPattern(shaderCode, "\\bpow\\b")
        
        cycles += muls * 4 + divs * 20 + sqrts * 30 + pows * 50
        
        if (divs > 5) {
            bottlenecks.add(Bottleneck(
                BottleneckType.ALU_BOUND,
                "Division operations",
                Impact.MEDIUM,
                "Found $divs divisions - expensive on GPU"
            ))
            recommendations.add("Replace divisions with multiplications where possible")
        }
        
        if (sqrts > 3) {
            bottlenecks.add(Bottleneck(
                BottleneckType.ALU_BOUND,
                "Square root operations",
                Impact.MEDIUM,
                "Found $sqrts sqrt() calls"
            ))
            recommendations.add("Use rsqrt() instead of 1.0/sqrt() when possible")
        }
        
        if (pows > 2) {
            bottlenecks.add(Bottleneck(
                BottleneckType.ALU_BOUND,
                "Power operations",
                Impact.HIGH,
                "Found $pows pow() calls - very expensive"
            ))
            recommendations.add("Replace pow(x, 2.0) with x*x")
            recommendations.add("Replace pow(x, 0.5) with sqrt(x)")
        }
        
        // Thread divergence
        val ifStatements = countPattern(shaderCode, "\\bif\\b")
        if (ifStatements > 5) {
            bottlenecks.add(Bottleneck(
                BottleneckType.THREAD_DIVERGENCE,
                "Conditional branches",
                Impact.HIGH,
                "Found $ifStatements if statements - causes thread divergence"
            ))
            recommendations.add("Consider branchless programming with mix/select")
            recommendations.add("Organize threads to minimize divergence")
        }
        
        // Memory bandwidth
        val bufferAccesses = countPattern(shaderCode, "\\[\\w+\\]")
        if (bufferAccesses > 20) {
            bottlenecks.add(Bottleneck(
                BottleneckType.MEMORY_BANDWIDTH,
                "Buffer accesses",
                Impact.MEDIUM,
                "Found $bufferAccesses buffer accesses"
            ))
            recommendations.add("Use threadgroup memory for frequently accessed data")
            recommendations.add("Ensure coalesced memory access patterns")
        }
        
        // Base latency + instruction count
        val totalInstructions = muls + divs + sqrts + pows + textureReads + ifStatements
        cycles += totalInstructions * 2
        
        // Estimate latency (assuming 1.5 GHz GPU clock)
        val latencyMicros = (cycles.toDouble() / 1500.0)
        
        if (recommendations.isEmpty()) {
            recommendations.add("Shader is well optimized!")
        }
        
        return ProfileResult(
            estimatedGPUCycles = cycles,
            estimatedLatency = latencyMicros,
            bottlenecks = bottlenecks,
            recommendations = recommendations
        )
    }
    
    private fun countPattern(code: String, pattern: String): Int {
        return Regex(pattern).findAll(code).count()
    }
    
    fun formatProfileResult(result: ProfileResult): String {
        val sb = StringBuilder()
        sb.append("=== Metal Shader Profile ===\n\n")
        sb.append("Estimated GPU Cycles: ${result.estimatedGPUCycles}\n")
        sb.append("Estimated Latency: ${"%.2f".format(result.estimatedLatency)} μs\n\n")
        
        if (result.bottlenecks.isNotEmpty()) {
            sb.append("Bottlenecks Found:\n")
            result.bottlenecks.forEachIndexed { idx, bottleneck ->
                sb.append("${idx + 1}. [${bottleneck.impact}] ${bottleneck.type}\n")
                sb.append("   ${bottleneck.description}\n\n")
            }
        }
        
        sb.append("Recommendations:\n")
        result.recommendations.forEachIndexed { idx, rec ->
            sb.append("${idx + 1}. $rec\n")
        }
        
        return sb.toString()
    }
}

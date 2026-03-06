package com.jakubjirak.metalshader.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.jakubjirak.metalshader.language.MetalFile

class MetalPerformanceInspection : LocalInspectionTool() {
    
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitElement(element: PsiElement) {
                super.visitElement(element)
                
                if (element.containingFile !is MetalFile) return
                
                val text = element.text
                
                // Check for common performance issues
                checkDivisionByConstant(element, text, holder)
                checkBranchDivergence(element, text, holder)
                checkTextureAccess(element, text, holder)
                checkThreadgroupMemory(element, text, holder)
                checkVectorization(element, text, holder)
                checkMathFunctions(element, text, holder)
            }
        }
    }
    
    private fun checkDivisionByConstant(element: PsiElement, text: String, holder: ProblemsHolder) {
        // Division by power of 2 should use shift
        val divPattern = Regex("""/\s*(2|4|8|16|32|64|128|256)\b""")
        if (divPattern.containsMatchIn(text)) {
            holder.registerProblem(
                element,
                "Division by power of 2 can be optimized with right shift (>>)",
                com.intellij.codeInspection.ProblemHighlightType.WEAK_WARNING
            )
        }
    }
    
    private fun checkBranchDivergence(element: PsiElement, text: String, holder: ProblemsHolder) {
        // Warn about potential thread divergence in compute kernels
        if (text.contains("kernel") && text.contains("if") && 
            (text.contains("thread_position_in_grid") || text.contains("gid"))) {
            
            // Check if condition depends on thread ID
            if (text.contains("gid.x") || text.contains("gid.y") || text.contains("tid")) {
                holder.registerProblem(
                    element,
                    "Branch depends on thread ID - potential thread divergence. Consider predication.",
                    com.intellij.codeInspection.ProblemHighlightType.WEAK_WARNING
                )
            }
        }
    }
    
    private fun checkTextureAccess(element: PsiElement, text: String, holder: ProblemsHolder) {
        // Multiple reads from same texture coordinate
        if (text.contains(".read(") && text.count { it == '(' } > 1) {
            val coordPattern = Regex("""\.read\(([^)]+)\)""")
            val matches = coordPattern.findAll(text).toList()
            if (matches.size > 1) {
                val coords = matches.map { it.groupValues[1] }
                if (coords.distinct().size < coords.size) {
                    holder.registerProblem(
                        element,
                        "Multiple texture reads with same coordinates - cache the result",
                        com.intellij.codeInspection.ProblemHighlightType.WARNING
                    )
                }
            }
        }
    }
    
    private fun checkThreadgroupMemory(element: PsiElement, text: String, holder: ProblemsHolder) {
        // Large threadgroup memory allocation
        if (text.contains("threadgroup") && text.contains("[")) {
            val sizePattern = Regex("""threadgroup\s+\w+\s+\w+\[(\d+)\]""")
            sizePattern.find(text)?.let { match ->
                val size = match.groupValues[1].toIntOrNull()
                if (size != null && size > 16384) { // 16KB warning
                    holder.registerProblem(
                        element,
                        "Large threadgroup memory allocation ($size bytes) - may limit occupancy",
                        com.intellij.codeInspection.ProblemHighlightType.WARNING
                    )
                }
            }
        }
    }
    
    private fun checkVectorization(element: PsiElement, text: String, holder: ProblemsHolder) {
        // Scalar operations that could be vectorized
        if (text.contains("float ") && !text.contains("float2") && 
            !text.contains("float3") && !text.contains("float4")) {
            
            // Count scalar float operations
            val scalarOps = text.count { it == '+' || it == '-' || it == '*' || it == '/' }
            if (scalarOps >= 4) {
                holder.registerProblem(
                    element,
                    "Multiple scalar operations - consider vectorization (float4)",
                    com.intellij.codeInspection.ProblemHighlightType.WEAK_WARNING
                )
            }
        }
    }
    
    private fun checkMathFunctions(element: PsiElement, text: String, holder: ProblemsHolder) {
        // Expensive math functions
        val expensiveFuncs = listOf("pow", "exp", "log", "sin", "cos", "tan", "sqrt")
        
        for (func in expensiveFuncs) {
            if (text.contains("$func(")) {
                // Check if in hot loop
                if (text.contains("for") || text.contains("while")) {
                    holder.registerProblem(
                        element,
                        "'$func()' in loop - consider fast math alternatives or precomputation",
                        com.intellij.codeInspection.ProblemHighlightType.WEAK_WARNING
                    )
                }
                
                // Check for specific optimizations
                when (func) {
                    "pow" -> {
                        if (text.contains("pow(") && text.contains(", 2")) {
                            holder.registerProblem(
                                element,
                                "pow(x, 2) can be replaced with x * x",
                                com.intellij.codeInspection.ProblemHighlightType.WEAK_WARNING
                            )
                        }
                    }
                    "sqrt" -> {
                        if (text.contains("1.0 / sqrt(")) {
                            holder.registerProblem(
                                element,
                                "Use rsqrt() instead of 1.0/sqrt() for better performance",
                                com.intellij.codeInspection.ProblemHighlightType.WARNING
                            )
                        }
                    }
                }
            }
        }
    }
}

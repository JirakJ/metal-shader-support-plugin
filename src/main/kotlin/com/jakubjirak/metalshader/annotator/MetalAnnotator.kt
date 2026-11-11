package com.jakubjirak.metalshader.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.jakubjirak.metalshader.language.MetalFile

class MetalAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element.containingFile !is MetalFile) return

        val text = element.text

        // Check for common Metal errors
        checkThreadgroupSize(text, element, holder)
        checkTextureAccess(text, element, holder)
        checkAttributeFormat(text, element, holder)
        checkVectorSwizzle(text, element, holder)
    }

    private fun checkThreadgroupSize(text: String, element: PsiElement, holder: AnnotationHolder) {
        // Check for threadgroup size > 1024
        val threadgroupPattern = Regex("""threads_per_threadgroup\s*\(\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)""")
        val match = threadgroupPattern.find(text)
        
        if (match != null) {
            val x = match.groupValues[1].toIntOrNull() ?: 1
            val y = match.groupValues[2].toIntOrNull() ?: 1
            val z = match.groupValues[3].toIntOrNull() ?: 1
            val total = x * y * z

            if (total > 1024) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Threadgroup size ($total) exceeds maximum limit (1024)"
                ).range(element).create()
            }
        }
    }

    private fun checkTextureAccess(text: String, element: PsiElement, holder: AnnotationHolder) {
        // Warn about texture access without bounds checking
        val readsTexture = text.contains("texture") && text.contains(".read(")
        val queriesSize = text.contains("get_width") || text.contains("get_height")
        if (readsTexture && !queriesSize) {
            
            if (element.parent?.text?.contains("if") != true) {
                holder.newAnnotation(
                    HighlightSeverity.WEAK_WARNING,
                    "Consider adding bounds checking before texture access"
                ).range(element).create()
            }
        }
    }

    private fun checkAttributeFormat(text: String, element: PsiElement, holder: AnnotationHolder) {
        // Check for malformed attributes
        if (text.startsWith("[[") && !text.endsWith("]]")) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Unclosed attribute declaration"
            ).range(element).create()
        }

        // Check for duplicate buffer/texture bindings within each function scope
        val duplicates = findDuplicateBindings(text)
        for (dup in duplicates) {
            holder.newAnnotation(HighlightSeverity.WARNING, dup).range(element).create()
        }
    }

    private fun checkVectorSwizzle(text: String, element: PsiElement, holder: AnnotationHolder) {
        // Check for invalid vector swizzle operations
        val swizzlePattern = Regex("""\.\s*([xyzwrgba]+)\s*""")
        val match = swizzlePattern.find(text)

        if (match != null) {
            val swizzle = match.groupValues[1]
            
            // Check for mixing rgba with xyzw
            val hasXyzw = swizzle.any { it in "xyzw" }
            val hasRgba = swizzle.any { it in "rgba" }
            
            if (hasXyzw && hasRgba) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Cannot mix xyzw and rgba swizzle patterns"
                ).range(element).create()
            }

            // Check for valid swizzle length (max 4)
            if (swizzle.length > 4) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Vector swizzle cannot exceed 4 components"
                ).range(element).create()
            }
        }
    }

    companion object {
        private val BUFFER_PATTERN = Regex("""\[\[buffer\((\d+)\)\]\]""")
        private val TEXTURE_PATTERN = Regex("""\[\[texture\((\d+)\)\]\]""")
        private val FUNCTION_START = Regex("""(?:kernel|vertex|fragment)\s+[\w<>]+\s+\w+\s*\(""")

        /**
         * Finds duplicate buffer/texture binding indices scoped per shader function.
         *
         * In Metal, binding indices are scoped per entry-point function (kernel, vertex,
         * fragment), not globally. Reusing the same index across different functions is
         * valid and standard practice per the Metal Shading Language specification.
         *
         * @return list of warning messages for functions with duplicate bindings
         */
        fun findDuplicateBindings(text: String): List<String> {
            val warnings = mutableListOf<String>()

            val functionBodies = extractFunctionSignatures(text)
            for (funcSig in functionBodies) {
                val bufferBindings = BUFFER_PATTERN.findAll(funcSig).map { it.groupValues[1] }.toList()
                val textureBindings = TEXTURE_PATTERN.findAll(funcSig).map { it.groupValues[1] }.toList()

                if (bufferBindings.size != bufferBindings.distinct().size) {
                    warnings.add("Duplicate buffer binding indices detected")
                }

                if (textureBindings.size != textureBindings.distinct().size) {
                    warnings.add("Duplicate texture binding indices detected")
                }
            }

            return warnings
        }

        /**
         * Extracts the full parameter list of each shader function, handling nested
         * parentheses from attribute annotations like [[buffer(0)]].
         */
        private fun extractFunctionSignatures(text: String): List<String> {
            val signatures = mutableListOf<String>()

            for (match in FUNCTION_START.findAll(text)) {
                val startIdx = match.range.last + 1
                var depth = 1
                var i = startIdx
                while (i < text.length && depth > 0) {
                    when (text[i]) {
                        '(' -> depth++
                        ')' -> depth--
                    }
                    i++
                }
                if (depth == 0) {
                    signatures.add(text.substring(match.range.first, i))
                }
            }

            return signatures
        }
    }
}

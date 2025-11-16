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
        if (text.contains("texture") && text.contains(".read(") && 
            !text.contains("get_width") && !text.contains("get_height")) {
            
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

        // Check for duplicate buffer/texture bindings in same scope
        val bufferPattern = Regex("""\[\[buffer\((\d+)\)\]\]""")
        val texturePattern = Regex("""\[\[texture\((\d+)\)\]\]""")
        
        val bufferBindings = bufferPattern.findAll(text).map { it.groupValues[1] }.toList()
        val textureBindings = texturePattern.findAll(text).map { it.groupValues[1] }.toList()

        if (bufferBindings.size != bufferBindings.distinct().size) {
            holder.newAnnotation(
                HighlightSeverity.WARNING,
                "Duplicate buffer binding indices detected"
            ).range(element).create()
        }

        if (textureBindings.size != textureBindings.distinct().size) {
            holder.newAnnotation(
                HighlightSeverity.WARNING,
                "Duplicate texture binding indices detected"
            ).range(element).create()
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
}

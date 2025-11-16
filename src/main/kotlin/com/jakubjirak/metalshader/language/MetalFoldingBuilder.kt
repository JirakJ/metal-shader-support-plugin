package com.jakubjirak.metalshader.language

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class MetalFoldingBuilder : FoldingBuilderEx() {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = mutableListOf<FoldingDescriptor>()
        
        // Fold block comments
        collectCommentFolds(root, descriptors)
        
        // Fold braces
        collectBraceFolds(root, descriptors)
        
        return descriptors.toTypedArray()
    }
    
    private fun collectCommentFolds(element: PsiElement, descriptors: MutableList<FoldingDescriptor>) {
        val text = element.text
        if (text.startsWith("/*") && text.endsWith("*/") && text.contains("\n")) {
            descriptors.add(FoldingDescriptor(element.node, element.textRange))
        }
        
        element.children.forEach { collectCommentFolds(it, descriptors) }
    }
    
    private fun collectBraceFolds(element: PsiElement, descriptors: MutableList<FoldingDescriptor>) {
        val text = element.text
        
        // Fold function bodies
        if (text.contains("{") && text.contains("}")) {
            val startOffset = text.indexOf('{')
            val endOffset = text.lastIndexOf('}')
            
            if (startOffset >= 0 && endOffset > startOffset) {
                val range = TextRange(
                    element.textRange.startOffset + startOffset,
                    element.textRange.startOffset + endOffset + 1
                )
                if (range.length > 10) { // Only fold if substantial
                    descriptors.add(FoldingDescriptor(element.node, range))
                }
            }
        }
        
        element.children.forEach { collectBraceFolds(it, descriptors) }
    }

    override fun getPlaceholderText(node: ASTNode): String {
        val text = node.text
        return when {
            text.startsWith("/*") -> "/* ... */"
            text.contains("kernel") -> "{ kernel ... }"
            text.contains("vertex") -> "{ vertex ... }"
            text.contains("fragment") -> "{ fragment ... }"
            else -> "{ ... }"
        }
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false
}

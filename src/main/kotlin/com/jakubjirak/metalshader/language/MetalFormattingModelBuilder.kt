package com.jakubjirak.metalshader.language

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings

class MetalFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val settings = formattingContext.codeStyleSettings
        val element = formattingContext.psiElement
        
        return FormattingModelProvider.createFormattingModelForPsiFile(
            element.containingFile,
            MetalBlock(element.node, Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment(), settings),
            settings
        )
    }
}

class MetalBlock(
    private val node: ASTNode,
    private val wrap: Wrap?,
    private val alignment: Alignment?,
    private val settings: CodeStyleSettings
) : ASTBlock {
    
    override fun getNode(): ASTNode = node
    override fun getTextRange() = node.textRange
    override fun getWrap(): Wrap? = wrap
    override fun getIndent(): Indent? = Indent.getNoneIndent()
    override fun getAlignment(): Alignment? = alignment
    override fun getSpacing(child1: Block?, child2: Block): Spacing? = null
    override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
        return ChildAttributes(Indent.getNormalIndent(), null)
    }
    override fun isIncomplete(): Boolean = false
    override fun isLeaf(): Boolean = node.firstChildNode == null
    
    override fun getSubBlocks(): List<Block> {
        val blocks = mutableListOf<Block>()
        var child = node.firstChildNode
        
        while (child != null) {
            if (child.textRange.length > 0) {
                blocks.add(
                    MetalBlock(
                        child,
                        Wrap.createWrap(WrapType.NONE, false),
                        alignment,
                        settings
                    )
                )
            }
            child = child.treeNext
        }
        
        return blocks
    }
}

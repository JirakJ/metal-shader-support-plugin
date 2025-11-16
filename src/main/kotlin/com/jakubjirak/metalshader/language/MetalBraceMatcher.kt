package com.jakubjirak.metalshader.language

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

class MetalBraceMatcher : PairedBraceMatcher {
    override fun getPairs() = arrayOf(
        BracePair(MetalTokenTypes.BRACKET, MetalTokenTypes.BRACKET, true),
        BracePair(MetalTokenTypes.BRACKET, MetalTokenTypes.BRACKET, true),
        BracePair(MetalTokenTypes.BRACKET, MetalTokenTypes.BRACKET, true)
    )

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true

    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int) = openingBraceOffset
}

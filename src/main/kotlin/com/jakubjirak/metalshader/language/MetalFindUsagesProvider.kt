package com.jakubjirak.metalshader.language

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.cacheBuilder.WordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet

class MetalFindUsagesProvider : FindUsagesProvider {
    override fun getWordsScanner(): WordsScanner {
        return DefaultWordsScanner(
            MetalLexer(),
            TokenSet.create(MetalTokenTypes.IDENTIFIER),
            MetalParserDefinition.COMMENTS,
            MetalParserDefinition.STRINGS
        )
    }

    override fun canFindUsagesFor(psiElement: PsiElement): Boolean {
        return psiElement.text.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*"))
    }

    override fun getHelpId(psiElement: PsiElement): String? = null

    override fun getType(element: PsiElement): String {
        val text = element.text
        return when {
            text in MetalKeywords.KEYWORDS -> "keyword"
            text in MetalKeywords.TYPES -> "type"
            text in MetalKeywords.BUILT_IN_FUNCTIONS -> "function"
            else -> "identifier"
        }
    }

    override fun getDescriptiveName(element: PsiElement): String {
        return element.text
    }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String {
        return element.text
    }
}

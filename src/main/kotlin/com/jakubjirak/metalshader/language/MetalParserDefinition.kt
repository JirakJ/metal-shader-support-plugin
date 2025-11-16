package com.jakubjirak.metalshader.language

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class MetalParserDefinition : ParserDefinition {
    override fun createLexer(project: Project?): Lexer = MetalLexer()

    override fun createParser(project: Project?): PsiParser = MetalParser()

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getCommentTokens(): TokenSet = COMMENTS

    override fun getStringLiteralElements(): TokenSet = STRINGS

    override fun createElement(node: ASTNode?): PsiElement = MetalPsiElement(node!!)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = MetalFile(viewProvider)

    companion object {
        val FILE = IFileElementType(MetalLanguage)
        val COMMENTS = TokenSet.create(MetalTokenTypes.LINE_COMMENT, MetalTokenTypes.BLOCK_COMMENT)
        val STRINGS = TokenSet.create(MetalTokenTypes.STRING)
    }
}

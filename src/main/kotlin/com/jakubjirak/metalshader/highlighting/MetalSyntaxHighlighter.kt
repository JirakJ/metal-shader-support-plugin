package com.jakubjirak.metalshader.highlighting

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType
import com.jakubjirak.metalshader.language.MetalLexer
import com.jakubjirak.metalshader.language.MetalTokenTypes

class MetalSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = MetalLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            MetalTokenTypes.KEYWORD -> KEYWORD_KEYS
            MetalTokenTypes.TYPE -> TYPE_KEYS
            MetalTokenTypes.QUALIFIER -> QUALIFIER_KEYS
            MetalTokenTypes.NUMBER -> NUMBER_KEYS
            MetalTokenTypes.STRING -> STRING_KEYS
            MetalTokenTypes.LINE_COMMENT, MetalTokenTypes.BLOCK_COMMENT -> COMMENT_KEYS
            MetalTokenTypes.OPERATOR -> OPERATOR_KEYS
            MetalTokenTypes.BRACKET -> BRACKET_KEYS
            MetalTokenTypes.SEMICOLON -> SEMICOLON_KEYS
            MetalTokenTypes.ATTRIBUTE -> ATTRIBUTE_KEYS
            MetalTokenTypes.BUILT_IN -> BUILT_IN_KEYS
            MetalTokenTypes.BAD_CHARACTER -> BAD_CHARACTER_KEYS
            else -> EMPTY_KEYS
        }
    }

    companion object {
        val KEYWORD = TextAttributesKey.createTextAttributesKey(
            "METAL_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD
        )
        val TYPE = TextAttributesKey.createTextAttributesKey(
            "METAL_TYPE", DefaultLanguageHighlighterColors.CLASS_NAME
        )
        val QUALIFIER = TextAttributesKey.createTextAttributesKey(
            "METAL_QUALIFIER", DefaultLanguageHighlighterColors.METADATA
        )
        val NUMBER = TextAttributesKey.createTextAttributesKey(
            "METAL_NUMBER", DefaultLanguageHighlighterColors.NUMBER
        )
        val STRING = TextAttributesKey.createTextAttributesKey(
            "METAL_STRING", DefaultLanguageHighlighterColors.STRING
        )
        val COMMENT = TextAttributesKey.createTextAttributesKey(
            "METAL_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT
        )
        val OPERATOR = TextAttributesKey.createTextAttributesKey(
            "METAL_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN
        )
        val BRACKET = TextAttributesKey.createTextAttributesKey(
            "METAL_BRACKET", DefaultLanguageHighlighterColors.BRACKETS
        )
        val SEMICOLON = TextAttributesKey.createTextAttributesKey(
            "METAL_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON
        )
        val ATTRIBUTE = TextAttributesKey.createTextAttributesKey(
            "METAL_ATTRIBUTE", DefaultLanguageHighlighterColors.METADATA
        )
        val BUILT_IN = TextAttributesKey.createTextAttributesKey(
            "METAL_BUILT_IN", DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL
        )
        val BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(
            "METAL_BAD_CHARACTER", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE
        )

        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val TYPE_KEYS = arrayOf(TYPE)
        private val QUALIFIER_KEYS = arrayOf(QUALIFIER)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        private val STRING_KEYS = arrayOf(STRING)
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val OPERATOR_KEYS = arrayOf(OPERATOR)
        private val BRACKET_KEYS = arrayOf(BRACKET)
        private val SEMICOLON_KEYS = arrayOf(SEMICOLON)
        private val ATTRIBUTE_KEYS = arrayOf(ATTRIBUTE)
        private val BUILT_IN_KEYS = arrayOf(BUILT_IN)
        private val BAD_CHARACTER_KEYS = arrayOf(BAD_CHARACTER)
        private val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }
}

package com.jakubjirak.metalshader.language

import com.intellij.psi.tree.IElementType
import com.intellij.psi.TokenType

object MetalTokenTypes {
    @JvmField val WHITE_SPACE = TokenType.WHITE_SPACE
    @JvmField val BAD_CHARACTER = TokenType.BAD_CHARACTER
    
    @JvmField val LINE_COMMENT = IElementType("LINE_COMMENT", MetalLanguage)
    @JvmField val BLOCK_COMMENT = IElementType("BLOCK_COMMENT", MetalLanguage)
    
    @JvmField val KEYWORD = IElementType("KEYWORD", MetalLanguage)
    @JvmField val TYPE = IElementType("TYPE", MetalLanguage)
    @JvmField val QUALIFIER = IElementType("QUALIFIER", MetalLanguage)
    @JvmField val IDENTIFIER = IElementType("IDENTIFIER", MetalLanguage)
    @JvmField val NUMBER = IElementType("NUMBER", MetalLanguage)
    @JvmField val STRING = IElementType("STRING", MetalLanguage)
    @JvmField val OPERATOR = IElementType("OPERATOR", MetalLanguage)
    @JvmField val BRACKET = IElementType("BRACKET", MetalLanguage)
    @JvmField val SEMICOLON = IElementType("SEMICOLON", MetalLanguage)
    @JvmField val ATTRIBUTE = IElementType("ATTRIBUTE", MetalLanguage)
    @JvmField val BUILT_IN = IElementType("BUILT_IN", MetalLanguage)
}

package com.jakubjirak.metalshader.language

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

class MetalLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var startOffset = 0
    private var endOffset = 0
    private var currentOffset = 0
    private var currentTokenType: IElementType? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.startOffset = startOffset
        this.endOffset = endOffset
        this.currentOffset = startOffset
        advance()
    }

    override fun getState() = 0

    override fun getTokenType() = currentTokenType

    override fun getTokenStart() = startOffset

    override fun getTokenEnd() = currentOffset

    override fun advance() {
        if (currentOffset >= endOffset) {
            currentTokenType = null
            return
        }

        startOffset = currentOffset
        val c = buffer[currentOffset]

        when {
            c.isWhitespace() -> {
                currentOffset++
                while (currentOffset < endOffset && buffer[currentOffset].isWhitespace()) {
                    currentOffset++
                }
                currentTokenType = MetalTokenTypes.WHITE_SPACE
            }
            c == '/' && currentOffset + 1 < endOffset -> {
                when (buffer[currentOffset + 1]) {
                    '/' -> {
                        currentOffset += 2
                        while (currentOffset < endOffset && buffer[currentOffset] != '\n') {
                            currentOffset++
                        }
                        currentTokenType = MetalTokenTypes.LINE_COMMENT
                    }
                    '*' -> {
                        currentOffset += 2
                        while (currentOffset + 1 < endOffset) {
                            if (buffer[currentOffset] == '*' && buffer[currentOffset + 1] == '/') {
                                currentOffset += 2
                                break
                            }
                            currentOffset++
                        }
                        currentTokenType = MetalTokenTypes.BLOCK_COMMENT
                    }
                    else -> {
                        currentOffset++
                        currentTokenType = MetalTokenTypes.OPERATOR
                    }
                }
            }
            c == '[' && currentOffset + 1 < endOffset && buffer[currentOffset + 1] == '[' -> {
                currentOffset += 2
                while (currentOffset + 1 < endOffset) {
                    if (buffer[currentOffset] == ']' && buffer[currentOffset + 1] == ']') {
                        currentOffset += 2
                        break
                    }
                    currentOffset++
                }
                currentTokenType = MetalTokenTypes.ATTRIBUTE
            }
            c == '"' -> {
                currentOffset++
                while (currentOffset < endOffset && buffer[currentOffset] != '"') {
                    if (buffer[currentOffset] == '\\') currentOffset++
                    currentOffset++
                }
                if (currentOffset < endOffset) currentOffset++
                currentTokenType = MetalTokenTypes.STRING
            }
            c.isDigit() -> {
                currentOffset++
                while (currentOffset < endOffset && (buffer[currentOffset].isDigit() || buffer[currentOffset] == '.')) {
                    currentOffset++
                }
                if (currentOffset < endOffset && buffer[currentOffset] in "fFhH") currentOffset++
                currentTokenType = MetalTokenTypes.NUMBER
            }
            c.isLetter() || c == '_' -> {
                currentOffset++
                while (currentOffset < endOffset && (buffer[currentOffset].isLetterOrDigit() || buffer[currentOffset] == '_')) {
                    currentOffset++
                }
                val text = buffer.subSequence(startOffset, currentOffset).toString()
                currentTokenType = when {
                    text in MetalKeywords.KEYWORDS -> MetalTokenTypes.KEYWORD
                    text in MetalKeywords.TYPES -> MetalTokenTypes.TYPE
                    text in MetalKeywords.QUALIFIERS -> MetalTokenTypes.QUALIFIER
                    text in MetalKeywords.BUILT_IN_FUNCTIONS -> MetalTokenTypes.BUILT_IN
                    else -> MetalTokenTypes.IDENTIFIER
                }
            }
            c in "{}()[]<>,.;:=+-*/%!&|^~?" -> {
                currentOffset++
                currentTokenType = when (c) {
                    '{', '}', '(', ')', '[', ']' -> MetalTokenTypes.BRACKET
                    ';' -> MetalTokenTypes.SEMICOLON
                    else -> MetalTokenTypes.OPERATOR
                }
            }
            else -> {
                currentOffset++
                currentTokenType = MetalTokenTypes.BAD_CHARACTER
            }
        }
    }

    override fun getBufferSequence() = buffer
    override fun getBufferEnd() = endOffset
}

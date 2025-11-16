package com.jakubjirak.metalshader.language

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler

class MetalQuoteHandler : SimpleTokenSetQuoteHandler(MetalTokenTypes.STRING)

package com.jakubjirak.metalshader.language

import com.intellij.lang.Language

object MetalLanguage : Language("Metal") {
    override fun getDisplayName() = "Metal Shading Language"
    override fun isCaseSensitive() = true
}

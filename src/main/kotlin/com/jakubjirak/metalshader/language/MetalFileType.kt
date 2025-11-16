package com.jakubjirak.metalshader.language

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object MetalFileType : LanguageFileType(MetalLanguage) {
    override fun getName() = "Metal"
    override fun getDescription() = "Metal Shading Language file"
    override fun getDefaultExtension() = "metal"
    override fun getIcon(): Icon? = MetalIcons.FILE
}

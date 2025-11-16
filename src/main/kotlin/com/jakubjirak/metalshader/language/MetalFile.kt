package com.jakubjirak.metalshader.language

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class MetalFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, MetalLanguage) {
    override fun getFileType(): FileType = MetalFileType
    override fun toString() = "Metal File"
}

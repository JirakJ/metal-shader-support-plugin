package com.jakubjirak.metalshader.language

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType

class MetalTemplateContextType : TemplateContextType("METAL") {
    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        return templateActionContext.file is MetalFile
    }
    
    override fun getPresentableName(): String = "Metal"
}

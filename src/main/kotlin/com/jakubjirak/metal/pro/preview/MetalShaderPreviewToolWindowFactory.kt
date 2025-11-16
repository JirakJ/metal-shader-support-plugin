// Copyright (c) 2025 Jakub Jirák. All rights reserved.
package com.jakubjirak.metal.pro.preview

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class MetalShaderPreviewToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val previewService = MetalShaderPreviewService.getInstance(project)
        val content = ContentFactory.getInstance().createContent(
            previewService.getPreviewPanel(),
            "",
            false
        )
        toolWindow.contentManager.addContent(content)
    }
}

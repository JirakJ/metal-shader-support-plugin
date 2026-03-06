package com.jakubjirak.metalshader.pro.debugger

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class MetalDebuggerToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val component = MetalDebuggerPanel(project)
        
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(component, "", false)
        toolWindow.contentManager.addContent(content)
    }
    
    override fun shouldBeAvailable(project: Project): Boolean {
        return true
    }
}

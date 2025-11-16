package com.jakubjirak.metalshader.pro.profiler

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.jakubjirak.metalshader.licensing.MetalLicenseManager
import javax.swing.JLabel
import javax.swing.SwingConstants

class MetalProfilerToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val licenseManager = MetalLicenseManager.getInstance()
        
        val component = if (licenseManager.hasProLicense()) {
            MetalProfilerPanel(project)
        } else {
            JLabel("Metal Profiler requires Pro or Enterprise license", SwingConstants.CENTER)
        }
        
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(component, "", false)
        toolWindow.contentManager.addContent(content)
    }
    
    override fun shouldBeAvailable(project: Project): Boolean {
        return true
    }
}

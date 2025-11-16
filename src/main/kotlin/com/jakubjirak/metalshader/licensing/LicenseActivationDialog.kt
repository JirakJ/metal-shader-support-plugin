package com.jakubjirak.metalshader.licensing

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class LicenseActivationDialog(
    private val licenseManager: MetalLicenseManager,
    project: Project?
) : DialogWrapper(project, true) {
    
    private val licenseKeyField = JBTextField(40)
    
    init {
        title = "Metal Shader Support - License Activation"
        init()
    }
    
    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout(10, 10))
        val state = licenseManager.getState()
        
        val infoPanel = JPanel(BorderLayout(0, 5))
        infoPanel.add(JBLabel("<html>" +
            "<h3>Metal Shader Support - License Activation</h3>" +
            "<p>Current tier: <b>${state.tier}</b> - ${licenseManager.getLicenseInfo()}</p>" +
            "<br>" +
            "<p>Enter your license key to unlock Pro features:</p>" +
            "<ul>" +
            "<li>✨ Shader Performance Profiler - GPU cycle estimation</li>" +
            "<li>🎯 Complexity Analyzer - bottleneck detection</li>" +
            "<li>🔍 Advanced Shader Validator - comprehensive checks</li>" +
            "<li>⚡ Automatic Optimizer - one-click optimizations</li>" +
            "<li>🐛 Debug Helper - texture visualizers</li>" +
            "<li>📧 Priority Support - direct email support</li>" +
            "</ul>" +
            "<br>" +
            "<p><b>Pricing:</b></p>" +
            "<ul>" +
            "<li>Pro Tier: $49/year - All features above</li>" +
            "<li>Enterprise Tier: $199/year - Team tools + transpiler</li>" +
            "</ul>" +
            "<br>" +
            "<p>Purchase license at: <b>https://jakubjirak.com</b></p>" +
            "<p>Contact: <b>support@jakubjirak.com</b></p>" +
            "</html>"), BorderLayout.NORTH)
        
        infoPanel.add(JBLabel("License Key:"), BorderLayout.CENTER)
        
        panel.add(infoPanel, BorderLayout.NORTH)
        panel.add(licenseKeyField, BorderLayout.CENTER)
        
        return panel
    }
    
    fun getLicenseKey(): String = licenseKeyField.text.trim()
}

package com.jakubjirak.metal.pro.preview

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.jakubjirak.metalshader.licensing.MetalLicenseManager
import javax.swing.*
import java.awt.BorderLayout

class ShaderPreviewToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()
        val previewPanel = ShaderPreviewPanel(project)
        val content = contentFactory.createContent(previewPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project): Boolean {
        val licenseManager = MetalLicenseManager.getInstance()
        return licenseManager.hasProFeatures() || licenseManager.hasEnterpriseFeatures()
    }
}

class ShaderPreviewPanel(private val project: Project) : JPanel(BorderLayout()) {
    
    init {
        val licenseManager = MetalLicenseManager.getInstance()
        
        if (licenseManager.hasProFeatures() || licenseManager.hasEnterpriseFeatures()) {
            setupPreviewUI()
        } else {
            showUpgradePrompt()
        }
    }
    
    private fun setupPreviewUI() {
        val previewArea = JTextArea()
        previewArea.text = """
            Metal Shader Live Preview (Pro Feature)
            
            This panel will show real-time rendering of your Metal shaders.
            
            Features:
            - Live preview at 60 FPS
            - Hot-reload on code changes
            - GPU performance metrics
            - Texture/buffer inspection
            
            Select a .metal file in the editor to start preview.
        """.trimIndent()
        previewArea.isEditable = false
        
        val scrollPane = JScrollPane(previewArea)
        add(scrollPane, BorderLayout.CENTER)
        
        val controlPanel = JPanel()
        val playButton = JButton("▶ Start Preview")
        val stopButton = JButton("⏹ Stop")
        stopButton.isEnabled = false
        
        controlPanel.add(playButton)
        controlPanel.add(stopButton)
        add(controlPanel, BorderLayout.SOUTH)
    }
    
    private fun showUpgradePrompt() {
        val panel = JPanel(BorderLayout())
        val textArea = JTextArea()
        textArea.text = """
            🔒 Metal Shader Live Preview
            
            This is a PRO feature ($49/year)
            
            Upgrade to unlock:
            ✓ Real-time shader preview
            ✓ GPU debugger integration
            ✓ Performance profiling
            ✓ Asset pipeline tools
            
            Use license key: METAL-PRO-XXXX-XXXX-XXXX
            Or visit: https://metal-shader-plugin.com
        """.trimIndent()
        textArea.isEditable = false
        panel.add(JScrollPane(textArea), BorderLayout.CENTER)
        
        val upgradeButton = JButton("Activate License")
        upgradeButton.addActionListener {
            val licenseKey = JOptionPane.showInputDialog(
                this,
                "Enter your Pro/Enterprise license key:",
                "Activate License",
                JOptionPane.QUESTION_MESSAGE
            )
            
            if (licenseKey != null && licenseKey.isNotBlank()) {
                val licenseManager = MetalLicenseManager.getInstance()
                if (licenseManager.validateAndActivateLicense(licenseKey)) {
                    JOptionPane.showMessageDialog(
                        this,
                        "License activated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    )
                    removeAll()
                    setupPreviewUI()
                    revalidate()
                    repaint()
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Invalid license key",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        }
        
        val buttonPanel = JPanel()
        buttonPanel.add(upgradeButton)
        panel.add(buttonPanel, BorderLayout.SOUTH)
        
        add(panel, BorderLayout.CENTER)
    }
}

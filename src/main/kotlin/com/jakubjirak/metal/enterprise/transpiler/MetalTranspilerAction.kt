package com.jakubjirak.metal.enterprise.transpiler

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.jakubjirak.metalshader.licensing.MetalLicenseManager
import javax.swing.*
import java.awt.BorderLayout
import java.awt.GridLayout

class MetalTranspilerAction : AnAction() {
    
    override fun update(e: AnActionEvent) {
        val licenseManager = MetalLicenseManager.getInstance()
        e.presentation.isEnabled = licenseManager.hasEnterpriseFeatures()
    }
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        
        if (file.extension != "metal") {
            Messages.showWarningDialog(
                project,
                "Please open a .metal file to transpile",
                "Metal Transpiler"
            )
            return
        }
        
        showTranspilerDialog(project, file.name, editor.document.text)
    }
    
    private fun showTranspilerDialog(
        project: com.intellij.openapi.project.Project,
        fileName: String,
        metalCode: String
    ) {
        val dialog = JDialog()
        dialog.title = "Metal Cross-Platform Transpiler (Enterprise)"
        dialog.setSize(1000, 700)
        dialog.setLocationRelativeTo(null)
        
        val mainPanel = JPanel(BorderLayout())
        
        val targetPanel = JPanel(GridLayout(1, 3, 10, 10))
        targetPanel.border = BorderFactory.createTitledBorder("Select Target Platform")
        
        val spirvButton = JRadioButton("SPIR-V (Vulkan)", true)
        val hlslButton = JRadioButton("HLSL (DirectX)")
        val wgslButton = JRadioButton("WGSL (WebGPU)")
        
        val group = ButtonGroup()
        group.add(spirvButton)
        group.add(hlslButton)
        group.add(wgslButton)
        
        targetPanel.add(spirvButton)
        targetPanel.add(hlslButton)
        targetPanel.add(wgslButton)
        
        mainPanel.add(targetPanel, BorderLayout.NORTH)
        
        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
        
        val inputArea = JTextArea(metalCode)
        inputArea.isEditable = false
        inputArea.border = BorderFactory.createTitledBorder("Metal Source")
        
        val outputArea = JTextArea()
        outputArea.isEditable = false
        outputArea.border = BorderFactory.createTitledBorder("Transpiled Output")
        
        splitPane.leftComponent = JScrollPane(inputArea)
        splitPane.rightComponent = JScrollPane(outputArea)
        splitPane.dividerLocation = 500
        
        mainPanel.add(splitPane, BorderLayout.CENTER)
        
        val buttonPanel = JPanel()
        val transpileButton = JButton("🔄 Transpile")
        val saveButton = JButton("💾 Save Output")
        val closeButton = JButton("Close")
        
        saveButton.isEnabled = false
        
        transpileButton.addActionListener {
            val target = when {
                spirvButton.isSelected -> "SPIR-V"
                hlslButton.isSelected -> "HLSL"
                else -> "WGSL"
            }
            
            outputArea.text = "// Transpiled to $target\n// Original file: $fileName\n\n[Transpilation output would appear here]"
            saveButton.isEnabled = true
        }
        
        closeButton.addActionListener { dialog.dispose() }
        
        buttonPanel.add(transpileButton)
        buttonPanel.add(saveButton)
        buttonPanel.add(closeButton)
        mainPanel.add(buttonPanel, BorderLayout.SOUTH)
        
        dialog.contentPane = mainPanel
        dialog.isVisible = true
    }
}

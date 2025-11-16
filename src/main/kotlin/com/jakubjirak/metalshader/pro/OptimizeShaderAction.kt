package com.jakubjirak.metalshader.pro

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages
import com.jakubjirak.metalshader.language.MetalFile
import com.jakubjirak.metalshader.licensing.MetalLicenseManager

class OptimizeShaderAction : AnAction("Optimize Shader") {
    
    override fun actionPerformed(e: AnActionEvent) {
        // Pro feature check
        if (!MetalLicenseManager.getInstance().isProFeatureEnabled()) {
            Messages.showInfoMessage(
                e.project,
                "Shader Optimizer is a Pro feature.\n\n" +
                        "Unlock with Pro license:\n" +
                        "• Automatic optimization suggestions\n" +
                        "• pow() → multiplication conversions\n" +
                        "• Division → multiplication optimizations\n" +
                        "• Float → half precision suggestions\n" +
                        "• One-click apply optimizations\n\n" +
                        "Contact: support@jakubjirak.com",
                "Metal Shader Support - Pro Feature"
            )
            return
        }
        
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.PSI_FILE) as? MetalFile ?: return
        
        val optimizer = MetalShaderOptimizer()
        val optimizations = optimizer.optimizeShader(file.text)
        
        if (optimizations.isNullOrEmpty()) {
            Messages.showMessageDialog(
                project,
                "✅ No optimizations found!\n\nYour shader is already well optimized.",
                "Metal Shader Optimizer",
                Messages.getInformationIcon()
            )
            return
        }
        
        val formatted = optimizer.formatOptimizations(optimizations)
        
        // Show optimizations
        val response = Messages.showYesNoDialog(
            project,
            "$formatted\nApply all ${optimizations.size} optimization(s)?",
            "Metal Shader Optimizer",
            "Apply All",
            "Show Only",
            Messages.getQuestionIcon()
        )
        
        if (response == Messages.YES) {
            // Apply optimizations
            WriteCommandAction.runWriteCommandAction(project) {
                val document = file.viewProvider.document
                if (document != null) {
                    val optimizedCode = optimizer.applyOptimizations(file.text, optimizations)
                    document.setText(optimizedCode)
                }
            }
            
            Messages.showMessageDialog(
                project,
                "✅ Applied ${optimizations.size} optimization(s) successfully!\n\n" +
                        "Estimated speedup: ${"%.1f".format(optimizations.sumOf { it.estimatedSpeedup } / optimizations.size)}%",
                "Optimizations Applied",
                Messages.getInformationIcon()
            )
        }
    }
    
    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE)
        val isMetalFile = file is MetalFile
        val isProEnabled = MetalLicenseManager.getInstance().isProFeatureEnabled()
        
        e.presentation.isEnabled = isMetalFile && isProEnabled
        e.presentation.isVisible = isMetalFile
    }
}

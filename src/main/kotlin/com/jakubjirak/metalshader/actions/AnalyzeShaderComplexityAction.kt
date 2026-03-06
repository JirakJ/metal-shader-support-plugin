package com.jakubjirak.metalshader.actions

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.jakubjirak.metalshader.analysis.MetalShaderComplexityAnalyzer
import com.jakubjirak.metalshader.language.MetalFile

class AnalyzeShaderComplexityAction : AnAction() {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.PSI_FILE) as? MetalFile ?: return
        
        val analyzer = MetalShaderComplexityAnalyzer()
        val report = analyzer.analyze(file) ?: return
        val suggestions = analyzer.generateOptimizationSuggestions(report)
        
        val message = buildString {
            appendLine("<h3>Metal Shader Complexity Report</h3>")
            appendLine("<b>File:</b> ${file.name}<br/>")
            appendLine("<br/>")
            
            appendLine("<b>Complexity Level:</b> ${report.complexityLevel}<br/>")
            appendLine("<b>Estimated Cycles:</b> ${report.estimatedCyclesMin} - ${report.estimatedCyclesMax}<br/>")
            appendLine("<br/>")
            
            appendLine("<b>Operations:</b><br/>")
            appendLine("• Texture Reads: ${report.textureReads}<br/>")
            appendLine("• Texture Writes: ${report.textureWrites}<br/>")
            appendLine("• Math Operations: ${report.mathOperations}<br/>")
            appendLine("• Branches: ${report.branchCount}<br/>")
            appendLine("• Loops: ${report.loopCount}<br/>")
            appendLine("• Synchronizations: ${report.threadgroupSyncCount}<br/>")
            appendLine("<br/>")
            
            if (report.warnings.isNotEmpty()) {
                appendLine("<b>⚠️ Warnings:</b><br/>")
                report.warnings.forEach {
                    appendLine("• $it<br/>")
                }
                appendLine("<br/>")
            }
            
            appendLine("<b>💡 Optimization Suggestions:</b><br/>")
            suggestions.forEach {
                appendLine("• $it<br/>")
            }
        }
        
        val notificationType = when (report.complexityLevel) {
            MetalShaderComplexityAnalyzer.ComplexityLevel.LOW -> NotificationType.INFORMATION
            MetalShaderComplexityAnalyzer.ComplexityLevel.MEDIUM -> NotificationType.INFORMATION
            MetalShaderComplexityAnalyzer.ComplexityLevel.HIGH -> NotificationType.WARNING
            MetalShaderComplexityAnalyzer.ComplexityLevel.VERY_HIGH -> NotificationType.ERROR
        }
        
        Notifications.Bus.notify(
            Notification(
                "Metal Shader",
                "Shader Complexity Analysis",
                message,
                notificationType
            ),
            project
        )
    }
    
    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE)
        val isMetalFile = file is MetalFile
        
        e.presentation.isEnabled = isMetalFile
        e.presentation.isVisible = isMetalFile
    }
}

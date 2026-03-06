package com.jakubjirak.metalshader.pro

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.jakubjirak.metalshader.language.MetalFile

class ProfileShaderAction : AnAction("Profile Shader Performance") {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.PSI_FILE) as? MetalFile ?: return
        
        val profiler = MetalShaderProfiler()
        val result = profiler.profileShader(file.text, project)
        
        if (result != null) {
            val formatted = profiler.formatProfileResult(result)
            
            // Show results in dialog
            Messages.showMessageDialog(
                project,
                formatted,
                "Metal Shader Profile Results",
                Messages.getInformationIcon()
            )
            
            // Also show notification with summary
            val impact = when {
                result.bottlenecks.any { it.impact == MetalShaderProfiler.Impact.CRITICAL } -> "CRITICAL"
                result.bottlenecks.any { it.impact == MetalShaderProfiler.Impact.HIGH } -> "HIGH"
                result.bottlenecks.any { it.impact == MetalShaderProfiler.Impact.MEDIUM } -> "MEDIUM"
                else -> "LOW"
            }
            
            Notifications.Bus.notify(
                Notification(
                    "Metal Shader Support",
                    "Shader Profile Complete",
                    "Found ${result.bottlenecks.size} bottleneck(s). Impact: $impact\n" +
                            "Estimated: ${result.estimatedGPUCycles} cycles, " +
                            "${"%.2f".format(result.estimatedLatency)} μs",
                    if (impact == "LOW") NotificationType.INFORMATION else NotificationType.WARNING
                ),
                project
            )
        }
    }
    
    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.PSI_FILE)
        val isMetalFile = file is MetalFile
        
        e.presentation.isEnabled = isMetalFile
        e.presentation.isVisible = isMetalFile
    }
}

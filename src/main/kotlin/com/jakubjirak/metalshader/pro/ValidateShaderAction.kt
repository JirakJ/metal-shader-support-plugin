package com.jakubjirak.metalshader.pro

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.jakubjirak.metalshader.language.MetalFile
import com.jakubjirak.metalshader.licensing.MetalLicenseManager

class ValidateShaderAction : AnAction("Validate Shader") {
    
    override fun actionPerformed(e: AnActionEvent) {
        // Pro feature check
        if (!MetalLicenseManager.getInstance().isProFeatureEnabled()) {
            Messages.showInfoMessage(
                e.project,
                "Advanced Shader Validation is a Pro feature.\n\n" +
                        "Unlock with Pro license:\n" +
                        "• Threadgroup size validation\n" +
                        "• Buffer binding checks\n" +
                        "• Performance warnings\n" +
                        "• Portability checks\n" +
                        "• Metal 4 compatibility\n\n" +
                        "Contact: support@jakubjirak.com",
                "Metal Shader Support - Pro Feature"
            )
            return
        }
        
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.PSI_FILE) as? MetalFile ?: return
        
        val validator = MetalShaderValidator()
        val result = validator.validate(file.text)
        
        if (result != null) {
            val formatted = validator.formatValidationResult(result)
            
            // Show results in dialog
            Messages.showMessageDialog(
                project,
                formatted,
                "Metal Shader Validation Results",
                if (result.isValid && result.warnings.isEmpty()) {
                    Messages.getInformationIcon()
                } else if (!result.isValid) {
                    Messages.getErrorIcon()
                } else {
                    Messages.getWarningIcon()
                }
            )
            
            // Show notification with summary
            val notifType = when {
                !result.isValid -> NotificationType.ERROR
                result.warnings.isNotEmpty() -> NotificationType.WARNING
                else -> NotificationType.INFORMATION
            }
            
            val summary = when {
                !result.isValid -> "❌ ${result.errors.size} error(s) found"
                result.warnings.isNotEmpty() -> "⚠️  ${result.warnings.size} warning(s) found"
                else -> "✅ Shader is valid!"
            }
            
            Notifications.Bus.notify(
                Notification(
                    "Metal Shader Support",
                    "Shader Validation Complete",
                    summary,
                    notifType
                ),
                project
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

package com.jakubjirak.metalshader.licensing

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class DeactivateLicenseAction : AnAction("Deactivate License") {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val licenseManager = MetalLicenseManager.getInstance()
        val state = licenseManager.getState()
        
        if (state.tier == MetalLicenseManager.LicenseTier.FREE) {
            Messages.showInfoMessage(
                project,
                "No active license to deactivate.\n\nYou are currently using the Free tier.",
                "Metal Shader Support - No Active License"
            )
            return
        }
        
        val result = Messages.showYesNoDialog(
            project,
            "Are you sure you want to deactivate your license?\n\n" +
                    "Current tier: ${state.tier}\n" +
                    "This will revert you to the Free tier and disable Pro features.\n\n" +
                    "You can reactivate your license at any time.",
            "Metal Shader Support - Deactivate License",
            "Deactivate",
            "Cancel",
            Messages.getWarningIcon()
        )
        
        if (result == Messages.YES) {
            licenseManager.deactivateLicense()
            Messages.showInfoMessage(
                project,
                "License deactivated successfully.\n\n" +
                        "You are now using the Free tier.\n" +
                        "To reactivate, go to Tools → Metal Shader Support → License → Activate License",
                "Metal Shader Support - License Deactivated"
            )
        }
    }
    
    override fun update(e: AnActionEvent) {
        val licenseManager = MetalLicenseManager.getInstance()
        val state = licenseManager.getState()
        e.presentation.isEnabled = state.tier != MetalLicenseManager.LicenseTier.FREE
    }
}

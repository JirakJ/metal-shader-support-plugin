package com.jakubjirak.metalshader.licensing

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class LicenseActivationAction : AnAction("Activate License") {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val licenseManager = MetalLicenseManager.getInstance()
        
        val dialog = LicenseActivationDialog(licenseManager, project)
        if (dialog.showAndGet()) {
            val licenseKey = dialog.getLicenseKey()
            if (licenseKey.isEmpty()) {
                Messages.showWarningDialog(
                    project,
                    "Please enter a license key.",
                    "Metal Shader Support - No License Key"
                )
                return
            }
            
            if (licenseManager.activateLicense(licenseKey)) {
                val state = licenseManager.getState()
                Messages.showInfoMessage(
                    project,
                    "✅ License activated successfully!\n\n" +
                            "Tier: ${state.tier}\n" +
                            "Status: ${licenseManager.getLicenseInfo()}\n\n" +
                            "All Pro features are now unlocked.\n" +
                            "Restart IDE to fully apply changes.",
                    "Metal Shader Support - License Activated"
                )
            } else {
                Messages.showErrorDialog(
                    project,
                    "❌ Invalid license key. Please check and try again.\n\n" +
                            "Valid formats:\n" +
                            "• METAL-PRO-XXXX-XXXX-XXXX (Pro tier)\n" +
                            "• METAL-ENT-XXXX-XXXX-XXXX (Enterprise tier)\n\n" +
                            "For licensing inquiries: support@jakubjirak.com",
                    "Metal Shader Support - Activation Failed"
                )
            }
        }
    }
}

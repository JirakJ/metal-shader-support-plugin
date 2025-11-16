package com.jakubjirak.metalshader.licensing

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class LicenseStartupActivity : ProjectActivity {
    
    override suspend fun execute(project: Project) {
        val licenseManager = MetalLicenseManager.getInstance()
        val state = licenseManager.getState()
        
        // Only show notification once per session for Free tier users
        if (state.tier == MetalLicenseManager.LicenseTier.FREE) {
            showFreeTierNotification(project)
        } else {
            // For Pro/Enterprise users, just log that license is active
            showLicenseActiveNotification(project, state.tier)
        }
    }
    
    private fun showFreeTierNotification(project: Project) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Metal Shader Support")
            .createNotification(
                "Metal Shader Support - Free Tier",
                "You're using the Free tier. Upgrade to Pro to unlock performance profiler, " +
                        "complexity analyzer, and more advanced features.",
                NotificationType.INFORMATION
            )
            .addAction(object : AnAction("Activate License") {
                override fun actionPerformed(e: AnActionEvent) {
                    // Open the license activation dialog directly
                    val licenseManager = MetalLicenseManager.getInstance()
                    val dialog = LicenseActivationDialog(licenseManager, e.project)
                    dialog.show()
                }
            })
            .addAction(object : AnAction("Learn More") {
                override fun actionPerformed(e: AnActionEvent) {
                    com.intellij.ide.BrowserUtil.browse("https://jakubjirak.com")
                }
            })
            .notify(project)
    }
    
    private fun showLicenseActiveNotification(project: Project, tier: MetalLicenseManager.LicenseTier) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Metal Shader Support")
            .createNotification(
                "Metal Shader Support - ${tier} License Active",
                "All Pro features are unlocked. Thank you for your support!",
                NotificationType.INFORMATION
            )
            .notify(project)
    }
}

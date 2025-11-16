package com.jakubjirak.metalshader.licensing

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import java.text.SimpleDateFormat
import java.util.*

class LicenseInfoAction : AnAction("License Information") {
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val licenseManager = MetalLicenseManager.getInstance()
        val state = licenseManager.getState()
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val activationDate = if (state.activationDate > 0) {
            dateFormat.format(Date(state.activationDate))
        } else {
            "Not activated"
        }
        
        val message = buildString {
            append("<html>")
            append("<h3>Metal Shader Support - License Information</h3>")
            append("<br>")
            append("<table cellpadding='5'>")
            append("<tr><td><b>License Tier:</b></td><td>${state.tier}</td></tr>")
            append("<tr><td><b>Status:</b></td><td>${licenseManager.getLicenseInfo()}</td></tr>")
            append("<tr><td><b>Activation Date:</b></td><td>$activationDate</td></tr>")
            
            if (state.licenseKey.isNotEmpty()) {
                val maskedKey = state.licenseKey.take(12) + "..." + state.licenseKey.takeLast(4)
                append("<tr><td><b>License Key:</b></td><td>$maskedKey</td></tr>")
            }
            
            append("</table>")
            append("<br>")
            
            when (state.tier) {
                MetalLicenseManager.LicenseTier.FREE -> {
                    append("<p><b>Free Tier Features:</b></p>")
                    append("<ul>")
                    append("<li>Basic syntax highlighting</li>")
                    append("<li>Code completion</li>")
                    append("<li>Live templates</li>")
                    append("<li>Basic error detection</li>")
                    append("</ul>")
                    append("<br>")
                    append("<p><b>Upgrade to Pro</b> to unlock:</p>")
                    append("<ul>")
                    append("<li>Performance profiler</li>")
                    append("<li>Shader complexity analyzer</li>")
                    append("<li>Advanced validation</li>")
                    append("<li>Automatic optimization</li>")
                    append("<li>Priority support</li>")
                    append("</ul>")
                    append("<br>")
                    append("<p>Purchase: <b>https://jakubjirak.com</b></p>")
                    append("<p>Contact: <b>support@jakubjirak.com</b></p>")
                }
                MetalLicenseManager.LicenseTier.PRO -> {
                    append("<p><b>✅ All Pro Features Unlocked</b></p>")
                    append("<ul>")
                    append("<li>✅ Performance profiler</li>")
                    append("<li>✅ Shader complexity analyzer</li>")
                    append("<li>✅ Advanced validation</li>")
                    append("<li>✅ Automatic optimization</li>")
                    append("<li>✅ Priority support</li>")
                    append("</ul>")
                    append("<br>")
                    append("<p>Thank you for supporting Metal Shader Support!</p>")
                }
                MetalLicenseManager.LicenseTier.ENTERPRISE -> {
                    append("<p><b>✅ All Enterprise Features Unlocked</b></p>")
                    append("<ul>")
                    append("<li>✅ All Pro features</li>")
                    append("<li>✅ Team collaboration tools</li>")
                    append("<li>✅ Advanced analytics</li>")
                    append("<li>✅ Cross-platform export</li>")
                    append("<li>✅ Dedicated support</li>")
                    append("</ul>")
                    append("<br>")
                    append("<p>Thank you for your enterprise support!</p>")
                }
            }
            
            append("</html>")
        }
        
        Messages.showMessageDialog(
            project,
            message,
            "Metal Shader Support - License Information",
            Messages.getInformationIcon()
        )
    }
}

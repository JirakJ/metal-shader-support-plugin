/*
 * Copyright (c) 2025 Jakub Jirák. All rights reserved.
 * This software and its source code are proprietary and confidential.
 */
package com.jakubjirak.metalshader.enterprise.transpiler

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.jakubjirak.metalshader.licensing.MetalLicenseManager
import com.jakubjirak.metalshader.licensing.MetalLicenseManager.LicenseTier

class TranspileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val licenseManager = MetalLicenseManager.getInstance()
        
        if (licenseManager.getCurrentTier() != LicenseTier.ENTERPRISE) {
            Messages.showInfoMessage(
                "Enterprise license required for shader transpilation.\n\n" +
                "Upgrade to Enterprise ($199/year) to convert Metal shaders to:\n" +
                "• SPIR-V (Vulkan)\n" +
                "• HLSL (DirectX)\n" +
                "• WGSL (WebGPU)",
                "Enterprise Feature"
            )
            return
        }
        
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        
        if (!file.name.endsWith(".metal") && !file.name.endsWith(".msl")) {
            Messages.showErrorDialog(
                "Please select a Metal shader file (.metal or .msl)",
                "Invalid File Type"
            )
            return
        }
        
        val project = e.project ?: return
        val service = MetalTranspilerService.getInstance(project)
        
        val options = arrayOf("SPIR-V (Vulkan)", "HLSL (DirectX)", "WGSL (WebGPU)", "Cancel")
        val choice = Messages.showDialog(
            "Select target shader language:",
            "Transpile Metal Shader",
            options,
            0,
            Messages.getQuestionIcon()
        )
        
        when (choice) {
            0 -> service.transpileToSPIRV(file)
            1 -> service.transpileToHLSL(file)
            2 -> service.transpileToWGSL(file)
        }
    }
    
    override fun update(e: AnActionEvent) {
        val licenseManager = MetalLicenseManager.getInstance()
        e.presentation.isEnabled = licenseManager.getCurrentTier() == LicenseTier.ENTERPRISE
        
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isVisible = file != null && 
            (file.name.endsWith(".metal") || file.name.endsWith(".msl"))
    }
}

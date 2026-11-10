/*
 * Copyright (c) 2025 Jakub Jirák.
 * Licensed under the Apache License, Version 2.0. See LICENSE for details.
 */
package com.jakubjirak.metalshader.enterprise.transpiler

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages

class TranspileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
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
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabled = file != null && 
            (file.name.endsWith(".metal") || file.name.endsWith(".msl"))
        e.presentation.isVisible = file != null && 
            (file.name.endsWith(".metal") || file.name.endsWith(".msl"))
    }
}

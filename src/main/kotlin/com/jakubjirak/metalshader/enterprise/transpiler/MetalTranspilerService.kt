/*
 * Copyright (c) 2025 Jakub Jirák.
 * Licensed under the Apache License, Version 2.0. See LICENSE for details.
 */
package com.jakubjirak.metalshader.enterprise.transpiler

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.application.WriteAction
import java.io.IOException

@Service(Service.Level.PROJECT)
class MetalTranspilerService(private val project: Project) {
    
    companion object {
        fun getInstance(project: Project): MetalTranspilerService {
            return project.getService(MetalTranspilerService::class.java)
        }
    }
    
    fun transpileToSPIRV(metalFile: VirtualFile) {
        transpile(metalFile, "spvasm", "SPIR-V") { content ->
            MetalTranspiler.convertMetalToSPIRV(content, metalFile.name)
        }
    }

    fun transpileToHLSL(metalFile: VirtualFile) {
        transpile(metalFile, "hlsl", "HLSL") { content ->
            MetalTranspiler.convertMetalToHLSL(content, metalFile.name)
        }
    }

    fun transpileToWGSL(metalFile: VirtualFile) {
        transpile(metalFile, "wgsl", "WGSL") { content ->
            MetalTranspiler.convertMetalToWGSL(content, metalFile.name)
        }
    }

    private fun transpile(sourceFile: VirtualFile, targetExtension: String, targetName: String, 
                         converter: (String) -> String) {
        try {
            val content = String(sourceFile.contentsToByteArray())
            val transpiled = converter(content)
            
            val outputFileName = sourceFile.nameWithoutExtension + "." + targetExtension
            val outputFile = sourceFile.parent?.findChild(outputFileName) 
                ?: WriteAction.compute<VirtualFile, IOException> {
                    sourceFile.parent?.createChildData(this, outputFileName)
                }
            
            WriteAction.run<IOException> {
                outputFile?.setBinaryContent(transpiled.toByteArray())
            }
            
            if (outputFile != null) {
                FileEditorManager.getInstance(project).openFile(outputFile, true)
                Messages.showInfoMessage(
                    project,
                    "Successfully transpiled to $targetName:\n${outputFile.path}",
                    "Transpilation Complete"
                )
            }
        } catch (e: Exception) {
            Messages.showErrorDialog(
                project,
                "Failed to transpile shader:\n${e.message}",
                "Transpilation Error"
            )
        }
    }
}

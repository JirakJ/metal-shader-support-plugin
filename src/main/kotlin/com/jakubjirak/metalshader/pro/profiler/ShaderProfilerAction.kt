/*
 * Copyright (c) 2025 Jakub Jirák. All rights reserved.
 * This software and its source code are proprietary and confidential.
 */
package com.jakubjirak.metalshader.pro.profiler

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager

class ShaderProfilerAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        
        // Open Metal Profiler tool window
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow("Metal Profiler")
        
        if (toolWindow != null) {
            toolWindow.activate(null)
        } else {
            Messages.showInfoMessage(
                project,
                "Metal Profiler tool window is not available.\nPlease restart IDE to enable this feature.",
                "GPU Profiler"
            )
        }
    }
    
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = true
    }
}

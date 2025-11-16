// Copyright (c) 2025 Jakub Jirák. All rights reserved.
package com.jakubjirak.metal.pro.preview

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBLabel
import com.jakubjirak.metalshader.language.MetalFileType
import javax.swing.JPanel

@Service(Service.Level.PROJECT)
class MetalShaderPreviewService(private val project: Project) : Disposable {
    
    private var currentPreviewPanel: MetalShaderPreviewPanel? = null
    private var currentEditor: Editor? = null
    
    init {
        // Listen for file editor changes
        project.messageBus.connect(this).subscribe(
            FileEditorManagerListener.FILE_EDITOR_MANAGER,
            object : FileEditorManagerListener {
                override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
                    updatePreview(file, source.selectedTextEditor)
                }
            }
        )
    }
    
    fun getPreviewPanel(): JPanel {
        val selectedEditor = FileEditorManager.getInstance(project).selectedTextEditor
        val selectedFile = FileEditorManager.getInstance(project).selectedFiles.firstOrNull()
        
        if (selectedEditor != null && selectedFile?.fileType is MetalFileType) {
            return getOrCreatePreviewPanel(selectedEditor)
        }
        
        return JPanel(java.awt.BorderLayout()).apply {
            add(JBLabel("Open a .metal file to see preview").apply {
                horizontalAlignment = JBLabel.CENTER
            }, java.awt.BorderLayout.CENTER)
        }
    }
    
    private fun updatePreview(file: VirtualFile?, editor: Editor?) {
        if (file?.fileType is MetalFileType && editor != null) {
            val panel = getOrCreatePreviewPanel(editor)
            panel.updateShaderCode(editor.document.text)
        }
    }
    
    private fun getOrCreatePreviewPanel(editor: Editor): MetalShaderPreviewPanel {
        if (currentPreviewPanel == null || currentEditor != editor) {
            currentPreviewPanel?.let { Disposer.dispose(it) }
            
            currentPreviewPanel = MetalShaderPreviewPanel(project, editor).also {
                Disposer.register(this, it)
            }
            currentEditor = editor
        }
        return currentPreviewPanel!!
    }
    
    override fun dispose() {
        currentPreviewPanel?.let { Disposer.dispose(it) }
        currentPreviewPanel = null
        currentEditor = null
    }
    
    companion object {
        fun getInstance(project: Project): MetalShaderPreviewService = project.service()
    }
}

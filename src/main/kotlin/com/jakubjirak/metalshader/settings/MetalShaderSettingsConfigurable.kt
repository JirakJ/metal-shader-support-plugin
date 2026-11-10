/*
 * Copyright (c) 2024-2025 Jakub Jirak.
 * Licensed under the Apache License, Version 2.0. See LICENSE for details.
 */
package com.jakubjirak.metalshader.settings

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Project-level configurable for the Metal Shader Support plugin.
 * Provides a settings UI under Settings > Tools > Metal Shader Support.
 */
class MetalShaderSettingsConfigurable(private val project: Project) : SearchableConfigurable {

    private lateinit var maxThreadgroupSizeField: JBTextField
    private lateinit var enablePreviewCheckBox: JBCheckBox
    private lateinit var previewFpsCapField: JBTextField
    private lateinit var transpilerTargetField: JBTextField
    private lateinit var enablePerfInspectionsCheckBox: JBCheckBox
    private var myPanel: JPanel? = null

    override fun getId(): String = "metal.shader.support.settings"

    override fun getDisplayName(): String = "Metal Shader Support"

    override fun createComponent(): JComponent {
        maxThreadgroupSizeField = JBTextField()
        enablePreviewCheckBox = JBCheckBox("Enable real-time shader preview")
        previewFpsCapField = JBTextField()
        transpilerTargetField = JBTextField()
        enablePerfInspectionsCheckBox = JBCheckBox("Enable performance inspections")

        val panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Max threadgroup size:"), maxThreadgroupSizeField, 1, false)
            .addComponent(enablePreviewCheckBox, 1)
            .addLabeledComponent(JBLabel("Preview FPS cap:"), previewFpsCapField, 1, false)
            .addLabeledComponent(JBLabel("Transpiler target (SPIR-V, HLSL, WGSL):"), transpilerTargetField, 1, false)
            .addComponent(enablePerfInspectionsCheckBox, 1)
            .addComponentFillVertically(JPanel(), 0)
            .panel

        myPanel = panel
        reset()
        return panel
    }

    override fun isModified(): Boolean {
        val settings = MetalShaderSettings.getInstance(project)
        return maxThreadgroupSizeField.text != settings.maxThreadgroupSize.toString() ||
                enablePreviewCheckBox.isSelected != settings.enableRealtimePreview ||
                previewFpsCapField.text != settings.previewFpsCap.toString() ||
                transpilerTargetField.text != settings.transpilerTarget ||
                enablePerfInspectionsCheckBox.isSelected != settings.enablePerformanceInspections
    }

    override fun apply() {
        val settings = MetalShaderSettings.getInstance(project)
        settings.maxThreadgroupSize = maxThreadgroupSizeField.text.toIntOrNull() ?: 1024
        settings.enableRealtimePreview = enablePreviewCheckBox.isSelected
        settings.previewFpsCap = previewFpsCapField.text.toIntOrNull() ?: 60
        settings.transpilerTarget = transpilerTargetField.text
        settings.enablePerformanceInspections = enablePerfInspectionsCheckBox.isSelected
    }

    override fun reset() {
        val settings = MetalShaderSettings.getInstance(project)
        maxThreadgroupSizeField.text = settings.maxThreadgroupSize.toString()
        enablePreviewCheckBox.isSelected = settings.enableRealtimePreview
        previewFpsCapField.text = settings.previewFpsCap.toString()
        transpilerTargetField.text = settings.transpilerTarget
        enablePerfInspectionsCheckBox.isSelected = settings.enablePerformanceInspections
    }
}

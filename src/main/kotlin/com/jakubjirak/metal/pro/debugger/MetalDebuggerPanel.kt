// Copyright (c) 2025 Jakub Jirák. All rights reserved.
package com.jakubjirak.metal.pro.debugger

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.GridLayout
import javax.swing.*
import javax.swing.table.DefaultTableModel

class MetalDebuggerPanel : JBPanel<MetalDebuggerPanel>(BorderLayout()) {
    
    private val variablesTable: JBTable
    private val threadInfoLabel = JBLabel("Thread: Not started")
    private val gpuStateLabel = JBLabel("GPU: Idle")
    
    init {
        // Variables table
        val model = DefaultTableModel(
            arrayOf("Variable", "Type", "Value", "Memory"),
            0
        )
        
        variablesTable = JBTable(model).apply {
            setShowGrid(true)
            gridColor = JBColor.border()
        }
        
        // Add sample data
        model.addRow(arrayOf("gid", "uint2", "(512, 384)", "Thread position"))
        model.addRow(arrayOf("color", "float4", "(0.5, 0.3, 0.8, 1.0)", "16 bytes"))
        model.addRow(arrayOf("inTexture", "texture2d<half>", "1024x768 RGBA16", "Device memory"))
        
        setupUI()
    }
    
    private fun setupUI() {
        border = JBUI.Borders.empty(8)
        
        // Top info panel
        val infoPanel = JPanel(GridLayout(2, 1, 5, 5)).apply {
            border = JBUI.Borders.empty(0, 0, 8, 0)
            add(threadInfoLabel)
            add(gpuStateLabel)
        }
        
        // Variables section
        val variablesPanel = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.empty(8, 0, 0, 0)
            add(JBLabel("Variables:").apply {
                border = JBUI.Borders.empty(0, 0, 4, 0)
            }, BorderLayout.NORTH)
            add(JBScrollPane(variablesTable), BorderLayout.CENTER)
        }
        
        // Control buttons
        val controlPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            border = JBUI.Borders.empty(8, 0, 0, 0)
            
            add(JButton("Capture Frame"))
            add(Box.createHorizontalStrut(5))
            add(JButton("Step Thread"))
            add(Box.createHorizontalStrut(5))
            add(JButton("View Memory"))
            add(Box.createHorizontalGlue())
        }
        
        add(infoPanel, BorderLayout.NORTH)
        add(variablesPanel, BorderLayout.CENTER)
        add(controlPanel, BorderLayout.SOUTH)
    }
}

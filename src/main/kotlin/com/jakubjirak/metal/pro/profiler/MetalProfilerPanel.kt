// Copyright (c) 2025 Jakub Jirák. All rights reserved.
package com.jakubjirak.metal.pro.profiler

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.*

class MetalProfilerPanel : JBPanel<MetalProfilerPanel>(BorderLayout()) {
    
    private val gpuTimeLabel = JBLabel("GPU Time: 0.5ms")
    private val memoryLabel = JBLabel("Memory: 128 MB")
    private val bandwidthLabel = JBLabel("Bandwidth: 45 GB/s")
    private val occupancyLabel = JBLabel("Occupancy: 87%")
    
    private val performanceChart = PerformanceChartPanel()
    
    init {
        setupUI()
    }
    
    private fun setupUI() {
        border = JBUI.Borders.empty(8)
        
        // Stats panel
        val statsPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = JBUI.Borders.empty(0, 0, 8, 0)
            
            add(createStatRow("Performance Statistics:"))
            add(Box.createVerticalStrut(8))
            add(createStatRow(gpuTimeLabel))
            add(createStatRow(memoryLabel))
            add(createStatRow(bandwidthLabel))
            add(createStatRow(occupancyLabel))
        }
        
        // Chart
        performanceChart.preferredSize = Dimension(400, 200)
        performanceChart.border = JBUI.Borders.customLine(JBColor.border(), 1)
        
        val chartPanel = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.empty(8, 0, 0, 0)
            add(JBLabel("GPU Timeline:").apply {
                border = JBUI.Borders.empty(0, 0, 4, 0)
            }, BorderLayout.NORTH)
            add(performanceChart, BorderLayout.CENTER)
        }
        
        // Recommendations
        val recommendations = JTextArea().apply {
            text = """
                Performance Recommendations:
                • Consider reducing threadgroup size from 1024 to 512
                • Texture bandwidth is near limit - use mipmaps
                • Good occupancy - kernel is well optimized
            """.trimIndent()
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
            background = JBColor.PanelBackground
            border = JBUI.Borders.empty(8)
        }
        
        val recommendPanel = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.empty(8, 0, 0, 0)
            add(JScrollPane(recommendations), BorderLayout.CENTER)
        }
        
        add(statsPanel, BorderLayout.NORTH)
        add(chartPanel, BorderLayout.CENTER)
        add(recommendPanel, BorderLayout.SOUTH)
    }
    
    private fun createStatRow(text: String): JPanel {
        return createStatRow(JBLabel(text))
    }
    
    private fun createStatRow(component: JComponent): JPanel {
        return JPanel(BorderLayout()).apply {
            add(component, BorderLayout.WEST)
            maximumSize = Dimension(Int.MAX_VALUE, component.preferredSize.height)
        }
    }
    
    private inner class PerformanceChartPanel : JPanel() {
        private val dataPoints = mutableListOf<Float>()
        
        init {
            // Generate sample data
            repeat(50) {
                dataPoints.add((Math.random() * 2 + 0.5).toFloat())
            }
            
            Timer(100) {
                dataPoints.removeAt(0)
                dataPoints.add((Math.random() * 2 + 0.5).toFloat())
                repaint()
            }.start()
        }
        
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            
            val g2d = g as Graphics2D
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            
            val w = width
            val h = height
            val margin = 20
            
            // Background
            g2d.color = JBColor.WHITE
            g2d.fillRect(0, 0, w, h)
            
            // Grid
            g2d.color = JBColor.LIGHT_GRAY
            for (i in 0..5) {
                val y = margin + (h - 2 * margin) * i / 5
                g2d.drawLine(margin, y, w - margin, y)
            }
            
            // Data line
            g2d.color = JBColor.BLUE
            val pointSpacing = (w - 2 * margin).toFloat() / (dataPoints.size - 1)
            
            for (i in 0 until dataPoints.size - 1) {
                val x1 = margin + (i * pointSpacing).toInt()
                val y1 = h - margin - (dataPoints[i] / 3f * (h - 2 * margin)).toInt()
                val x2 = margin + ((i + 1) * pointSpacing).toInt()
                val y2 = h - margin - (dataPoints[i + 1] / 3f * (h - 2 * margin)).toInt()
                
                g2d.drawLine(x1, y1, x2, y2)
            }
            
            // Labels
            g2d.color = JBColor.BLACK
            g2d.drawString("3ms", 2, margin)
            g2d.drawString("0ms", 2, h - margin)
            g2d.drawString("GPU Frame Time", w / 2 - 40, h - 5)
        }
    }
}

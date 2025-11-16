package com.jakubjirak.metal.documentation

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement

class MetalDocumentationProvider : AbstractDocumentationProvider() {
    
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        val text = element?.text ?: return null
        
        return when (text) {
            "kernel" -> """
                <html><body>
                <h3>kernel</h3>
                <p>Declares a compute kernel function that executes on the GPU.</p>
                <pre>kernel void myKernel(uint3 gid [[thread_position_in_grid]]) { }</pre>
                </body></html>
            """.trimIndent()
            
            "vertex" -> """
                <html><body>
                <h3>vertex</h3>
                <p>Declares a vertex shader function for graphics pipeline.</p>
                </body></html>
            """.trimIndent()
            
            "fragment" -> """
                <html><body>
                <h3>fragment</h3>
                <p>Declares a fragment shader function for graphics pipeline.</p>
                </body></html>
            """.trimIndent()
            
            else -> null
        }
    }
}

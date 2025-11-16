package com.jakubjirak.metalshader.language

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement

class MetalDocumentationProvider : AbstractDocumentationProvider() {
    
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        if (element == null) return null
        
        val text = element.text
        
        return when {
            // Shader function keywords
            text == "kernel" -> buildDoc(
                "Kernel Function",
                "Compute shader entry point executed on GPU",
                """
                <b>Usage:</b><br/>
                <code>kernel void myKernel(params...) { }</code><br/><br/>
                Executes parallel computations on textures or buffers.
                """.trimIndent()
            )
            
            text == "vertex" -> buildDoc(
                "Vertex Function",
                "Vertex shader entry point",
                """
                <b>Usage:</b><br/>
                <code>vertex VertexOut vertexShader(VertexIn in [[stage_in]]) { }</code><br/><br/>
                Processes vertex data for rendering pipeline.
                """.trimIndent()
            )
            
            text == "fragment" -> buildDoc(
                "Fragment Function",
                "Fragment/pixel shader entry point",
                """
                <b>Usage:</b><br/>
                <code>fragment float4 fragmentShader(VertexOut in [[stage_in]]) { }</code><br/><br/>
                Determines final pixel colors.
                """.trimIndent()
            )
            
            // Metal 4 - Mesh shading pipeline
            text == "object" -> buildDoc(
                "Object Shader (Metal 4)",
                "Task/Amplification shader for mesh pipeline (iOS 18+, macOS 15+)",
                """
                <b>Usage:</b><br/>
                <code>[[object]] void objectShader(object_data Payload& payload [[payload]]) { }</code><br/><br/>
                Performs culling and LOD selection before mesh shader execution.<br/>
                Equivalent to task shader in Vulkan or amplification shader in DirectX.
                """.trimIndent()
            )
            
            text == "mesh" -> buildDoc(
                "Mesh Shader (Metal 4)",
                "Geometry generation shader (iOS 18+, macOS 15+)",
                """
                <b>Usage:</b><br/>
                <code>[[mesh]] void meshShader(MeshOutput output) { }</code><br/><br/>
                Generates vertices and primitives dynamically on GPU.<br/>
                Replaces traditional vertex shader + geometry shader pipeline.
                """.trimIndent()
            )
            
            // Built-in functions
            text == "sample" -> buildDoc(
                "sample()",
                "Sample a texture",
                """
                <b>Signature:</b><br/>
                <code>T sample(sampler s, float2 coord)</code><br/><br/>
                Samples texture at given coordinates with filtering.
                """.trimIndent()
            )
            
            text == "read" -> buildDoc(
                "read()",
                "Read texture without sampling",
                """
                <b>Signature:</b><br/>
                <code>T read(uint2 coord)</code><br/><br/>
                Reads exact texel value without filtering.
                """.trimIndent()
            )
            
            text == "write" -> buildDoc(
                "write()",
                "Write to texture",
                """
                <b>Signature:</b><br/>
                <code>void write(T color, uint2 coord)</code><br/><br/>
                Writes color to texture at given coordinates.
                """.trimIndent()
            )
            
            text == "dot" -> buildDoc(
                "dot()",
                "Vector dot product",
                """
                <b>Signature:</b><br/>
                <code>float dot(floatN x, floatN y)</code><br/><br/>
                Returns scalar product of two vectors.
                """.trimIndent()
            )
            
            text == "normalize" -> buildDoc(
                "normalize()",
                "Normalize vector",
                """
                <b>Signature:</b><br/>
                <code>floatN normalize(floatN v)</code><br/><br/>
                Returns unit vector in same direction.
                """.trimIndent()
            )
            
            // Metal 4 - Raytracing functions
            text == "trace_ray" -> buildDoc(
                "trace_ray() (Metal 4)",
                "Trace a ray through acceleration structure (iOS 18+, macOS 15+)",
                """
                <b>Signature:</b><br/>
                <code>intersection_result trace_ray(ray, acceleration_structure, mask)</code><br/><br/>
                Hardware-accelerated raytracing. Returns closest intersection.
                """.trimIndent()
            )
            
            text == "intersect_ray" -> buildDoc(
                "intersect_ray() (Metal 4)",
                "Test ray intersection (iOS 18+, macOS 15+)",
                """
                <b>Signature:</b><br/>
                <code>bool intersect_ray(ray, acceleration_structure)</code><br/><br/>
                Returns true if ray hits any geometry. Optimized for shadows/occlusion.
                """.trimIndent()
            )
            
            text == "set_mesh_outputs" -> buildDoc(
                "set_mesh_outputs() (Metal 4)",
                "Set mesh shader output counts (iOS 18+, macOS 15+)",
                """
                <b>Signature:</b><br/>
                <code>void set_mesh_outputs(uint vertexCount, uint primitiveCount)</code><br/><br/>
                Defines how many vertices and primitives the mesh shader will output.
                """.trimIndent()
            )
            
            // Address spaces
            text == "device" -> buildDoc(
                "device",
                "Device memory address space",
                "Memory accessible by GPU. Persistent across kernel invocations."
            )
            
            text == "threadgroup" -> buildDoc(
                "threadgroup",
                "Threadgroup memory address space",
                "Shared memory between threads in same threadgroup. Fast, but limited size."
            )
            
            text == "constant" -> buildDoc(
                "constant",
                "Constant memory address space",
                "Read-only memory optimized for uniform access patterns."
            )
            
            // Attributes
            text.contains("[[texture") -> buildDoc(
                "[[texture(n)]]",
                "Texture binding attribute",
                "Binds function parameter to texture at index <i>n</i>."
            )
            
            text.contains("[[buffer") -> buildDoc(
                "[[buffer(n)]]",
                "Buffer binding attribute",
                "Binds function parameter to buffer at index <i>n</i>."
            )
            
            text.contains("[[stage_in") -> buildDoc(
                "[[stage_in]]",
                "Stage input attribute",
                "Marks parameter as input from previous pipeline stage."
            )
            
            text.contains("[[thread_position_in_grid") -> buildDoc(
                "[[thread_position_in_grid]]",
                "Thread position in compute grid",
                "Unique position of thread in entire compute grid."
            )
            
            // Metal 4 - Ray tracing attributes
            text.contains("[[payload") -> buildDoc(
                "[[payload]] (Metal 4)",
                "Ray payload data (iOS 18+, macOS 15+)",
                "Data passed between raytracing shaders. Shared between intersection and closest-hit shaders."
            )
            
            text.contains("[[instance_data") -> buildDoc(
                "[[instance_data]] (Metal 4)",
                "Per-instance data in raytracing (iOS 18+, macOS 15+)",
                "Custom data attached to acceleration structure instances."
            )
            
            text.contains("[[primitive_data") -> buildDoc(
                "[[primitive_data]] (Metal 4)",
                "Per-primitive data in mesh shader (iOS 18+, macOS 15+)",
                "Custom data passed from mesh shader to fragment shader per-primitive."
            )
            
            text.contains("[[ray_origin") -> buildDoc(
                "[[ray_origin]] (Metal 4)",
                "Ray origin in intersection function (iOS 18+, macOS 15+)",
                "World-space origin of the ray being tested."
            )
            
            text.contains("[[ray_direction") -> buildDoc(
                "[[ray_direction]] (Metal 4)",
                "Ray direction in intersection function (iOS 18+, macOS 15+)",
                "Normalized world-space direction of the ray."
            )
            
            else -> null
        }
    }
    
    private fun buildDoc(title: String, summary: String, details: String): String {
        return """
            <html>
            <head>
                <style>
                    body { font-family: sans-serif; padding: 8px; }
                    h3 { margin: 0 0 8px 0; color: #4a90e2; }
                    .summary { font-style: italic; margin-bottom: 12px; color: #666; }
                    code { background: #f5f5f5; padding: 2px 6px; border-radius: 3px; font-family: monospace; }
                    b { color: #333; }
                </style>
            </head>
            <body>
                <h3>$title</h3>
                <div class="summary">$summary</div>
                <div>$details</div>
            </body>
            </html>
        """.trimIndent()
    }
}

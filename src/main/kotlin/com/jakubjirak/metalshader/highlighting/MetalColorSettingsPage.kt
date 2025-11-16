package com.jakubjirak.metalshader.highlighting

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.jakubjirak.metalshader.language.MetalIcons
import javax.swing.Icon

class MetalColorSettingsPage : ColorSettingsPage {
    override fun getAttributeDescriptors() = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName() = "Metal"

    override fun getIcon(): Icon = MetalIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = MetalSyntaxHighlighter()

    override fun getDemoText() = """
        // Metal Shading Language Example
        #include <metal_stdlib>
        using namespace metal;
        
        struct VertexIn {
            float4 position [[attribute(0)]];
            float2 texCoord [[attribute(1)]];
        };
        
        struct VertexOut {
            float4 position [[position]];
            float2 texCoord;
        };
        
        vertex VertexOut vertexShader(
            VertexIn in [[stage_in]],
            constant float4x4& mvpMatrix [[buffer(0)]]
        ) {
            VertexOut out;
            out.position = mvpMatrix * in.position;
            out.texCoord = in.texCoord;
            return out;
        }
        
        fragment float4 fragmentShader(
            VertexOut in [[stage_in]],
            texture2d<float> colorTexture [[texture(0)]],
            sampler textureSampler [[sampler(0)]]
        ) {
            float4 color = colorTexture.sample(textureSampler, in.texCoord);
            return color;
        }
        
        kernel void computeShader(
            texture2d<half, access::read> inTexture [[texture(0)]],
            texture2d<half, access::write> outTexture [[texture(1)]],
            uint2 gid [[thread_position_in_grid]]
        ) {
            if (gid.x >= outTexture.get_width() || gid.y >= outTexture.get_height()) {
                return;
            }
            
            half4 color = inTexture.read(gid);
            half luminance = dot(color.rgb, half3(0.299h, 0.587h, 0.114h));
            outTexture.write(half4(luminance), gid);
        }
    """.trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap() = null

    companion object {
        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor("Keyword", MetalSyntaxHighlighter.KEYWORD),
            AttributesDescriptor("Type", MetalSyntaxHighlighter.TYPE),
            AttributesDescriptor("Qualifier", MetalSyntaxHighlighter.QUALIFIER),
            AttributesDescriptor("Number", MetalSyntaxHighlighter.NUMBER),
            AttributesDescriptor("String", MetalSyntaxHighlighter.STRING),
            AttributesDescriptor("Comment", MetalSyntaxHighlighter.COMMENT),
            AttributesDescriptor("Operator", MetalSyntaxHighlighter.OPERATOR),
            AttributesDescriptor("Brackets", MetalSyntaxHighlighter.BRACKET),
            AttributesDescriptor("Semicolon", MetalSyntaxHighlighter.SEMICOLON),
            AttributesDescriptor("Attribute", MetalSyntaxHighlighter.ATTRIBUTE),
            AttributesDescriptor("Built-in Function", MetalSyntaxHighlighter.BUILT_IN),
            AttributesDescriptor("Bad Character", MetalSyntaxHighlighter.BAD_CHARACTER)
        )
    }
}

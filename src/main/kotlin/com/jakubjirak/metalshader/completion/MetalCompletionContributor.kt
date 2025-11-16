package com.jakubjirak.metalshader.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import com.jakubjirak.metalshader.language.MetalKeywords
import com.jakubjirak.metalshader.language.MetalLanguage

class MetalCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().withLanguage(MetalLanguage),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    // Keywords
                    MetalKeywords.KEYWORDS.forEach { keyword ->
                        result.addElement(
                            LookupElementBuilder.create(keyword)
                                .bold()
                                .withTypeText("keyword")
                        )
                    }

                    // Types
                    MetalKeywords.TYPES.forEach { type ->
                        result.addElement(
                            LookupElementBuilder.create(type)
                                .withTypeText("type")
                        )
                    }

                    // Qualifiers
                    MetalKeywords.QUALIFIERS.forEach { qualifier ->
                        result.addElement(
                            LookupElementBuilder.create(qualifier)
                                .withTypeText("qualifier")
                        )
                    }

                    // Built-in functions
                    MetalKeywords.BUILT_IN_FUNCTIONS.forEach { function ->
                        result.addElement(
                            LookupElementBuilder.create("$function()")
                                .withPresentableText(function)
                                .withInsertHandler { context, _ ->
                                    val editor = context.editor
                                    val offset = editor.caretModel.offset
                                    editor.caretModel.moveToOffset(offset - 1)
                                }
                                .withTypeText("built-in function")
                        )
                    }

                    // Common attributes
                    addAttributeCompletions(result)

                    // Common snippets
                    addSnippetCompletions(result)
                }
            }
        )
    }

    private fun addAttributeCompletions(result: CompletionResultSet) {
        val attributes = listOf(
            "[[stage_in]]" to "Stage input",
            "[[position]]" to "Vertex position",
            "[[color(n)]]" to "Color attachment",
            "[[texture(n)]]" to "Texture binding",
            "[[buffer(n)]]" to "Buffer binding",
            "[[sampler(n)]]" to "Sampler binding",
            "[[attribute(n)]]" to "Vertex attribute",
            "[[thread_position_in_grid]]" to "Thread position in grid",
            "[[thread_position_in_threadgroup]]" to "Thread position in threadgroup",
            "[[threadgroup_position_in_grid]]" to "Threadgroup position in grid",
            "[[threads_per_threadgroup]]" to "Threads per threadgroup",
            "[[threads_per_grid]]" to "Threads per grid"
        )

        attributes.forEach { (attr, desc) ->
            result.addElement(
                LookupElementBuilder.create(attr)
                    .withTypeText(desc)
            )
        }
    }

    private fun addSnippetCompletions(result: CompletionResultSet) {
        // Vertex shader template
        result.addElement(
            LookupElementBuilder.create("vertex_template")
                .withPresentableText("vertex shader")
                .withInsertHandler { context, _ ->
                    val template = """
                        vertex VertexOut vertexShader(
                            VertexIn in [[stage_in]],
                            constant float4x4& mvpMatrix [[buffer(0)]]
                        ) {
                            VertexOut out;
                            out.position = mvpMatrix * in.position;
                            return out;
                        }
                    """.trimIndent()
                    context.document.replaceString(
                        context.startOffset,
                        context.tailOffset,
                        template
                    )
                }
                .withTypeText("snippet")
        )

        // Fragment shader template
        result.addElement(
            LookupElementBuilder.create("fragment_template")
                .withPresentableText("fragment shader")
                .withInsertHandler { context, _ ->
                    val template = """
                        fragment float4 fragmentShader(
                            VertexOut in [[stage_in]],
                            texture2d<float> texture [[texture(0)]],
                            sampler textureSampler [[sampler(0)]]
                        ) {
                            return texture.sample(textureSampler, in.texCoord);
                        }
                    """.trimIndent()
                    context.document.replaceString(
                        context.startOffset,
                        context.tailOffset,
                        template
                    )
                }
                .withTypeText("snippet")
        )

        // Kernel template
        result.addElement(
            LookupElementBuilder.create("kernel_template")
                .withPresentableText("compute kernel")
                .withInsertHandler { context, _ ->
                    val template = """
                        kernel void computeKernel(
                            texture2d<float, access::read> inTexture [[texture(0)]],
                            texture2d<float, access::write> outTexture [[texture(1)]],
                            uint2 gid [[thread_position_in_grid]]
                        ) {
                            if (gid.x >= outTexture.get_width() || gid.y >= outTexture.get_height()) {
                                return;
                            }
                            
                            float4 color = inTexture.read(gid);
                            outTexture.write(color, gid);
                        }
                    """.trimIndent()
                    context.document.replaceString(
                        context.startOffset,
                        context.tailOffset,
                        template
                    )
                }
                .withTypeText("snippet")
        )
    }
}

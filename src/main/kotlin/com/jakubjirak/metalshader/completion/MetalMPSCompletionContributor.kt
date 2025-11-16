package com.jakubjirak.metalshader.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import com.jakubjirak.metalshader.language.MetalIcons
import com.jakubjirak.metalshader.language.MetalLanguage

class MetalMPSCompletionContributor : CompletionContributor() {
    
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            MPSCompletionProvider()
        )
    }
}

class MPSCompletionProvider : CompletionProvider<CompletionParameters>() {
    
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        // Metal Performance Shaders built-in kernels
        addMPSImageFilters(result)
        addMPSMatrixOperations(result)
        addMPSNeuralNetwork(result)
        addMPSRayIntersector(result)
    }
    
    private fun addMPSImageFilters(result: CompletionResultSet) {
        val imageFilters = mapOf(
            "MPSImageGaussianBlur" to "Gaussian blur filter",
            "MPSImageBox" to "Box filter",
            "MPSImageTent" to "Tent filter",
            "MPSImageMedian" to "Median filter",
            "MPSImageSobel" to "Sobel edge detection",
            "MPSImageThresholdBinary" to "Binary threshold",
            "MPSImageThresholdTruncate" to "Truncate threshold",
            "MPSImageHistogram" to "Histogram calculation",
            "MPSImageHistogramEqualization" to "Histogram equalization",
            "MPSImageConvolution" to "Custom convolution",
            "MPSImageDilate" to "Morphological dilation",
            "MPSImageErode" to "Morphological erosion",
            "MPSImageScale" to "Image scaling",
            "MPSImageBilinearScale" to "Bilinear scaling",
            "MPSImageLanczosScale" to "Lanczos scaling"
        )
        
        imageFilters.forEach { (name, desc) ->
            result.addElement(
                LookupElementBuilder.create(name)
                    .withIcon(MetalIcons.FILE)
                    .withTypeText("MPS Filter")
                    .withTailText(" - $desc", true)
            )
        }
    }
    
    private fun addMPSMatrixOperations(result: CompletionResultSet) {
        val matrixOps = mapOf(
            "MPSMatrixMultiplication" to "Matrix multiplication (GEMM)",
            "MPSMatrixVectorMultiplication" to "Matrix-vector multiplication",
            "MPSMatrixSum" to "Matrix sum",
            "MPSMatrixSoftMax" to "SoftMax activation",
            "MPSMatrixLogSoftMax" to "Log SoftMax activation",
            "MPSMatrixDecompositionLU" to "LU decomposition",
            "MPSMatrixDecompositionCholesky" to "Cholesky decomposition",
            "MPSMatrixSolveTriangular" to "Triangular system solve",
            "MPSMatrixFindTopK" to "Find top K values"
        )
        
        matrixOps.forEach { (name, desc) ->
            result.addElement(
                LookupElementBuilder.create(name)
                    .withIcon(MetalIcons.FILE)
                    .withTypeText("MPS Matrix")
                    .withTailText(" - $desc", true)
            )
        }
    }
    
    private fun addMPSNeuralNetwork(result: CompletionResultSet) {
        val nnLayers = mapOf(
            "MPSCNNConvolution" to "Convolution layer",
            "MPSCNNConvolutionTranspose" to "Transposed convolution",
            "MPSCNNFullyConnected" to "Fully connected layer",
            "MPSCNNPoolingMax" to "Max pooling",
            "MPSCNNPoolingAverage" to "Average pooling",
            "MPSCNNBatchNormalization" to "Batch normalization",
            "MPSCNNInstanceNormalization" to "Instance normalization",
            "MPSCNNNeuronReLU" to "ReLU activation",
            "MPSCNNNeuronSigmoid" to "Sigmoid activation",
            "MPSCNNNeuronTanH" to "TanH activation",
            "MPSCNNNeuronPReLU" to "PReLU activation",
            "MPSCNNNeuronELU" to "ELU activation",
            "MPSCNNSoftMax" to "SoftMax layer",
            "MPSCNNLoss" to "Loss layer",
            "MPSCNNDropout" to "Dropout layer",
            "MPSCNNUpsampling" to "Upsampling layer",
            "MPSRNNMatrixTraining" to "RNN training",
            "MPSLSTMDescriptor" to "LSTM layer"
        )
        
        nnLayers.forEach { (name, desc) ->
            result.addElement(
                LookupElementBuilder.create(name)
                    .withIcon(MetalIcons.FILE)
                    .withTypeText("MPS Neural Network")
                    .withTailText(" - $desc", true)
            )
        }
    }
    
    private fun addMPSRayIntersector(result: CompletionResultSet) {
        val rayOps = mapOf(
            "MPSRayIntersector" to "Ray-triangle intersection (Metal 3+)",
            "MPSAccelerationStructure" to "Acceleration structure",
            "MPSInstanceAccelerationStructure" to "Instance acceleration structure (Metal 4)",
            "MPSPrimitiveAccelerationStructure" to "Primitive acceleration structure (Metal 4)",
            "MPSTriangleAccelerationStructure" to "Triangle acceleration structure",
            "MPSPolygonAccelerationStructure" to "Polygon acceleration structure",
            "MPSQuadrilateralAccelerationStructure" to "Quad acceleration structure",
            "MPSSVGFDenoiser" to "SVGF denoiser (Metal 4)"
        )
        
        rayOps.forEach { (name, desc) ->
            result.addElement(
                LookupElementBuilder.create(name)
                    .withIcon(MetalIcons.FILE)
                    .withTypeText("MPS Raytracing")
                    .withTailText(" - $desc", true)
            )
        }
    }
}

#include <metal_stdlib>
using namespace metal;

struct VertexIn {
    float4 position [[attribute(0)]];
    float2 texCoord [[attribute(1)]];
    float3 normal [[attribute(2)]];
};

struct VertexOut {
    float4 position [[position]];
    float2 texCoord;
    float3 normal;
};

vertex VertexOut vertexShader(
    VertexIn in [[stage_in]],
    constant float4x4& modelViewProjection [[buffer(0)]],
    constant float3x3& normalMatrix [[buffer(1)]]
) {
    VertexOut out;
    out.position = modelViewProjection * in.position;
    out.texCoord = in.texCoord;
    out.normal = normalMatrix * in.normal;
    return out;
}

fragment float4 fragmentShader(
    VertexOut in [[stage_in]],
    texture2d<float> colorTexture [[texture(0)]],
    sampler textureSampler [[sampler(0)]],
    constant float3& lightDirection [[buffer(0)]]
) {
    float4 color = colorTexture.sample(textureSampler, in.texCoord);
    float3 normal = normalize(in.normal);
    float diffuse = max(dot(normal, lightDirection), 0.0);
    return color * diffuse;
}

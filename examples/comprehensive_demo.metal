#include <metal_stdlib>
using namespace metal;

// ============================================================================
// STRUCTURES
// ============================================================================

struct VertexIn {
    float4 position [[attribute(0)]];
    float2 texCoord [[attribute(1)]];
    float3 normal [[attribute(2)]];
    float4 color [[attribute(3)]];
};

struct VertexOut {
    float4 position [[position]];
    float2 texCoord;
    float3 normal;
    float4 color;
    float3 worldPosition;
};

struct Uniforms {
    float4x4 modelMatrix;
    float4x4 viewMatrix;
    float4x4 projectionMatrix;
    float3x3 normalMatrix;
    float3 cameraPosition;
    float time;
};

struct Light {
    float3 position;
    float3 color;
    float intensity;
    float radius;
};

// ============================================================================
// VERTEX SHADERS
// ============================================================================

vertex VertexOut basicVertexShader(
    VertexIn in [[stage_in]],
    constant Uniforms& uniforms [[buffer(0)]]
) {
    VertexOut out;
    
    float4x4 mvp = uniforms.projectionMatrix * uniforms.viewMatrix * uniforms.modelMatrix;
    out.position = mvp * in.position;
    out.texCoord = in.texCoord;
    out.normal = uniforms.normalMatrix * in.normal;
    out.color = in.color;
    out.worldPosition = (uniforms.modelMatrix * in.position).xyz;
    
    return out;
}

vertex VertexOut animatedVertexShader(
    VertexIn in [[stage_in]],
    constant Uniforms& uniforms [[buffer(0)]]
) {
    VertexOut out;
    
    // Sine wave animation
    float4 pos = in.position;
    pos.y += sin(pos.x * 2.0 + uniforms.time) * 0.1;
    
    float4x4 mvp = uniforms.projectionMatrix * uniforms.viewMatrix * uniforms.modelMatrix;
    out.position = mvp * pos;
    out.texCoord = in.texCoord;
    out.normal = uniforms.normalMatrix * in.normal;
    out.color = in.color;
    out.worldPosition = (uniforms.modelMatrix * pos).xyz;
    
    return out;
}

// ============================================================================
// FRAGMENT SHADERS
// ============================================================================

fragment float4 texturedFragmentShader(
    VertexOut in [[stage_in]],
    texture2d<float> baseColor [[texture(0)]],
    texture2d<float> normalMap [[texture(1)]],
    sampler textureSampler [[sampler(0)]],
    constant Uniforms& uniforms [[buffer(0)]],
    constant Light* lights [[buffer(1)]],
    constant uint& lightCount [[buffer(2)]]
) {
    // Sample textures
    float4 color = baseColor.sample(textureSampler, in.texCoord);
    float3 normal = normalize(in.normal);
    
    // Lighting calculation
    float3 finalColor = float3(0.0);
    
    for (uint i = 0; i < lightCount; i++) {
        Light light = lights[i];
        
        // Calculate light direction and distance
        float3 lightDir = light.position - in.worldPosition;
        float distance = length(lightDir);
        lightDir = normalize(lightDir);
        
        // Attenuation
        float attenuation = 1.0 / (1.0 + distance * distance / (light.radius * light.radius));
        
        // Diffuse lighting
        float diffuse = max(dot(normal, lightDir), 0.0);
        
        // Specular lighting (Blinn-Phong)
        float3 viewDir = normalize(uniforms.cameraPosition - in.worldPosition);
        float3 halfDir = normalize(lightDir + viewDir);
        float specular = pow(max(dot(normal, halfDir), 0.0), 32.0);
        
        // Accumulate light contribution
        finalColor += (diffuse + specular) * light.color * light.intensity * attenuation;
    }
    
    // Ambient lighting
    finalColor += float3(0.1);
    
    return float4(color.rgb * finalColor, color.a);
}

fragment float4 pbrFragmentShader(
    VertexOut in [[stage_in]],
    texture2d<float> albedoMap [[texture(0)]],
    texture2d<float> metallicMap [[texture(1)]],
    texture2d<float> roughnessMap [[texture(2)]],
    texture2d<float> normalMap [[texture(3)]],
    texturecube<float> environmentMap [[texture(4)]],
    sampler textureSampler [[sampler(0)]],
    constant Uniforms& uniforms [[buffer(0)]]
) {
    // Sample material properties
    float3 albedo = albedoMap.sample(textureSampler, in.texCoord).rgb;
    float metallic = metallicMap.sample(textureSampler, in.texCoord).r;
    float roughness = roughnessMap.sample(textureSampler, in.texCoord).r;
    float3 normal = normalize(in.normal);
    
    // View direction
    float3 V = normalize(uniforms.cameraPosition - in.worldPosition);
    float3 R = reflect(-V, normal);
    
    // Sample environment map
    float3 envColor = environmentMap.sample(textureSampler, R).rgb;
    
    // Simple PBR approximation
    float3 F0 = mix(float3(0.04), albedo, metallic);
    float NdotV = max(dot(normal, V), 0.0);
    float3 fresnel = F0 + (1.0 - F0) * pow(1.0 - NdotV, 5.0);
    
    float3 color = albedo * (1.0 - metallic) + envColor * fresnel * (1.0 - roughness);
    
    return float4(color, 1.0);
}

// ============================================================================
// COMPUTE SHADERS
// ============================================================================

kernel void blurKernel(
    texture2d<half, access::read> inTexture [[texture(0)]],
    texture2d<half, access::write> outTexture [[texture(1)]],
    constant float& blurRadius [[buffer(0)]],
    uint2 gid [[thread_position_in_grid]]
) {
    if (gid.x >= outTexture.get_width() || gid.y >= outTexture.get_height()) {
        return;
    }
    
    int radius = int(blurRadius);
    half4 sum = half4(0.0h);
    float weight = 0.0;
    
    for (int y = -radius; y <= radius; y++) {
        for (int x = -radius; x <= radius; x++) {
            int2 offset = int2(x, y);
            uint2 coord = uint2(int2(gid) + offset);
            
            if (coord.x < inTexture.get_width() && coord.y < inTexture.get_height()) {
                float dist = length(float2(x, y));
                float w = exp(-dist * dist / (2.0 * blurRadius * blurRadius));
                sum += inTexture.read(coord) * half(w);
                weight += w;
            }
        }
    }
    
    outTexture.write(sum / half(weight), gid);
}

kernel void edgeDetectionKernel(
    texture2d<float, access::read> inTexture [[texture(0)]],
    texture2d<float, access::write> outTexture [[texture(1)]],
    uint2 gid [[thread_position_in_grid]]
) {
    if (gid.x >= outTexture.get_width() || gid.y >= outTexture.get_height()) {
        return;
    }
    
    // Sobel operator
    float3 gx = float3(0.0);
    float3 gy = float3(0.0);
    
    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            uint2 coord = uint2(int2(gid) + int2(x, y));
            coord = clamp(coord, uint2(0), uint2(inTexture.get_width() - 1, inTexture.get_height() - 1));
            
            float3 color = inTexture.read(coord).rgb;
            
            // Sobel kernels
            float kx[3][3] = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
            float ky[3][3] = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
            
            gx += color * kx[y + 1][x + 1];
            gy += color * ky[y + 1][x + 1];
        }
    }
    
    float magnitude = length(float2(length(gx), length(gy)));
    outTexture.write(float4(magnitude), gid);
}

kernel void particleUpdateKernel(
    device float4* positions [[buffer(0)]],
    device float4* velocities [[buffer(1)]],
    constant float& deltaTime [[buffer(2)]],
    constant float3& gravity [[buffer(3)]],
    uint gid [[thread_position_in_grid]]
) {
    float4 pos = positions[gid];
    float4 vel = velocities[gid];
    
    // Apply gravity
    vel.xyz += gravity * deltaTime;
    
    // Update position
    pos.xyz += vel.xyz * deltaTime;
    
    // Bounce off ground
    if (pos.y < 0.0) {
        pos.y = 0.0;
        vel.y = -vel.y * 0.8; // 80% restitution
    }
    
    positions[gid] = pos;
    velocities[gid] = vel;
}

// ============================================================================
// ADVANCED COMPUTE - PARALLEL REDUCTION
// ============================================================================

kernel void parallelSumReduction(
    device const float* input [[buffer(0)]],
    device float* output [[buffer(1)]],
    threadgroup float* sharedData [[threadgroup(0)]],
    uint gid [[thread_position_in_grid]],
    uint lid [[thread_position_in_threadgroup]],
    uint groupSize [[threads_per_threadgroup]]
) {
    // Load data into threadgroup memory
    sharedData[lid] = input[gid];
    threadgroup_barrier(mem_flags::mem_threadgroup);
    
    // Parallel reduction
    for (uint stride = groupSize / 2; stride > 0; stride >>= 1) {
        if (lid < stride) {
            sharedData[lid] += sharedData[lid + stride];
        }
        threadgroup_barrier(mem_flags::mem_threadgroup);
    }
    
    // Write result
    if (lid == 0) {
        output[gid / groupSize] = sharedData[0];
    }
}

// ============================================================================
// RAYTRACING SHADER (Metal 3.0+)
// ============================================================================

struct Ray {
    float3 origin;
    float3 direction;
};

struct Sphere {
    float3 center;
    float radius;
    float3 color;
};

float intersectSphere(Ray ray, Sphere sphere) {
    float3 oc = ray.origin - sphere.center;
    float a = dot(ray.direction, ray.direction);
    float b = 2.0 * dot(oc, ray.direction);
    float c = dot(oc, oc) - sphere.radius * sphere.radius;
    float discriminant = b * b - 4 * a * c;
    
    if (discriminant < 0) {
        return -1.0;
    }
    
    return (-b - sqrt(discriminant)) / (2.0 * a);
}

kernel void raytracingKernel(
    texture2d<float, access::write> outTexture [[texture(0)]],
    constant Sphere* spheres [[buffer(0)]],
    constant uint& sphereCount [[buffer(1)]],
    constant float3& cameraPos [[buffer(2)]],
    uint2 gid [[thread_position_in_grid]]
) {
    if (gid.x >= outTexture.get_width() || gid.y >= outTexture.get_height()) {
        return;
    }
    
    // Calculate ray direction
    float2 uv = float2(gid) / float2(outTexture.get_width(), outTexture.get_height());
    uv = uv * 2.0 - 1.0;
    uv.y = -uv.y;
    
    Ray ray;
    ray.origin = cameraPos;
    ray.direction = normalize(float3(uv, -1.0));
    
    // Find closest intersection
    float minDist = INFINITY;
    float3 color = float3(0.1, 0.1, 0.2); // Background color
    
    for (uint i = 0; i < sphereCount; i++) {
        float t = intersectSphere(ray, spheres[i]);
        if (t > 0.0 && t < minDist) {
            minDist = t;
            float3 hitPoint = ray.origin + ray.direction * t;
            float3 normal = normalize(hitPoint - spheres[i].center);
            float3 lightDir = normalize(float3(1, 1, -1));
            float diffuse = max(dot(normal, lightDir), 0.0);
            color = spheres[i].color * (diffuse + 0.2);
        }
    }
    
    outTexture.write(float4(color, 1.0), gid);
}

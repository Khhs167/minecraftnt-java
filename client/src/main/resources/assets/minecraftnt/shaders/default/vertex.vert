#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 size;
layout (location = 2) in vec2 uv;
layout (location = 3) in vec2 uv_size;
layout (location = 4) in float orientation;

out QUAD_DATA {
    vec2 uv;
    vec2 uv_size;
    vec2 size;
    int orientation;
} vs_out;

void main()
{
    gl_Position = vec4(position, 1.0);
    vs_out.size = size;
    vs_out.uv = uv;
    vs_out.uv_size = uv_size;
    vs_out.orientation = int(floor(orientation));
}
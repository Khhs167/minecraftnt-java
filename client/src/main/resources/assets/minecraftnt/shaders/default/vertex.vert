#version 330

layout (location = 0) in vec3 position;
layout (location = 2) in vec2 uv;
layout (location = 4) in float lighting;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;


out QUAD_DATA {
    vec2 uv;
    float lighting;
} vs_out;

void main()
{
    gl_Position = projection * view * model * vec4(position, 1.0);
    vs_out.uv = uv;
    vs_out.lighting = lighting;
}
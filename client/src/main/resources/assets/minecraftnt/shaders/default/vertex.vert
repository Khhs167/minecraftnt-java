#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 size;
layout (location = 2) in int orientation;

out QUAD_DATA {
    vec2 size;
    int orientation;
} vs_out;

void main()
{
    gl_Position = vec4(position, 1.0);
    vs_out.size = size;
    vs_out.orientation = orientation;
}
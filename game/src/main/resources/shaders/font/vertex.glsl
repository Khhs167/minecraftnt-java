#version 330 core

layout (location = 0) in vec3 pos; // the position variable has attribute position 0
layout (location = 1) in vec2 uv; // the position variable has attribute position 0
layout (location = 2) in float color; // the position variable has attribute position 0

out vec2 vertexUV; // specify a color output to the fragment shader

uniform vec2 screen_pos;
uniform vec2 screen_size;

void main()
{
    vec3 p = pos;
    vec3 offset = vec3(screen_pos / screen_size, 0);
    vec4 v_pos = vec4(p / vec3(screen_size.x, screen_size.y, 1.0f) * 32 - vec3(1, 1, 0.5f) + offset, 1.0);// + vec4(screen_pos / screen_size, -0.5f, 0);
    gl_Position = v_pos; // see how we directly give a vec3 to vec4's constructor
    vertexUV = uv; // set the output variable to a dark-red color
}
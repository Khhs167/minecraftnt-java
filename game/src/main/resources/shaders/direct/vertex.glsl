#version 330 core

layout (location = 0) in vec3 pos; // the position variable has attribute position 0
layout (location = 2) in vec3 color; // the position variable has attribute position 0

out vec3 vertex_color; // specify a color output to the fragment shader

uniform mat4 mat_projection;
uniform mat4 mat_view;

void main()
{
    gl_Position = mat_projection * mat_view  * vec4(pos, 1.0); // see how we directly give a vec3 to vec4's constructor
    vertex_color = color;
}
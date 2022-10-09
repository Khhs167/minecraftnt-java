#version 330 core
out vec4 FragColor;

uniform sampler2D texture;

in vec3 vertex_color; // the input variable from the vertex shader (same name and same type)

void main()
{
    FragColor = vec4(vertex_color, 1);
}
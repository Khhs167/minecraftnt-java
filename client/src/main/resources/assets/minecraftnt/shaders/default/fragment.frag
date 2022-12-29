#version 330

uniform sampler2D texture;

out vec4 FragColor;

in struct VertexData {
    vec2 uv;
    float lighting;
} Vertex;

void main() {
    vec2 actualUV = vec2(Vertex.uv.x, 1 - Vertex.uv.y);
    vec4 texture_color = texture2D(texture, actualUV);

    float lighting = Vertex.lighting;//(Vertex.lighting * 0.9f) + 0.1f;

    vec4 shaded_color = vec4(texture_color.rgb * lighting, texture_color.a);

    FragColor = shaded_color;
}
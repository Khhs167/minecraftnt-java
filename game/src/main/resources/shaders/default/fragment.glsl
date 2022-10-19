#version 330 core
out vec4 FragColor;

uniform sampler2D texture;

in vec2 vertexUV; // the input variable from the vertex shader (same name and same type)
in float depth; // the input variable from the vertex shader (same name and same type)
in float lightning;

void main()
{
    vec4 color = texture2D(texture, vertexUV);

    float fogDepth = 100; // Replace the 40 to anything... 40 is also ok.
    vec4 fogColour = vec4(0.5f, 0.8f, 1.0f, 1.0f); // Choose anything

    float originalZ = gl_FragCoord.z / gl_FragCoord.w;
    float fog = clamp(pow(originalZ / fogDepth, 2), 0, 1);

    float finalLightning = lightning * 0.9f + 0.1f;


    FragColor = mix(vec4(color.rgb * finalLightning, color.a), fogColour , fog);
}
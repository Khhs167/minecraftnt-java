package net.minecraftnt.util.noise;

public class FractalPerlin {
    public int octaves;
    public float sizes;
    public float fallOff;

    private final FastNoiseLite fastNoiseLite;

    public FractalPerlin() {
        fastNoiseLite = new FastNoiseLite();
        fastNoiseLite.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
    }

    public float GetNoise(float x, float y) {
        float value = 0;
        float scale = 1;
        float strength = 1;
        float max = 1;
        for(int i = 0; i < octaves; i++) {
            max += strength;
            float v = fastNoiseLite.GetNoise(x * scale, y * scale);
            value += v * strength;
            strength *= fallOff;
            scale *= sizes;
        }

        return value / max;
    }
}

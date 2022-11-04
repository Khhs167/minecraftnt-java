package net.minecraftnt.util.maths;

public final class MathUtils {

    public static float lerpUnclamped(float from, float to, float v){
        return from + (to - from) * v;
    }

    public static float lerp(float from, float to, float v){
        return clamp(from, to, lerpUnclamped(from, to, v));
    }

    public static float clamp(float min, float max, float v){
        if(v < min)
            return min;
        return Math.min(v, max);
    }

}

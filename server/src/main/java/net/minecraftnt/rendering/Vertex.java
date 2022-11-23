package net.minecraftnt.rendering;

import net.minecraftnt.util.maths.Vector2;
import net.minecraftnt.util.maths.Vector3;

public record Vertex(Vector3 pos, Vector2 uv, float lighting) {
}

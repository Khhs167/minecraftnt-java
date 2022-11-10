package net.minecraftnt.client.rendering;

import net.minecraftnt.util.maths.Vector2;
import net.minecraftnt.util.maths.Vector3;

public record Quad(Vector3[] positions, Vector2[] uv, float lighting) {
}

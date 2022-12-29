package net.minecraftnt.rendering;

import java.nio.ByteBuffer;

public record TextureData(ByteBuffer dataBuffer, int width, int height) {
}

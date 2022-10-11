package net.minecraftnt.server.physics;

import net.minecraftnt.util.Vector3;

import java.util.Vector;

public class ConstrainResult {
    public final Vector3 position, velocity;

    public ConstrainResult(Vector3 position, Vector3 velocity) {
        this.position = position;
        this.velocity = velocity;
    }
}

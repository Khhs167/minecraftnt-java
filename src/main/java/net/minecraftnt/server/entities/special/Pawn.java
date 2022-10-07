package net.minecraftnt.server.entities.special;

import net.minecraftnt.client.rendering.Camera;
import net.minecraftnt.server.entities.Entity;
import net.minecraftnt.util.Vector3;

public abstract class Pawn extends Entity {
    public Pawn(Vector3 pos) {
        super(pos);
    }

    public abstract void translateCamera(Camera camera);
}

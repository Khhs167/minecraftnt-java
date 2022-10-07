package net.minecraftnt.server.physics;

import net.minecraftnt.util.Vector3;

public interface Collider {

    boolean shouldConstrain = false;

    void constrain(Vector3 position);
    boolean colliding(Vector3 position);
}

package net.minecraftnt.server.physics;

import net.minecraftnt.util.Vector3;

public interface Collider {

    default boolean shouldConstrain() {
        return false;
    }

    ConstrainResult constrain(Vector3 position, Vector3 velocity);
    boolean colliding(Vector3 position);

}

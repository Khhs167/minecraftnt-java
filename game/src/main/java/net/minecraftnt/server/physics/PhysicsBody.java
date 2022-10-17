package net.minecraftnt.server.physics;

import net.minecraftnt.server.entities.Entity;
import net.minecraftnt.util.Transform;
import net.minecraftnt.util.Vector3;

public class PhysicsBody {
    private ColliderAABB collider;
    private final Transform transform;
    private Vector3 velocity;
    private boolean doSimulate;

    public PhysicsBody(Entity entity) {
        this.transform = entity.getTransform();
        this.velocity = Vector3.zero();
        this.doSimulate = true;
    }


    public PhysicsBody setCollider(ColliderAABB collider) {
        this.collider = collider;
        return this;
    }


    public PhysicsBody setDoSimulate(boolean doSimulate) {
        this.doSimulate = doSimulate;
        return this;
    }

    public void updateBody(){
        if(doSimulate) {
            constrain();
        }
        transform.location.setX((collider.min.getX() + collider.max.getX()) / 2.0F);
        transform.location.setY(collider.min.getY());
        transform.location.setZ((collider.min.getZ() + collider.max.getZ()) / 2.0F);
    }

    public PhysicsBody setVelocity(Vector3 velocity) {
        this.velocity = velocity;
        return this;
    }

    public PhysicsBody addVelocity(Vector3 velocity){
        this.velocity = this.velocity.add(velocity);
        return this;
    }

    public Transform getTransform() {
        return transform;
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public ColliderAABB getCollider() {
        return collider;
    }

    public void constrain(){
        if (collider != null) {
            if (collider.shouldConstrain()) {
                ConstrainResult result = collider.constrain(getTransform().location, velocity);
                velocity = result.velocity;
            }
        }
    }
}

package net.minecraftnt.server.physics;

import net.minecraftnt.server.Minecraft;
import net.minecraftnt.server.entities.Entity;
import net.minecraftnt.util.Transform;
import net.minecraftnt.util.Vector3;

public class PhysicsBody {
    private Collider collider;
    private final Transform transform;
    private Vector3 velocity;
    private boolean doSimulate;

    private final Entity entity;

    public PhysicsBody(Entity entity) {
        this.entity = entity;
        this.transform = this.entity.getTransform();
        this.velocity = Vector3.zero();
        this.doSimulate = true;
    }


    public PhysicsBody setCollider(Collider collider) {
        this.collider = collider;
        return this;
    }

    public boolean colliding(){
        return colliding(getTransform().location);
    }

    public boolean colliding(Vector3 pos){
        if(collider != null)
            return collider.colliding(pos);
        return false;
    }

    public PhysicsBody setDoSimulate(boolean doSimulate) {
        this.doSimulate = doSimulate;
        return this;
    }

    public void updateBody(){
        if(doSimulate) {
            constrain();
        }

        transform.location = transform.location.add(velocity.multiply(entity.getDeltaTime()));
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

    public Collider getCollider() {
        return collider;
    }

    public void constrain(){
        if (collider != null) {
            if (collider.shouldConstrain()) {
                ConstrainResult result = collider.constrain(getTransform().location, velocity);
                getTransform().location = result.position;
                velocity = result.velocity;
            }
        }
    }
}

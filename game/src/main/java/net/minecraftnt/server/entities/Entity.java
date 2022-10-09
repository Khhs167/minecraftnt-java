package net.minecraftnt.server.entities;

import net.minecraftnt.server.Minecraft;
import net.minecraftnt.server.physics.PhysicsBody;
import net.minecraftnt.util.Transform;
import net.minecraftnt.util.Vector3;

public abstract class Entity {
    private Transform transform;
    private PhysicsBody physicsBody;
    public Transform getTransform(){
        return transform;
    }
    public PhysicsBody getPhysicsBody() { return physicsBody; }
    public PhysicsBody addPhysicsBody(boolean override){
        if(override) {
            physicsBody = new PhysicsBody(this);
        } else {
            if(physicsBody == null)
                physicsBody = new PhysicsBody(this);
        }
        return physicsBody;
    }

    public PhysicsBody addPhysicsBody() {
        return addPhysicsBody(false);
    }
    
    public Entity(Vector3 pos){
        this.transform = new Transform(pos);
    }
    public void update(){
        getPhysicsBody().updateBody();
    }

    public float getDeltaTime(){
        return Minecraft.getInstance().getDeltaTime();
    }
}

package net.minecraftnt.server.entities;

import net.minecraftnt.server.Minecraft;
import net.minecraftnt.server.physics.ColliderAABB;
import net.minecraftnt.server.physics.PhysicsBody;
import net.minecraftnt.util.Transform;
import net.minecraftnt.util.Vector3;

public abstract class Entity {
    private Transform transform;
    private PhysicsBody physicsBody;
    
    protected float boundingBoxSizeX = 0.6f;
    protected float boundingBoxSizeY = 1.8f;
    public float eyesOffset = 1.6f;
    public boolean isGrounded;
    
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
        setPos(pos);
    }
    public void setPos(Vector3 pos) {
    	float halfWidth = boundingBoxSizeX / 2.0F;
        float halfHeight = boundingBoxSizeY / 2.0F;
    	addPhysicsBody().setCollider(new ColliderAABB(
    			new Vector3(pos.getX() - halfWidth, pos.getY() - halfHeight, pos.getZ() - halfWidth),
    			new Vector3(pos.getX() + halfWidth, pos.getY() + halfHeight, pos.getZ() + halfWidth)));
    }
    public void update(){
    	
    	float originalYVel = getPhysicsBody().getVelocity().getY();
    	
        getPhysicsBody().updateBody();
        
        float YVel = getPhysicsBody().getVelocity().getY();
        
        isGrounded = originalYVel != YVel && originalYVel < 0.0F && Math.abs(YVel) < 0.01;
        
        ColliderAABB bb = getPhysicsBody().getCollider();
        transform.location.setX((bb.min.getX() + bb.max.getX()) / 2.0F);
        transform.location.setY(bb.min.getY());
        transform.location.setZ((bb.min.getZ() + bb.max.getZ()) / 2.0F);
    }

    public float getDeltaTime(){
        return Minecraft.getInstance().getDeltaTime();
    }
}

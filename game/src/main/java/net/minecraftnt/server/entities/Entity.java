package net.minecraftnt.server.entities;

import net.minecraftnt.server.Minecraft;
import net.minecraftnt.server.physics.ColliderAABB;
import net.minecraftnt.server.physics.PhysicsBody;
import net.minecraftnt.util.Transform;
import net.minecraftnt.util.Vector3;

public abstract class Entity {
    private final Transform transform;
    private PhysicsBody physicsBody;

    private float boundsX;
    private float boundsY;
    
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
    }
    
    public void setPosition(Vector3 pos) {
    	this.transform.location = pos;
    	this.createAABB(boundsX, boundsY);
    }
    
    public void createAABB(float boundingBoxSizeX, float boundingBoxSizeY) {
    	float halfWidth = boundingBoxSizeX / 2.0F;
        float halfHeight = boundingBoxSizeY / 2.0F;
        this.boundsX = boundingBoxSizeX;
        this.boundsY = boundingBoxSizeY;
        Vector3 pos = transform.location;
    	addPhysicsBody().setCollider(new ColliderAABB(
    			new Vector3(pos.getX() - halfWidth, pos.getY() - halfHeight, pos.getZ() - halfWidth),
    			new Vector3(pos.getX() + halfWidth, pos.getY() + halfHeight, pos.getZ() + halfWidth)));
    }
    public void update(){
    	
    	float originalYVel = getPhysicsBody().getVelocity().getY();
    	
        getPhysicsBody().updateBody();
        
        float YVel = getPhysicsBody().getVelocity().getY();
        
        isGrounded = originalYVel != YVel && originalYVel < 0.0F && Math.abs(YVel) < 0.01f;

    }

    public float getDeltaTime(){
        return Minecraft.getInstance().getDeltaTime();
    }
}

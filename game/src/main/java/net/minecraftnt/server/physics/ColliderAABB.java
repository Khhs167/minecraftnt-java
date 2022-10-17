package net.minecraftnt.server.physics;

import java.util.ArrayList;
import java.util.List;

import net.minecraftnt.server.Minecraft;
import net.minecraftnt.server.blocks.Block;
import net.minecraftnt.util.Vector3;
import net.minecraftnt.util.Vector3I;
import net.minecraftnt.util.registries.Registry;

public class ColliderAABB implements Collider {

    public Vector3 min;
    public Vector3 max;
    
    static final float epsilon = 3.0f;

    public ColliderAABB(Vector3 min, Vector3 max){
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean shouldConstrain() {
        return true;
    }

    @Override
    public ConstrainResult constrain(Vector3 position, Vector3 velocity) {
        
    	List<ColliderAABB> boxes = GetBoxes();
    	
    	velocity = velocity.multiply(0.98F);

        for (ColliderAABB box : boxes) {

            velocity.setX(box.ClipXVelocity(velocity.getX(), this));
            velocity.setY(box.ClipYVelocity(velocity.getY(), this));
            velocity.setZ(box.ClipZVelocity(velocity.getZ(), this));

        }
    	
    	min = min.add(velocity);
    	max = max.add(velocity);

        return new ConstrainResult(position, velocity);
    }
    
    float ClipXVelocity(float vel, ColliderAABB box) {
    	
    	//Checks if box y and z are inside this aabb
    	
    	if(box.max.getY() <= min.getY() || box.min.getY() >= max.getY())
        {
            return vel;
        }
        if(box.max.getZ() <= min.getZ() || box.min.getZ() >= max.getZ())
        {
            return vel;
        }

        // If the aabb is inside on this axis clip the velocity
        
        if (vel > 0.0 && box.max.getX() <= min.getX()) {
        	float clipVel = min.getX() - box.max.getX();
            if (clipVel < vel) vel = clipVel;
        }
        
        if (vel < 0.0 && box.min.getX() >= max.getX()) {
        	float clipVel = max.getX() - box.min.getX();
            if (clipVel > vel) vel = clipVel;
        }
        return vel;
    }
    
    float ClipZVelocity(float vel, ColliderAABB box) {
    	
    	//Checks if box x and y are inside this aabb
    	
    	if(box.max.getY() <= min.getY() || box.min.getY() >= max.getY())
        {
            return vel;
        }
        if(box.max.getX() <= min.getX() || box.min.getX() >= max.getX())
        {
            return vel;
        }

        // If the aabb is inside on this axis clip the velocity
        
        if (vel > 0.0 && box.max.getZ() <= min.getZ()) {
        	float clipVel = min.getZ() - box.max.getZ();
            if (clipVel < vel) vel = clipVel;
        }
        
        if (vel < 0.0 && box.min.getZ() >= max.getZ()) {
        	float clipVel = max.getZ() - box.min.getZ();
            if (clipVel > vel) vel = clipVel;
        }
        
        return vel;
    }
    
    float ClipYVelocity(float vel, ColliderAABB box) {
    	
    	//Checks if box x and z are inside this aabb
    	
    	if(box.max.getX() <= min.getX() || box.min.getX() >= max.getX())
        {
            return vel;
        }
        if(box.max.getZ() <= min.getZ() || box.min.getZ() >= max.getZ())
        {
            return vel;
        }
        
        // If the aabb is inside on this axis clip the velocity

        if (vel > 0.0F && box.max.getY() <= min.getY()) {
        	float clipVel = min.getY() - box.max.getY();
            if (clipVel < vel) vel = clipVel;
        }
        
        if (vel < 0.0F && box.min.getY() >= max.getY()) {
        	float clipVel = max.getY() - box.min.getY();
            if (clipVel > vel) vel = clipVel;
        }
        
        return vel;
    }

    @Override
    public boolean colliding(Vector3 position) {
        return true;
    }
    
    List<ColliderAABB> GetBoxes() {
    	ArrayList<ColliderAABB> boxes = new ArrayList<>();
    	
    	final float VOXEL_SIZE = 1.0F;
    	
    	for (float x = min.getX() - epsilon; x <= max.getX() + epsilon; x += VOXEL_SIZE) {
    		for (float y = min.getY() - epsilon; y <= max.getY() + epsilon; y += VOXEL_SIZE) {
    			for (float z = min.getZ() - epsilon; z <= max.getZ() + epsilon; z += VOXEL_SIZE) {
    	    		
    				if (!getBlock(x, y, z)) continue;
    				
    				int xx = (int)Math.floor(x);
    				int yy = (int)Math.floor(y);
    				int zz = (int)Math.floor(z);

                    Block block = Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(new Vector3I(xx, yy, zz)));
    				
    				ColliderAABB box = block.getBoundingBox(new Vector3(xx, yy, zz));
    				boxes.add(box);
    				
    	    	}
        	}
    	}
    	
    	return boxes;
    }

    private boolean getBlock(float x, float y, float z){
        return Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(new Vector3I((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z)))).hasCollisions();
    }
}

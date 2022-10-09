package net.minecraftnt.server.physics;

import net.minecraftnt.server.Minecraft;
import net.minecraftnt.util.Vector3;
import net.minecraftnt.util.Vector3I;
import net.minecraftnt.util.registries.Registry;

public class ColliderAABB implements Collider {

    public Vector3 origin;
    public Vector3 size;

    public ColliderAABB(Vector3 origin, Vector3 size){
        this.origin = origin;
        this.size = size;
    }

    @Override
    public void constrain(Vector3 position) {

    }

    @Override
    public boolean colliding(Vector3 position) {
        for(int x = 0; x < size.getX(); x++){
            for (int y = 0; y < size.getY(); y++){
                for(int z = 0; z < size.getZ(); z++){
                    if(getBlock(position.add(origin).addX(x).addY(y).addZ(z)))
                        return true;
                }
            }
        }
        return getBlock(position.add(size));
    }


    private boolean getBlock(float x, float y, float z){
        return Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(new Vector3I((int)x, (int)y, (int)z))).hasCollisions();
    }
    private boolean getBlock(Vector3 p){
        return Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(p.floor())).hasCollisions();
    }
}

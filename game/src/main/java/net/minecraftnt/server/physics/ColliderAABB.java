package net.minecraftnt.server.physics;

import net.minecraftnt.server.Minecraft;
import net.minecraftnt.server.blocks.Block;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.Vector3;
import net.minecraftnt.util.Vector3I;
import net.minecraftnt.util.registries.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class ColliderAABB implements Collider {

    public Vector3 origin;
    public Vector3 size;

    private static final Logger LOGGER = LogManager.getLogger(ColliderAABB.class);


    public ColliderAABB(Vector3 origin, Vector3 size){
        this.origin = origin;
        this.size = size;
    }

    @Override
    public boolean shouldConstrain() {
        return true;
    }

    public float clipXVelocity(float velocity, ColliderAABB box){


        return velocity;

    }


    @Override
    public ConstrainResult constrain(Vector3 position, Vector3 velocity) {

        Vector3 deltaVelocity = velocity.multiply(Minecraft.getInstance().getDeltaTime());

        // Thanks to "ImSheeshCommando's" for this solution! If you wish to change it to a proper username, just go in and do it lol.

        var boundingBoxes = getCollidingBlockBoundingBoxes(position);

        for(ColliderAABB blockAABB : boundingBoxes){
            LOGGER.debug("{},{} colliding: {}", blockAABB.origin.toString(), blockAABB.size.toString(), colliding(blockAABB));
        }



        // TODO: Write a replacement for this code and delete it ig.
        /*

        // Old method

        if(colliding(position.add(velocity.multiply(Minecraft.getInstance().getDeltaTime())))){
            Vector3 direction = velocity.normalize();

            final float step_size = 0.01f;
            final int max_steps = 200;

            int c = 0;

            while(c < max_steps && !colliding(position)){

                position = position.add(direction.multiply(step_size));

                c++;
            }
            if(c < max_steps)
                position = position.add(direction.multiply(-step_size));

            velocity = Vector3.zero();

        }*/

        // TODO: Maybe not in here, but add divide and add it in here!

        // We have no vector3.divide yet because I am lazy. Multiply by 1 / n instead
        return new ConstrainResult(position, deltaVelocity.multiply(1 / Minecraft.getInstance().getDeltaTime()));
    }

    public ArrayList<ColliderAABB> getCollidingBlockBoundingBoxes(Vector3 position){
        ArrayList<ColliderAABB> boundingBoxes = new ArrayList<>();

        for(int x = 0; x < size.getX(); x++){
            for (int y = 0; y < size.getY(); y++){
                for(int z = 0; z < size.getZ(); z++){

                    Vector3 blockPos = position.add(origin).addX(x).addY(y).addZ(z);
                    Block block = getBlockClass(blockPos);
                    if(block.hasCollisions())
                        boundingBoxes.add(block.getBoundingBox(blockPos));
                }
            }
        }

        return boundingBoxes;
    }

    public ArrayList<Block> getCollidingBlocks(Vector3 position) {
        ArrayList<Block> blocks = new ArrayList<>();

        for(int x = 0; x < size.getX(); x++){
            for (int y = 0; y < size.getY(); y++){
                for(int z = 0; z < size.getZ(); z++){
                    blocks.add(getBlockClass(position.add(origin).addX(x).addY(y).addZ(z)));
                }
            }
        }

        return blocks;
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

    public boolean colliding(ColliderAABB box){

        return  this.origin.getX() <= box.origin.getX() + box.size.getX() &&
                this.origin.getX() + this.size.getX() >= box.origin.getX() &&

                this.origin.getY() <= box.origin.getY() + box.size.getY() &&
                this.origin.getY() + this.size.getY() >= box.origin.getY() &&

                this.origin.getZ() <= box.origin.getZ() + box.size.getZ() &&
                this.origin.getZ() + this.size.getZ() >= box.origin.getZ();
    }


    private boolean getBlock(float x, float y, float z){
        return Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(new Vector3I((int)x, (int)y, (int)z))).hasCollisions();
    }

    private Block getBlockClass(float x, float y, float z){
        return Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(new Vector3I((int)x, (int)y, (int)z)));
    }

    private Block getBlockClass(Vector3 p){
        return Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(p.floor()));
    }

    private boolean getBlock(Vector3 p){
        return Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(p.floor())).hasCollisions();
    }
}

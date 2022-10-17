package net.minecraftnt.server.entities;

import net.minecraftnt.client.ClientMainHandler;
import net.minecraftnt.client.rendering.Camera;
import net.minecraftnt.server.Minecraft;
import net.minecraftnt.server.blocks.Block;
import net.minecraftnt.server.entities.special.Pawn;
import net.minecraftnt.server.physics.PhysicsSettings;
import net.minecraftnt.server.world.World;
import net.minecraftnt.util.*;
import net.minecraftnt.util.input.KeyboardInput;
import net.minecraftnt.util.input.MouseInput;
import net.minecraftnt.util.registries.Registry;

public class PlayerEntity extends Pawn  {

    public static final Identifier IDENTIFIER = new Identifier("minecraft", "player");
    private float cameraRotation = 0;
    
    private static final float fixedDeltaTime = 1.0F / 50.0F;
    private float acc = 0.0F;



    public PlayerEntity(Vector3 pos) {
        super(pos);
        this.createAABB(0.6f, 1.8f);
    }

    @Override
    public void update() {

        KeyboardInput keyboardInput = ClientMainHandler.getKeyboardInput();

        acc += getDeltaTime();
        if (acc > 0.5f) acc = 0.5f;
        
        while (acc > fixedDeltaTime) {
        	
        	getPhysicsBody().addVelocity(PhysicsSettings.gravity.multiply(fixedDeltaTime).multiply(0.25f));
        	
        	if(this.isGrounded && keyboardInput.isKeyDown(KeyboardInput.KEY_JUMP)){
                float jumpForce = 0.25f;
                getPhysicsBody().addVelocity(Vector3.up().multiply(jumpForce));
            }

            float yVel = 0;
            float xVel = 0;

            if(keyboardInput.isKeyDown(KeyboardInput.KEY_FORWARD))
                yVel += 1;

            if(keyboardInput.isKeyDown(KeyboardInput.KEY_BACKWARDS))
                yVel -= 1;

            if(keyboardInput.isKeyDown(KeyboardInput.KEY_LEFT))
                xVel += 1;

            if(keyboardInput.isKeyDown(KeyboardInput.KEY_RIGHT))
                xVel -= 1;

            float walkSpeed = 4.317f;
            float movementSpeed = walkSpeed;

            float sprintSpeed = 6.612f;
            if(keyboardInput.isKeyDown(KeyboardInput.KEY_SPRINT))
                movementSpeed = sprintSpeed;


            Vector3 hVel = Vector3.zero();
            if(xVel != 0 || yVel != 0)
                hVel = hVel.add(getTransform().getForward().multiply(yVel)).add(getTransform().getRight().multiply(xVel)).normalize().multiply(movementSpeed * fixedDeltaTime);

            getPhysicsBody().getVelocity().setX(hVel.getX());
            getPhysicsBody().getVelocity().setZ(hVel.getZ());

            super.update();
        	
        	acc -= fixedDeltaTime;
        }

        mouseMove();

        //LOGGER.info("Colliding: {}, {}", getPhysicsBody().colliding(), footCollider.colliding(getTransform().location));
    }

    private void mouseMove(){
        MouseInput mouse = ClientMainHandler.getMouseInput();

        getTransform().rotate(Vector3.up().multiply(-mouse.getX() * 30));
        cameraRotation += mouse.getY() * 30;

        cameraRotation = Math.max(Math.min(cameraRotation, 89), -89);

        Vector3 pos = getTransform().location;
        Vector3 dir = ClientMainHandler.getInstance().getCamera().getForward().normalize();
        float epsilon = 0.1f;
        float max_length = 10;
        int max_steps = (int) (max_length / epsilon);
        int step = 0;

        while (step < max_steps){
            pos = pos.add(dir.multiply(epsilon));

            Block block = Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(pos.floor()));

            if(block.placeableSurface()){
                pos = pos.add(dir.multiply(epsilon));
                ClientMainHandler.getInstance().setPlacementPosition(pos.floor().toFloat());
                break;
            }

            step++;

        }

    }
    @Override
    public void translateCamera(Camera camera) {
        camera.getTransform().location = getTransform().location.clone().add(Vector3.up().multiply(1.8f));
        camera.getTransform().rotation = getTransform().rotation.clone().add(Vector3.right().multiply(cameraRotation));
    }
}

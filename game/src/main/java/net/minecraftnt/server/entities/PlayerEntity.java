package net.minecraftnt.server.entities;

import net.minecraftnt.client.ClientMainHandler;
import net.minecraftnt.client.rendering.Camera;
import net.minecraftnt.server.entities.special.Pawn;
import net.minecraftnt.server.physics.ColliderAABB;
import net.minecraftnt.server.physics.PhysicsSettings;
import net.minecraftnt.util.*;
import net.minecraftnt.util.input.KeyboardInput;
import net.minecraftnt.util.input.MouseInput;

public class PlayerEntity extends Pawn  {

    public static final Identifier IDENTIFIER = new Identifier("minecraft", "player");


    private final float sprintSpeed = 5.612f;
    private final float walkSpeed = 9.317f;
    private final float jumpForce = 0.5f;
    private float cameraRotation = 0;
    public float gravity = -13f;
    
    private static final float fixedDeltaTime = 1.0F / 50.0F;
    private float acc = 0.0F;

    public ColliderAABB footCollider;

    public PlayerEntity(Vector3 pos) {
        super(pos);
        footCollider = new ColliderAABB(Vector3.zero().addX(0.1f).addZ(0.1f).addY(-0.1f), new Vector3(0.8f, 0.2f, 0.8f));
    }

    @Override
    public void update() {

        KeyboardInput keyboardInput = ClientMainHandler.getKeyboardInput();

        acc += getDeltaTime();
        if (acc > 0.5f) acc = 0.5f;
        
        while (acc > fixedDeltaTime) {
        	
        	getPhysicsBody().addVelocity(PhysicsSettings.gravity.multiply(fixedDeltaTime).multiply(0.25f));
        	
        	if(this.isGrounded && keyboardInput.isKeyDown(KeyboardInput.KEY_JUMP)){
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

            Vector3 hVel = Vector3.zero();
            if(xVel != 0 || yVel != 0)
                hVel = hVel.add(getTransform().getForward().multiply(yVel)).add(getTransform().getRight().multiply(xVel)).normalize().multiply(walkSpeed * fixedDeltaTime);

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
    }
    @Override
    public void translateCamera(Camera camera) {
        camera.getTransform().location = getTransform().location.clone().add(Vector3.up().multiply(1.8f));
        camera.getTransform().rotation = getTransform().rotation.clone().add(Vector3.right().multiply(cameraRotation));
    }
}

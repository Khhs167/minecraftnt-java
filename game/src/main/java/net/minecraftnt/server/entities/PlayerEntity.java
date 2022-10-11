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
    private final float walkSpeed = 4.317f;
    private final float jumpForce = 6f;
    private float cameraRotation = 0;
    public float gravity = -13f;

    public ColliderAABB footCollider;

    public PlayerEntity(Vector3 pos) {
        super(pos);
        addPhysicsBody().setCollider(new ColliderAABB(Vector3.zero(), new Vector3(1, 1.8f, 1)));
        footCollider = new ColliderAABB(Vector3.zero().addX(0.1f).addZ(0.1f).addY(-0.1f), new Vector3(0.8f, 0.2f, 0.8f));
    }

    @Override
    public void update() {

        KeyboardInput keyboardInput = ClientMainHandler.getKeyboardInput();

        getPhysicsBody().addVelocity(PhysicsSettings.gravity.multiply(getDeltaTime()));


        if(footCollider.colliding(getTransform().location) && keyboardInput.isKeyDown(KeyboardInput.KEY_JUMP)){
            getPhysicsBody().addVelocity(Vector3.up().multiply(jumpForce));
            getTransform().location = getTransform().location.addY(0.1f);
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
            hVel = hVel.add(getTransform().getForward().multiply(yVel)).add(getTransform().getRight().multiply(xVel)).normalize().multiply(walkSpeed);

        getPhysicsBody().getVelocity().setX(hVel.getX());
        getPhysicsBody().getVelocity().setZ(hVel.getZ());

        super.update();

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

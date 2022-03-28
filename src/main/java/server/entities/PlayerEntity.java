package server.entities;

import client.Camera;
import server.Minecraft;
import server.blocks.Block;
import server.entities.special.Pawn;
import util.*;
import util.input.KeyboardInput;
import util.input.MouseInput;
import util.registries.Registry;

import static org.lwjgl.glfw.GLFW.*;
import static server.Minecraft.getKeyboardInput;

public class PlayerEntity extends Pawn  {

    public static final Identifier IDENTIFIER = new Identifier("minecraft", "player");

    private boolean isGrounded;
    private boolean isSprinting;

    private final float sprintSpeed = 10f;

    private Vector3 velocity = new Vector3();

    private float cameraRotation = 0;

    public PlayerEntity(Vector3 pos) {
        super(pos);
    }

    @Override
    public void update() {
        KeyboardInput keyboard = getKeyboardInput();

        Vector3 horizontalVelocity = new Vector3();
        if(keyboard.isKeyDown(GLFW_KEY_W))
            horizontalVelocity = getTransform().getForward();
        if(keyboard.isKeyDown(GLFW_KEY_S))
            horizontalVelocity = horizontalVelocity.add(getTransform().getForward().negate());
        if(keyboard.isKeyDown(GLFW_KEY_A))
            horizontalVelocity = horizontalVelocity.add(getTransform().getRight());
        if(keyboard.isKeyDown(GLFW_KEY_D))
            horizontalVelocity = horizontalVelocity.add(getTransform().getRight().negate());

        float walkSpeed = 5f;
        horizontalVelocity = horizontalVelocity.normalize().multiply(walkSpeed);

        velocity.setX(horizontalVelocity.getX());
        velocity.setZ(horizontalVelocity.getZ());

        float gravity = -15f;
        velocity.setY(velocity.getY() + gravity * Minecraft.getInstance().getDeltaTime());


        if(horizontalVelocity.length() > 0) {
            int loopCount = 0;
            while (checkHorizontalCollissions() && loopCount < 50) {
                velocity = velocity.add(velocity.negate().multiply(1f));
                loopCount++;
            }

            getTransform().move(velocity.xz().Vec3XZ().multiply(Minecraft.getInstance().getDeltaTime()));
            //getTransform().move(Vector3.right().multiply(velocity.getX() * Minecraft.getInstance().getDeltaTime()));
            /*boolean hasDoneZJump = false;
            while (raycast(getTransform().location.add(Vector3.up().multiply(-1.79f)), Vector3.forward(),
                    width)) {
                hasDoneZJump = true;
                velocity.setZ(velocity.getZ() - velocity.getZ() * 0.1f);
            }

            if(hasDoneZJump)
                velocity.setZ(0);

            getTransform().move(Vector3.forward().multiply(velocity.getZ() * Minecraft.getInstance().getDeltaTime()));*/
        }

        boolean hasDoneYJump = false;
        int loopCount = 0;
        while(raycast(getTransform().location.add(Vector3.up().multiply(-1.8f)), Vector3.down(),
                0.2f) && loopCount < 50
        ){
            velocity.setY(velocity.getY() - 0.1f);
            loopCount++;
            hasDoneYJump = true;
        }

        if(hasDoneYJump)
            velocity.setY(0);

        // Because we have to jump after resetting the pos and velocity!

        if(raycast(getTransform().location.add(Vector3.down().multiply(1.7f)), Vector3.down(), 1.1f) && keyboard.isKeyDown(GLFW_KEY_SPACE) && velocity.getY() == 0) {
            float jumpForce = 6f;
            velocity.setY(jumpForce);
        }

        getTransform().move(Vector3.up().multiply(velocity.getY() * Minecraft.getInstance().getDeltaTime()));




        MouseInput mouse = Minecraft.getMouseInput();

        getTransform().rotate(Vector3.up().multiply(-mouse.getX() * 30));
        cameraRotation += mouse.getY() * 30;

        cameraRotation = Math.max(Math.min(cameraRotation, 89), -89);

        var lookPos = updatePlacement();
        var lookPosInc = updatePlacementInclusive();

        if(lookPos != null){
            Minecraft.getInstance().setPlacementPosition(new Vector3(lookPos.getX(), lookPos.getY(), lookPos.getZ()));
            if(mouse.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)){
                Minecraft.getInstance().getWorld().setBlock(lookPos, Block.IDENTIFIER_COBBLESTONE);
            }
            if(mouse.isMouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT)){
                assert lookPosInc != null;
                Minecraft.getInstance().getWorld().setBlock(lookPosInc, Block.IDENTIFIER_AIR);
            }
        } else{
            Minecraft.getInstance().setPlacementPosition(Vector3.zero());
        }
    }

    private boolean checkHorizontalCollissions(){
        final float width = 0.1f;

        return raycast(getTransform().location.add(Vector3.up().multiply(-1.79f)), velocity.normalize(),
                width + velocity.length() * Minecraft.getInstance().getDeltaTime()) ||
                raycast(getTransform().location.add(Vector3.up().multiply(-1f)), velocity.normalize(),
                        width + velocity.length() * Minecraft.getInstance().getDeltaTime()) ||
                raycast(getTransform().location.add(Vector3.up().multiply(-0f)), velocity.normalize(),
                        width + velocity.length() * Minecraft.getInstance().getDeltaTime());
    }

    private Vector3I updatePlacement(){
        Vector3 pos = getTransform().location.clone();
        final float maxLength = 20;
        final float stepSize = 0.1f;
        Vector3 direction = Minecraft.getInstance().getCamera().getForward().normalize();
        float distance = 0;

        while (distance < maxLength){
            if(Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(pos.floor())).hasCollisions()){
                pos = pos.add(direction.negate().multiply(stepSize));

                return pos.floor();
            }

            pos = pos.add(direction.multiply(stepSize));
            distance += stepSize;
        }

        return null;
    }

    private Vector3 raycastPosition(Vector3 pos, Vector3 direction, float maxLength){
        final float stepSize = 0.1f;
        float distance = 0;

        while (distance < maxLength){
            if(Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(pos.floor())).hasCollisions()){
                return pos;
            }

            pos = pos.add(direction.multiply(stepSize));
            distance += stepSize;
        }

        return null;
    }

    private float raycastLength(Vector3 pos, Vector3 direction, float maxLength){
        final float stepSize = 0.1f;
        float distance = 0;

        while (distance < maxLength){
            if(Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(pos.floor())).hasCollisions()){
                return distance;
            }

            pos = pos.add(direction.multiply(stepSize));
            distance += stepSize;
        }

        return -1;
    }

    private boolean raycast(Vector3 pos, Vector3 direction, float maxLength){
        final float stepSize = 0.1f;
        float distance = 0;

        while (distance < maxLength){
            if(Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(pos.floor())).hasCollisions()){
                return true;
            }

            pos = pos.add(direction.multiply(stepSize));
            distance += stepSize;
        }

        return false;
    }

    private Vector3I updatePlacementInclusive(){
        Vector3 pos = getTransform().location.clone();
        final float maxLength = 20;
        final float stepSize = 0.1f;
        Vector3 direction = Minecraft.getInstance().getCamera().getForward().normalize();
        float distance = 0;

        while (distance < maxLength){
            if(Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(pos.floor())).hasCollisions()){

                return pos.floor();
            }

            pos = pos.add(direction.multiply(stepSize));
            distance += stepSize;
        }

        return null;
    }

    public boolean hasCollisionOffset(Vector3 offset){
        return Registry.BLOCKS.get(Minecraft.getInstance().getWorld().getBlock(getTransform().location.add(offset).floor())).hasCollisions();
    }

    @Override
    public void translateCamera(Camera camera) {
        camera.getTransform().location = getTransform().location.clone();
        camera.getTransform().rotation = getTransform().rotation.clone().add(Vector3.right().multiply(cameraRotation));
    }

    public Vector3 getVelocity() {
        return velocity;
    }
}

package net.minecraftnt.util.input;

import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.registries.Registry;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardInput {

    private final boolean[] keyboard = new boolean[GLFW_KEY_LAST];
    private final ArrayList<Integer> pressedKeys = new ArrayList<>();

    /**
     * The jump key(GLFW_KEY_SPACE by default)
     */
    public static final Identifier KEY_JUMP = Registry.KEYBOARD_MAP.addIdentifier("minecraft", "jump", GLFW_KEY_SPACE);

    /**
     * The quick-close key(GLFW_KEY_F4 by default)
     */
    public static final Identifier KEY_CLOSE = Registry.KEYBOARD_MAP.addIdentifier("minecraft", "close", GLFW_KEY_F4);

    /**
     * The mouse-free/pause key(GLFW_KEY_ESCAPE by default)
     */
    public static final Identifier KEY_FREE_MOUSE = Registry.KEYBOARD_MAP.addIdentifier("minecraft", "free_mouse", GLFW_KEY_ESCAPE);

    /**
     * The forward key(GLFW_KEY_W by default)
     */
    public static final Identifier KEY_FORWARD = Registry.KEYBOARD_MAP.addIdentifier("minecraft", "forward", GLFW_KEY_W);

    /**
     * The forward key(GLFW_KEY_W by default)
     */
    public static final Identifier KEY_BACKWARDS = Registry.KEYBOARD_MAP.addIdentifier("minecraft", "backwards", GLFW_KEY_S);

    /**
     * The forward key(GLFW_KEY_W by default)
     */
    public static final Identifier KEY_LEFT = Registry.KEYBOARD_MAP.addIdentifier("minecraft", "left", GLFW_KEY_A);

    /**
     * The forward key(GLFW_KEY_W by default)
     */
    public static final Identifier KEY_RIGHT = Registry.KEYBOARD_MAP.addIdentifier("minecraft", "right", GLFW_KEY_D);

    /**
     * The sprint key(GLFW_KEY_LEFT_SHIFT by default)
     */
    public static final Identifier KEY_SPRINT = Registry.KEYBOARD_MAP.addIdentifier("minecraft", "sprint", GLFW_KEY_LEFT_SHIFT);


    public void update(){
        pressedKeys.clear();
    }

    /**
     * Internal function to set if a key is currently pressed
     * @param id The ID of the key being pressed(e.g. GLFW_KEY_SPACE)
     */
    public void keyPress(int id){
        keyboard[id] = true;
        pressedKeys.add(id);
    }

    /**
     * Internal function to set if a key is currently released
     * @param id The ID of the key being released(e.g. GLFW_KEY_SPACE)
     */
    public void keyRelease(int id){
        keyboard[id] = false;
    }

    /**
     * Gets if a key is currently down.
     * @param key The identifier for the key being held down(e.g. KEY_JUMP)
     * @return If the key is down
     */
    public boolean isKeyDown(Identifier key){
        return keyboard[Registry.KEYBOARD_MAP.get(key)];
    }

    /**
     * Gets if a key is pressed(returns true first frame since press)
     * @param key The identifier for the key being pressed(e.g. KEY_JUMP)
     * @return If the key was pressed this frame
     */
    public boolean isKeyPressed(Identifier key){
        return pressedKeys.contains(Registry.KEYBOARD_MAP.get(key));
    }
}

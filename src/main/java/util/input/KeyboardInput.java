package util.input;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;

public class KeyboardInput {
    private boolean[] keyboard = new boolean[GLFW_KEY_LAST];
    private ArrayList<Integer> pressedKeys = new ArrayList<Integer>();

    public void update(){
        pressedKeys.clear();
    }

    public void keyPress(int id){
        keyboard[id] = true;
        pressedKeys.add(id);
    }

    public void keyRelease(int id){
        keyboard[id] = false;
    }

    public boolean isKeyDown(int id){
        return keyboard[id];
    }

    public boolean isKeyPressed(int id){
        return pressedKeys.contains(id);
    }
}

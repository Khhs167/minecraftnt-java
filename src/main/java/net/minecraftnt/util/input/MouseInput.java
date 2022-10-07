package net.minecraftnt.util.input;

import net.minecraftnt.client.rendering.Window;
import net.minecraftnt.util.Vector2;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {
    private Vector2 normalizedInput = new Vector2();
    private Vector2 lastInput = new Vector2();

    private boolean[] mouse = new boolean[GLFW_MOUSE_BUTTON_LAST];
    private ArrayList<Integer> pressedButtons = new ArrayList<Integer>();

    public void onInput(Vector2 input, Window window){

        float xoffset = input.getX() - lastInput.getX();
        float yoffset = input.getY() - lastInput.getY();
        lastInput = input.clone();

        xoffset /= (float)window.getSize().getX();
        yoffset /= (float)window.getSize().getY();

        normalizedInput = normalizedInput.add(new Vector2(xoffset, yoffset));
    }

    public void update(){
        normalizedInput = new Vector2();
        pressedButtons.clear();
    }

    public float getX() {return normalizedInput.getX();}
    public float getY() {return normalizedInput.getY();}

    public void mouseClick(int id){
        mouse[id] = true;
        pressedButtons.add(id);
    }

    public void mouseRelease(int id){
        mouse[id] = false;
    }

    public boolean isMouseButtonDown(int id){
        return mouse[id];
    }

    public boolean isMouseButtonPressed(int id){
        return pressedButtons.contains(id);
    }
}

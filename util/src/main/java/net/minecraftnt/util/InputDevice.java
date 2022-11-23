package net.minecraftnt.util;

import java.util.ArrayList;

public class InputDevice {

    public InputDevice(int keys) {
        keyMap = new boolean[keys];
    }

    private final boolean[] keyMap;
    private final ArrayList<Integer> currentFramePresses = new ArrayList<>();
    private final ArrayList<Integer> currentFrameReleases = new ArrayList<>();

    public boolean isDown(int id) {
        return keyMap[id];
    }

    public boolean isPressed(int id) {
        return currentFramePresses.contains(id);
    }

    public boolean isUp(int id) {
        return !isDown(id);
    }

    public boolean isReleased(int id) {
        return currentFrameReleases.contains(id);
    }

    public void pressKey(int id) {
        keyMap[id] = true;
        currentFramePresses.add(id);
    }

    public void releaseKey(int id) {
        keyMap[id] = false;
        currentFrameReleases.add(id);
    }

    public void step() {
        currentFramePresses.clear();
        currentFrameReleases.clear();
    }

}

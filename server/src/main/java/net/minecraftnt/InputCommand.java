package net.minecraftnt;

public class InputCommand {
    public String command;
    public float value;

    public InputCommand(String command) {
        this.command = command;
        this.value = 0;
    }

    public InputCommand(String command, float value) {
        this.command = command;
        this.value = value;
    }

    public InputCommand() {
        this.command = "";
        this.value = 0;
    }
}

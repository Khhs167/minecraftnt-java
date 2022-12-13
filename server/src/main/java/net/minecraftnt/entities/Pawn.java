package net.minecraftnt.entities;

import net.minecraftnt.InputCommand;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.Transformation;

import java.util.LinkedList;
import java.util.Queue;

public abstract class Pawn extends Entity {

    private final Queue<InputCommand> inputs = new LinkedList<>();
    public void registerInput(InputCommand input) {
        inputs.add(input);
    }

    protected InputCommand poll() {
        return inputs.poll();
    }

    public abstract Transformation transform(Transformation transformation, float deltaTime);
}

package net.minecraftnt.builtin.entities;

import net.minecraftnt.InputCommand;
import net.minecraftnt.entities.Entity;
import net.minecraftnt.entities.Pawn;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.Transformation;

public class CameraFlightPawn extends Pawn {
    public static final Identifier IDENTIFIER = new Identifier("minecraftnt", "pawns.flight");
    @Override
    public Entity create() {
        return null;
    }

    @Override
    public Transformation transform(Transformation transformation, float deltaTime) {
        InputCommand command;
        while ((command = poll()) != null) {
            switch (command.command) {
                case "forward" -> transformation.move(transformation.forward().multiply(deltaTime * 10));
                case "backward" -> transformation.move(transformation.forward().negated().multiply(deltaTime * 10));
                case "right" -> transformation.move(transformation.right().multiply(deltaTime * 10));
                case "left" -> transformation.move(transformation.right().multiply(deltaTime * -10));
            }
        }

        return transformation;
    }
}

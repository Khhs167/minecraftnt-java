package net.minecraftnt.rendering;

import net.minecraftnt.util.FaceFlags;
import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.maths.Vector3;
import net.minecraftnt.world.Block;

import java.util.ArrayList;
import java.util.Collection;

public interface ShapeGenerator {

    Identifier BLOCK = new Identifier("minecraftnt", "shapegen.block");

    Collection<Vertex> generateShape(FaceFlags faces, Vector3 position, Block block);
}

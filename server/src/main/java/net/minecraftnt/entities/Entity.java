package net.minecraftnt.entities;

import net.minecraftnt.util.Identifier;

/**
 * The base entity class for all entities to derive from, whether it'd be a tile entity or a mob
 * These all implement the create() function which should create a new instance of the entity
 */
public abstract class Entity {
    public abstract Entity create();
}

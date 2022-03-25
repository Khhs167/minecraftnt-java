package server.entities.special;

import client.Camera;
import server.entities.Entity;
import util.Vector3;

public abstract class Pawn extends Entity {
    public Pawn(Vector3 pos) {
        super(pos);
    }

    public abstract void translateCamera(Camera camera);
}

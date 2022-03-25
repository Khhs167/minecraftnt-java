package server.entities;

import util.Identifier;
import util.Transform;
import util.Vector2;
import util.Vector3;
import util.registries.Registry;

public abstract class Entity {
    private Transform transform;
    public Transform getTransform(){
        return transform;
    }
    public Entity(Vector3 pos){
        this.transform = new Transform(pos);
    }
    public abstract void update();
}

package net.minecraftnt.util;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class Transform {
    public Vector3 location;
    public Vector3 rotation;
    public Vector3 scale;

    public Matrix4f getMatrix(){
        Matrix4f worldMatrix = new Matrix4f().identity().translate(location.getX(), -location.getY(), location.getZ()).
                rotateX((float)Math.toRadians(rotation.getX())).
                rotateY((float)Math.toRadians(rotation.getY())).
                rotateZ((float)Math.toRadians(rotation.getZ())).scale(scale.getX(), scale.getY(), scale.getZ());
        return worldMatrix;
    }

    public Transform(Vector3 location, Vector3 rotation, Vector3 scale){
        this.location = location;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Transform(Vector3 location, Vector3 rotation){
        this.location = location;
        this.rotation = rotation;
        this.scale = Vector3.one();
    }

    public Transform(Vector3 location){
        this.location = location;
        this.rotation = new Vector3();
        this.scale = Vector3.one();
    }

    public Transform move(Vector3 movement){
        location.setX(location.getX() + movement.getX());
        location.setY(location.getY() + movement.getY());
        location.setZ(location.getZ() + movement.getZ());
        return this;
    }

    public Transform rotate(Vector3 rotation){
        this.rotation.setX(this.rotation.getX() + rotation.getX());
        this.rotation.setY(this.rotation.getY() + rotation.getY());
        this.rotation.setZ(this.rotation.getZ() + rotation.getZ());
        return this;
    }

    public Quaternionf getQuaternion(){
        return new Quaternionf().rotate(rotation.getX(), rotation.getY(), rotation.getZ());
    }

    public Vector3 getForward(){
        float X = (float) (Math.sin(Math.toRadians(rotation.getY())) * Math.cos(Math.toRadians(rotation.getX())));
        float Y = (float) Math.sin(Math.toRadians(-rotation.getX()));
        float Z = (float) (Math.cos(Math.toRadians(rotation.getX())) * Math.cos(Math.toRadians(rotation.getY())));

        return new Vector3(X, Y, Z).normalize();
    }

    public Vector3 getRight(){
        float X = (float) (Math.sin(Math.toRadians(rotation.getY() + 90)) * Math.cos(Math.toRadians(rotation.getX())));
        float Y = (float) Math.sin(Math.toRadians(-rotation.getX()));
        float Z = (float) (Math.cos(Math.toRadians(rotation.getX())) * Math.cos(Math.toRadians(rotation.getY() + 90)));

        return new Vector3(X, Y, Z).normalize();
    }
    public Vector3 getUp(){
        float X = (float) (Math.sin(Math.toRadians(rotation.getY())) * Math.cos(Math.toRadians(rotation.getX() + 90)));
        float Y = (float) Math.sin(Math.toRadians(-rotation.getX() + 90));
        float Z = (float) (Math.cos(Math.toRadians(rotation.getX() + 90)) * Math.cos(Math.toRadians(rotation.getY() + 90)));

        return new Vector3(X, Y, Z).normalize();
    }
}

package net.minecraftnt.util.maths;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;


public class Transformation implements Serializable {
    private Vector3 position = new Vector3();
    private Vector3 rotation = new Vector3();
    private Vector3 scale = new Vector3(1);

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Vector3 getRotation() {
        return rotation;
    }

    public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
    }

    public Vector3 getScale() {
        return scale;
    }

    public void setScale(Vector3 scale) {
        this.scale = scale;
    }

    public Matrix4 getMatrix() {
        Matrix4 matrix = Matrix4.identity();
        matrix = Matrix4.rotate(matrix, rotation.add(new Vector3(0, 0, 180)));
        matrix = Matrix4.translate(matrix, position);
        matrix = Matrix4.scale(matrix, scale);
        return matrix;
    }

    public Transformation translate(Vector3 translation) {
        this.position = this.position.add(translation);
        return this;
    }

    public Transformation rotate(Vector3 rotation) {
        this.rotation = this.rotation.add(rotation);
        return this;
    }
    public Transformation setRotationX(float rotation){
        setRotation(new Vector3(rotation, this.rotation.getY(), this.rotation.getZ()));
        return this;
    }
    public Transformation setRotationY(float rotation){
        setRotation(new Vector3(this.rotation.getX(), rotation, this.rotation.getZ()));
        return this;
    }
    public Transformation setRotationZ(float rotation){
        setRotation(new Vector3(this.rotation.getX(), this.rotation.getY(), rotation));
        return this;
    }
    public Transformation setPositionX(float position){
        setPosition(new Vector3(position, this.position.getY(), this.position.getZ()));
        return this;
    }
    public Transformation setPositionY(float position){
        setPosition(new Vector3(this.position.getX(), position, this.position.getZ()));
        return this;
    }
    public Transformation setPositionZ(float position){
        setPosition(new Vector3(this.position.getX(), this.position.getY(), position));
        return this;
    }

    // TODO: See if there is a better way to solve this piece of shit algorithm
    public Vector3 forward() {
        float X = (float) (Math.sin(Math.toRadians(rotation.getY())) * Math.cos(Math.toRadians(rotation.getX())));
        float Y = (float) Math.sin(Math.toRadians(-rotation.getX()));
        float Z = (float) (Math.cos(Math.toRadians(rotation.getX())) * Math.cos(Math.toRadians(rotation.getY())));
        return new Vector3(X, Y, Z).normalized();
    }

    public Vector3 right() {
        float X = (float) (Math.sin(Math.toRadians(rotation.getY() + 90)) * Math.cos(Math.toRadians(rotation.getX())));
        float Y = (float) Math.sin(Math.toRadians(-rotation.getX()));
        float Z = (float) (Math.cos(Math.toRadians(rotation.getX())) * Math.cos(Math.toRadians(rotation.getY() + 90)));

        return new Vector3(X, Y, Z).normalized();
    }

    public Vector3 up() {
        float X = (float) (Math.sin(Math.toRadians(rotation.getY())) * Math.cos(Math.toRadians(rotation.getX() + 90)));
        float Y = (float) Math.sin(Math.toRadians(-rotation.getX() + 90));
        float Z = (float) (Math.cos(Math.toRadians(rotation.getX() + 90)) * Math.cos(Math.toRadians(rotation.getY())));

        return new Vector3(X, Y, Z).normalized();
    }

    public Transformation move(Vector3 movement){
        position = position.add(movement);
        return this;
    }
}

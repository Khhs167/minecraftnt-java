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
        return Matrix4.transformation(this);
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

    private Quaternionf quaternionEuler(Vector3 euler) {
        Quaternionf quaternion = new Quaternionf();

        quaternion = quaternion.rotateX(euler.getX());
        quaternion = quaternion.rotateY(euler.getY());
        quaternion = quaternion.rotateZ(euler.getZ());

        return quaternion;
    }

    private Vector3 rotateVector(Quaternionf quaternion, Vector3 vector) {
        Vector3f vector3f = new Vector3f(vector.getX(), vector.getY(), vector.getZ());
        vector3f = quaternion.transform(vector3f);
        return new Vector3(vector3f.x, vector3f.y, vector3f.z);
    }

    public Vector3 forward() {
        return rotateVector(quaternionEuler(rotation), Vector3.FORWARD);
    }

    public Vector3 right() {
        return rotateVector(quaternionEuler(rotation), Vector3.RIGHT);
    }

    public Vector3 up() {
        return rotateVector(quaternionEuler(rotation), Vector3.UP);
    }

    public Transformation move(Vector3 movement){
        position = position.add(movement);
        return this;
    }
}

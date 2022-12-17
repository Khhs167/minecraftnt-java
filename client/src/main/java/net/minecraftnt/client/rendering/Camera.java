package net.minecraftnt.client.rendering;

import net.minecraftnt.util.maths.Matrix4;
import net.minecraftnt.util.maths.Transformation;
import net.minecraftnt.util.maths.Vector3;

public class Camera {
    private float fieldOfView = 90;

    private float nearPlane = 0.01f;
    private float farPlane = 1000f;

    private final Transformation transform = new Transformation();

    public void setPosition(Vector3 position) {
        transform.setPosition(position);
    }

    public Transformation getTransform(){
        return transform;
    }

    public void setFieldOfView(float fieldOfView) {
        this.fieldOfView = fieldOfView;
    }

    public void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
    }

    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
    }

    public Matrix4 getProjectionMatrix(float aspectRatio){
        return Matrix4.perspective(fieldOfView, aspectRatio, nearPlane, farPlane);
    }

    public Matrix4 getViewMatrix(){
        Vector3 frw = transform.forward();

        return Matrix4.lookAt(transform.getPosition(), transform.getPosition().add(frw), Vector3.UP);
    }
}

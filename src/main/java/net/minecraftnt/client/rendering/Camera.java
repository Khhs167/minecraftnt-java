package net.minecraftnt.client.rendering;

import net.minecraftnt.client.main.ClientMainHandler;
import org.joml.Matrix4f;
import net.minecraftnt.server.Minecraft;
import net.minecraftnt.util.Transform;
import net.minecraftnt.util.Vector3;

public class Camera {
    private float fieldOfView = (float)Math.toRadians(60);

    private float Z_NEAR = 0.01f;
    private float Z_FAR = 1000.f;

    private Transform transform = new Transform(Vector3.zero());

    public Transform getTransform(){
        return  transform;
    }

    public Matrix4f getProjectionMatrix(){
        float aspectRatio = ClientMainHandler.getInstance().getWindow().getRatio();
        return new Matrix4f().perspective(fieldOfView, aspectRatio, Z_NEAR, Z_FAR);
    }

    public Vector3 getForward(){
        float X = (float) (Math.sin(Math.toRadians(transform.rotation.getY())) * Math.cos(Math.toRadians(transform.rotation.getX())));
        float Y = (float) Math.sin(Math.toRadians(-transform.rotation.getX()));
        float Z = (float) (Math.cos(Math.toRadians(transform.rotation.getX())) * Math.cos(Math.toRadians(transform.rotation.getY())));

        return new Vector3(X, Y, Z).normalize();
    }

    public Vector3 getRight(){
        float X = (float) (Math.sin(Math.toRadians(transform.rotation.getY() + 90)) * Math.cos(Math.toRadians(transform.rotation.getX())));
        float Y = (float) Math.sin(Math.toRadians(-transform.rotation.getX()));
        float Z = (float) (Math.cos(Math.toRadians(transform.rotation.getX())) * Math.cos(Math.toRadians(transform.rotation.getY() + 90)));

        return new Vector3(X, Y, Z).normalize();
    }

    public Matrix4f getViewMatrix(){
        Vector3 frw = getForward();

        return new Matrix4f().lookAt(
                transform.location.getX(), transform.location.getY(), transform.location.getZ(),
                transform.location.getX() + frw.getX(), transform.location.getY() + frw.getY(), transform.location.getZ() + frw.getZ(),
                0, 1, 0);
    }
}

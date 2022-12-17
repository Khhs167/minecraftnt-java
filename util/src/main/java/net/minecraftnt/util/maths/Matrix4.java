package net.minecraftnt.util.maths;

import org.joml.Matrix4f;

public class Matrix4 {

    // I wanted to use my own, but in the end just lazily wrapping a JOML mat4 worked out just fine!
    private final Matrix4f internal;

    private Matrix4(org.joml.Matrix4f internal){
        this.internal = internal;
    }

    public float[] getData(){
        float[] out = new float[16];
        out = internal.get(out);
        return out;
    }

    public Matrix4f getInternal() {
        return internal;
    }

    public static Matrix4 perspective(float fov, float ratio, float near, float far){
        return new Matrix4(new Matrix4f().perspective((float)Math.toRadians(fov), ratio, near, far));
    }

    public static Matrix4 lookAt(Vector3 pos, Vector3 target, Vector3 up) {
        return new Matrix4(new Matrix4f().lookAt(pos.getX(), pos.getY(), pos.getZ(), target.getX(), target.getY(), target.getZ(), up.getX(), up.getY(), up.getZ()));
    }

    public static Matrix4 translate(Matrix4 t, Vector3 position){
        Matrix4f n = new Matrix4f(t.internal);
        n.translate(position.getX(), position.getY(), position.getZ());

        return new Matrix4(n);
    }

    public static Matrix4 rotate(Matrix4 t, Vector3 rotation){
        Matrix4f n = new Matrix4f(t.internal);
        n.rotateX((float)Math.toRadians(rotation.getX())).rotateY((float)Math.toRadians(rotation.getY())).rotateZ((float)Math.toRadians(rotation.getZ()));
        return new Matrix4(n);
    }

    public static Matrix4 scale(Matrix4 t, Vector3 scale){
        Matrix4f n = new Matrix4f(t.internal);
        n.scale(scale.getX(), scale.getY(), scale.getZ());
        return new Matrix4(n);
    }

    public static Matrix4 transformation(Transformation transformation){
        Vector3 location = transformation.getPosition();
        Vector3 rotation = transformation.getRotation();
        Vector3 scale = transformation.getScale();
        Matrix4f worldMatrix = new Matrix4f().identity().translate(location.getX(), -location.getY(), location.getZ()).
                rotateX((float)Math.toRadians(rotation.getX())).
                rotateY((float)Math.toRadians(rotation.getY())).
                rotateZ((float)Math.toRadians(rotation.getZ())).scale(scale.getX(), scale.getY(), scale.getZ());
        return new Matrix4(worldMatrix);
    }

    public static Matrix4 identity(){
        return new Matrix4(new Matrix4f().identity());
    }

}

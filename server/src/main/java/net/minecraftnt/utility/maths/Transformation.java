package net.minecraftnt.utility.maths;

public class Transformation {
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
}

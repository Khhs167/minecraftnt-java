package net.minecraftnt.util.maths;

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
}

package net.minecraftnt.util;

public class FaceFlags {


    public static final Faces FACE_FRONT = Faces.FACE_FRONT;
    public static final Faces FACE_RIGHT = Faces.FACE_RIGHT;
    public static final Faces FACE_BACK = Faces.FACE_BACK;
    public static final Faces FACE_LEFT = Faces.FACE_LEFT;
    public static final Faces FACE_TOP = Faces.FACE_TOP;
    public static final Faces FACE_BOTTOM = Faces.FACE_BOTTOM;
    public static final FaceFlags ALL_FACES = new FaceFlags(Faces.FACE_FRONT, Faces.FACE_RIGHT, Faces.FACE_BACK, Faces.FACE_LEFT, Faces.FACE_TOP, Faces.FACE_BOTTOM);

    private short value;

    public FaceFlags(Faces... values){
        for (Faces faces : values) {
            value |= faces.value;
        }
    }

    public boolean hasFace(Faces face){
        return (value & face.value) != 0;
    }

    public enum Faces {
        FACE_FRONT(0b0000001),
        FACE_RIGHT(0b00000010),
        FACE_BACK(0b00000100),
        FACE_LEFT(0b00001000),
        FACE_TOP(0b00010000),
        FACE_BOTTOM(0b00100000);

        private final int value;

        Faces(int value) {
            this.value = value;
        }
    }

}

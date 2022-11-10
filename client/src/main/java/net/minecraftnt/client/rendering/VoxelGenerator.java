package net.minecraftnt.client.rendering;

import net.minecraftnt.util.FaceFlags;
import net.minecraftnt.util.maths.Vector2;
import net.minecraftnt.util.maths.Vector3;

import java.util.LinkedList;

import static net.minecraftnt.util.FaceFlags.Faces.*;

public class VoxelGenerator {

    private static final Vector3[] offsets = new Vector3[]{
            new Vector3(0.0f, 0.0f, 0.0f),
            new Vector3(1.0f, 0.0f, 0.0f),
            new Vector3(0.0f, 0.0f, 1.0f),
            new Vector3(0.0f, 0.0f, 0.0f),
            new Vector3(0.0f, 1.0f, 0.0f),
            new Vector3(0.0f, 0.0f, 0.0f)
    };

    private final static Orientation[] orientations = new Orientation[]{

            new Orientation(new Vector3(0, 1, 0), new Vector3(1, 0, 0)), // YX - front
            new Orientation(new Vector3(0, 1, 0), new Vector3(0, 0, 1)), // YZ - right
            new Orientation(new Vector3(0, 1, 0), new Vector3(1, 0, 0)), // YX - back
            new Orientation(new Vector3(0, 1, 0), new Vector3(0, 0, 1)), // YZ - left
            new Orientation(new Vector3(1, 0, 0), new Vector3(0, 0, 1)), // XZ - top
            new Orientation(new Vector3(1, 0, 0), new Vector3(0, 0, 1))  // XZ - bottom

    };

    private static final FaceFlags.Faces[] faces_enum = new FaceFlags.Faces[]{
            FACE_FRONT,
            FACE_RIGHT,
            FACE_BACK,
            FACE_LEFT,
            FACE_TOP,
            FACE_BOTTOM
    };

    public static Quad[] generateVoxel(FaceFlags faces) {

        LinkedList<Quad> quads = new LinkedList<>();

        for(int i = 0; i < 6; i++){
            if(faces.hasFace(faces_enum[i])){
                Vector3 up = orientations[i].up;
                Vector3 right = orientations[i].right;

                Vector3 pos = offsets[i];

                quads.add(new Quad(new Vector3[] {
                        pos,
                        pos.add(up),
                        pos.add(up).add(right),
                        pos.add(right)
                }, new Vector2[]{
                        new Vector2(0, 0),
                        new Vector2(0, 1),
                        new Vector2(1, 1),
                        new Vector2(1, 0)
                }, 1));

            }

        }

        return quads.toArray(new Quad[0]);

    }

    public static RectangleMesh generateVoxelMesh(FaceFlags faces){
        RectangleMesh mesh = new RectangleMesh();
        mesh.quads = generateVoxel(faces);
        mesh.updateMesh();
        return mesh;
    }

    static class Orientation {
        public final Vector3 up;
        public final Vector3 right;

        private Orientation(Vector3 up, Vector3 right) {
            this.up = up;
            this.right = right;
        }
    }

}

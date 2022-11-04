package net.minecraftnt.client.rendering;

import net.minecraftnt.util.FaceFlags;
import net.minecraftnt.util.maths.Vector2;
import net.minecraftnt.util.maths.Vector3;

import java.util.Arrays;
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
            if(faces.hasFace(faces_enum[i]))
                quads.add(new Quad(offsets[i], new Vector2(1), new Vector2(0.5f), new Vector2(0.5f), i));
        }

        return quads.toArray(new Quad[0]);

    }

    public static RectangleMesh generateVoxelMesh(FaceFlags faces){
        RectangleMesh mesh = new RectangleMesh();
        Quad[] quads = generateVoxel(faces);
        mesh.quads.addAll(Arrays.stream(quads).toList());
        mesh.updateMesh();
        return mesh;
    }

}

package net.minecraftnt.client.builtin;

import net.minecraftnt.Registries;
import net.minecraftnt.rendering.ShapeGenerator;
import net.minecraftnt.rendering.TextureAtlasLocation;
import net.minecraftnt.rendering.Vertex;
import net.minecraftnt.util.FaceFlags;
import net.minecraftnt.util.maths.Vector2;
import net.minecraftnt.util.maths.Vector3;
import net.minecraftnt.server.world.Block;

import java.util.Collection;
import java.util.LinkedList;

public class VoxelGenerator implements ShapeGenerator {

    private static final Vector3[] offsets = new Vector3[]{
            new Vector3(0.0f, 0.0f, 0.0f),
            new Vector3(1.0f, 0.0f, 0.0f),
            new Vector3(1.0f, 0.0f, 1.0f),
            new Vector3(0.0f, 0.0f, 1.0f),
            new Vector3(1.0f, 1.0f, 1.0f),
            new Vector3(0.0f, 0.0f, 1.0f)
    };

    private final static Orientation[] orientations = new Orientation[]{

            new Orientation(new Vector3(0, 1, 0), new Vector3(1, 0, 0)), // YX - front
            new Orientation(new Vector3(0, 1, 0), new Vector3(0, 0, 1)), // YZ - right
            new Orientation(new Vector3(0, 1, 0), new Vector3(-1, 0, 0)), // YX - back
            new Orientation(new Vector3(0, 1, 0), new Vector3(0, 0, -1)), // YZ - left
            new Orientation(new Vector3(0, 0, -1), new Vector3(-1, 0, 0)), // XZ - top
            new Orientation(new Vector3(0, 0, -1), new Vector3(1, 0, 0))  // XZ - bottom

    };

    public Collection<Vertex> generateShape(FaceFlags faces, Vector3 position, Block block) {

        LinkedList<Vertex> vertices = new LinkedList<>();

        for(int i = 0; i < 6; i++){

            if(faces.hasFace(FaceFlags.FACES_LIST[i])){
                Vector3 up = orientations[i].up;
                Vector3 right = orientations[i].right;

                Vector3 pos = offsets[i].add(position);

                TextureAtlasLocation location = Registries.TEXTURE_ATLAS_LOC.get(block.getAtlasID(FaceFlags.FACES_LIST[i]));

                final Vector2 uv00 = location.getOrigin();
                final Vector2 uv01 = new Vector2(location.getOrigin().getX(), location.getMax().getY());
                final Vector2 uv10 = new Vector2(location.getMax().getX(), location.getOrigin().getY());
                final Vector2 uv11 = location.getMax();

                final Vector3 pos00 = pos;
                final Vector3 pos01 = pos.add(up);
                final Vector3 pos10 = pos.add(right);
                final Vector3 pos11 = pos.add(up).add(right);

                vertices.add(new Vertex(pos00, uv00, 1));
                vertices.add(new Vertex(pos01, uv01, 1));
                vertices.add(new Vertex(pos10, uv10, 1));

                vertices.add(new Vertex(pos01, uv01, 1));
                vertices.add(new Vertex(pos11, uv11, 1));
                vertices.add(new Vertex(pos10, uv10, 1));

            }

        }

        return vertices;

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
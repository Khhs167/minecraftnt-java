package client.ui.fonts;

import client.rendering.Mesh;
import client.rendering.Shader;
import util.Transform;
import util.Vector2;
import util.Vector3;
import util.registries.Registry;

public class Character {
    private final int id;
    private Mesh mesh;
    public Character(int id){
        this.id = id;
        mesh = new Mesh();

        mesh.vertices = new Vector3[]{
                new Vector3(0, 0, 0),
                new Vector3(1, 0, 0),
                new Vector3(0, 1, 0),
                new Vector3(1, 1, 0),
        };

        final float xOffset = (1f / CHARS.length) * this.id;

        mesh.uv = new Vector2[]{
                new Vector2(0f + xOffset, 1),
                new Vector2((1f / CHARS.length) + xOffset, 1),
                new Vector2(0f + xOffset, 0),
                new Vector2((1f / CHARS.length) + xOffset, 0)
        };

        mesh.triangles = new int[]{
                0, 1, 2,
                1, 3, 2
        };

        mesh.buildMesh();
    }

    public void render(Transform transform){

        mesh.renderNoPrep();
    }

    public static final char[] CHARS = new char[]{
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'a',
            'b',
            'c',
            'd',
            'e',
            'f',
            'g',
            'h',
            'i',
            'j',
            'k',
            'l',
            'm',
            'n',
            'o',
            'p',
            'q',
            'r',
            's',
            't',
            'u',
            'v',
            'w',
            'x',
            'y',
            'z',
            'å',
            'ä',
            'ö',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F',
            'G',
            'H',
            'I',
            'J',
            'K',
            'L',
            'M',
            'N',
            'O',
            'P',
            'Q',
            'R',
            'S',
            'T',
            'U',
            'V',
            'W',
            'X',
            'Y',
            'Z',
            'Å',
            'Ä',
            'Ö',

    };
}

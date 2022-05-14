package client.ui.fonts;

import client.rendering.Shader;
import client.rendering.Texture;
import server.Minecraft;
import util.Identifier;
import util.Transform;
import util.Vector2;
import util.Vector3;
import util.registries.Registry;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class Font {

    public static final Identifier FONT_DEFAULT = new Identifier("minecraft", "font_default");

    private Map<java.lang.Character, Character> characterMap = new HashMap<>();
    private Texture texture;

    public Font(String path){
        texture = new Texture(path);

        for (int i = 0; i < Character.CHARS.length; i++) {
            characterMap.put(Character.CHARS[i], new Character(i));
        }
    }

    public void renderText(String text, Vector2 pos){
        for (int i = 0; i < text.length(); i++){
            render(text.charAt(i), pos.add(new Vector2(i * 32, 0)));
        }
    }

    public void render(char character, Vector2 pos){
        texture.use();
        Shader program = Registry.SHADERS.get(Shader.SHADER_FONT);
        program.bind();
        glUniform2f(program.getUniformLocation("screen_pos"), pos.getX(), pos.getY());
        glUniform2f(program.getUniformLocation("screen_size"), Minecraft.getInstance().getWindow().getSize().getX() / Minecraft.getGuiScale(), Minecraft.getInstance().getWindow().getSize().getY() / Minecraft.getGuiScale());
        ((Character)characterMap.get(character)).render(new Transform(Vector3.up()));
        program.unbind();
    }
}

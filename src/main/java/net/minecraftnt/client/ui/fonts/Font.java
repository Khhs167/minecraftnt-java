package net.minecraftnt.client.ui.fonts;
import net.minecraftnt.client.ClientMainHandler;
import net.minecraftnt.client.rendering.Shader;
import net.minecraftnt.client.rendering.Texture;

import net.minecraftnt.util.Identifier;
import net.minecraftnt.util.Transform;
import net.minecraftnt.util.Vector2;
import net.minecraftnt.util.Vector3;
import net.minecraftnt.util.registries.Registry;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glUniform2f;

public class Font {

    public static final Identifier FONT_DEFAULT = new Identifier("minecraft", "font_default");
    public static Identifier FONT_DEFAULT_TEXTURE = new Identifier("minecraft", "font_default_texture");
    private Map<java.lang.Character, Character> characterMap = new HashMap<>();
    public Font(){
        for (int i = 0; i < Character.FONT_LEN; i++) {
            characterMap.put((char)i, new Character(i));
        }
    }

    public void renderText(String text, Vector2 pos){
        int posx = 0;
        int posy = 0;
        for (int i = 0; i < text.length(); i++){
            if(text.charAt(i) == '\n'){
                posy++;
                posx = 0;
                continue;
            }
            render(text.charAt(i), pos.add(new Vector2(posx * Character.FONT_WIDTH, posy * -Character.FONT_HEIGHT)));
            posx++;
        }
    }

    public void render(char character, Vector2 pos){
        Texture.use(FONT_DEFAULT_TEXTURE);
        Shader program = Registry.SHADERS.get(Shader.SHADER_FONT);
        program.bind();
        glUniform2f(program.getUniformLocation("screen_pos"), pos.getX(), pos.getY());
        glUniform2f(program.getUniformLocation("screen_size"), ClientMainHandler.getInstance().getWindow().getSize().getX() / ClientMainHandler.getGuiScale() * 2, ClientMainHandler.getInstance().getWindow().getSize().getY() / ClientMainHandler.getGuiScale() * 2);
        characterMap.get(character).render(new Transform(Vector3.up()));
        program.unbind();
    }
}

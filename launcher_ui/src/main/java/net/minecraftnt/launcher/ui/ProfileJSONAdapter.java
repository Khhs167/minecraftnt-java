package net.minecraftnt.launcher.ui;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;

public class ProfileJSONAdapter extends TypeAdapter<Profile> {
    @Override
    public void write(JsonWriter out, Profile value) throws IOException {
        out.beginObject();

        out.name("name");
        out.value(value.getName());

        out.name("version");
        out.value(value.getVersion());

        out.name("username");
        out.value(value.getUsername());

        out.endObject();
    }

    @Override
    public Profile read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        if(in.peek() != JsonToken.BEGIN_OBJECT){
            return null;
        }

        in.beginObject();

        HashMap<String, String> values = new HashMap<>();

        while (in.peek() == JsonToken.NAME){
            String name = in.nextName();
            String value = in.nextString();

            System.out.println("Name: " + name + ", Value: " + value);

            values.put(name, value);
        }

        in.endObject();

        Profile profile = new Profile();

        profile.setName(values.getOrDefault("name", "NULL"));
        profile.setVersion(values.getOrDefault("version", "NULL"));
        profile.setUsername(values.getOrDefault("username", "player"));

        return profile;

    }
}

package net.minecraftnt.util;

import java.io.DataInputStream;
import java.io.IOException;

public class GenericUtil {
    public static String readString(DataInputStream stream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        byte c = '\0';
        while ((c = stream.readByte()) != '\0') {
            stringBuilder.append((char)c);
        }
        return stringBuilder.toString();
    }
}

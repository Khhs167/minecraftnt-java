package net.minecraftnt;

import net.minecraftnt.util.Identifier;

import java.util.Objects;
import java.util.Random;

public class MinecraftntData {

    public static final Random RANDOM = new Random();

    public static boolean isClient() {

        if(Objects.equals(Thread.currentThread().getName(), "Server"))
            return false;

        try {
            Class.forName("net.minecraftnt.client.Client");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }

    }



}

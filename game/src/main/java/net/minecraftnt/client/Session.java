package net.minecraftnt.client;

import java.util.Objects;

public class Session {

    private final String username;
    private final String id;

    public Session(String username, String id){
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean validate() {
        return !Objects.equals(username, "__unregistered__user___") && !Objects.equals(id, "-");
    }
}

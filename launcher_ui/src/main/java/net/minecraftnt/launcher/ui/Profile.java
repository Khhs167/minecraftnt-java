package net.minecraftnt.launcher.ui;

public class Profile {
    private String name = "New Profile";
    private String version = "latest";
    private String username = "player";

    public Profile setName(String name) {
        this.name = name;
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Profile setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return name;
    }
}

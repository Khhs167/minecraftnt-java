package net.minecraftnt.server.data.resources;

public class PackInfo {
    public final String name;
    public final String description;
    public final int pack_version;

    public PackInfo(String name, String description, int pack_version) {
        this.name = name;
        this.description = description;
        this.pack_version = pack_version;
    }
}

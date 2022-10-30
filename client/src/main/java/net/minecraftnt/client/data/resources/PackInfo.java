package net.minecraftnt.client.data.resources;

public class PackInfo {

    //public static final Identifier PACK_IMAGE = new Identifier("minecraft", "packimage");

    public final String name;
    public final String description;
    public final int pack_version;

    public PackInfo(String name, String description, int pack_version) {
        this.name = name;
        this.description = description;
        this.pack_version = pack_version;
    }
}

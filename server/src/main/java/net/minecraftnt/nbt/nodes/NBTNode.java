package net.minecraftnt.nbt.nodes;

public abstract class NBTNode {
    private String name;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public NBTNode setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        String capitalType = type.substring(0, 1).toUpperCase() + type.substring(1);
        return indent + "TAG_" + capitalType + (name == null || name.isEmpty() ? "" :  "(\"" + name + "\")");
    }
}

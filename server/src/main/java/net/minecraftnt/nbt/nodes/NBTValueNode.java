package net.minecraftnt.nbt.nodes;

public class NBTValueNode<T> extends NBTNode{

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        return super.toString(indent) + ": " + getValue().toString();
    }
}

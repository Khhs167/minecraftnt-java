package net.minecraftnt.nbt.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class NBTListNode<T> extends NBTNode{

    private final ArrayList<T> data = new ArrayList<>();

    private String contentType;

    public T get(int i) {
        return data.get(i);
    }

    public T[] getArray(T[] empty){
        return data.toArray(empty);
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void add(T val){
        data.add(val);
    }

    public void addAll(T[] val){
        data.addAll(Arrays.stream(val).toList());
    }

    public void addAll(Collection<T> val){
        data.addAll(val);
    }

    public int getLength() {
        return data.size();
    }

    public ArrayList<T> getData() {
        return data;
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder s = new StringBuilder(super.toString(indent) + ": " + getLength() + " entries\n" + indent + "{\n");
        for(int i = 0; i < getLength(); i++){

            int maxEntries = 20;
            if(i > maxEntries){
                s.append(indent).append("\t...\n");
                break;
            }

            T c = get(i);
            if(c instanceof NBTNode node) {
                s.append(node.toString(indent + "\t")).append("\n");
            } else {
                s.append(indent).append("\t").append(c.toString()).append("\n");
            }


        }

        s.append(indent).append("}");

        return s.toString();

    }
}

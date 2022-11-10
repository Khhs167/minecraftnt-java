package net.minecraftnt.nbt.nodes;

import java.util.Objects;

public class NBTCompoundNode extends NBTListNode<NBTNode> {

    public NBTNode getChild(String name){
        for(int i = 0; i < getLength(); i++){
            if(Objects.equals(get(i).getName(), name))
                return get(i);
        }

        return null;
    }

    public short getShort(String name) {
        NBTNode child = getChild(name);
        if(Objects.equals(child.getType(), "short")){
            NBTValueNode<Short> valueNode = (NBTValueNode<Short>) child;

            return valueNode.getValue();
        }
        return 0;
    }

    public int getInt(String name) {
        NBTNode child = getChild(name);
        if(Objects.equals(child.getType(), "short")){
            NBTValueNode<Integer> valueNode = (NBTValueNode<Integer>) child;

            return valueNode.getValue();
        }
        return 0;
    }

    public String getString(String name) {
        NBTNode child = getChild(name);
        if(Objects.equals(child.getType(), "short")){
            NBTValueNode<String> valueNode = (NBTValueNode<String>) child;

            return valueNode.getValue();
        }
        return "";
    }

    public long getLong(String name) {
        NBTNode child = getChild(name);
        if(Objects.equals(child.getType(), "short")){
            NBTValueNode<Long> valueNode = (NBTValueNode<Long>) child;

            return valueNode.getValue();
        }
        return 0;
    }

    public float getFloat(String name) {
        NBTNode child = getChild(name);
        if(Objects.equals(child.getType(), "short")){
            NBTValueNode<Float> valueNode = (NBTValueNode<Float>) child;

            return valueNode.getValue();
        }
        return 0;
    }

    public double getDouble(String name) {
        NBTNode child = getChild(name);
        if(Objects.equals(child.getType(), "short")){
            NBTValueNode<Double> valueNode = (NBTValueNode<Double>) child;

            return valueNode.getValue();
        }
        return 0;
    }
}

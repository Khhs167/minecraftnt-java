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
            NBTValueNode<?> node = (NBTValueNode<?>) child;
            Object obj = node.getValue();
            if(obj instanceof Short value)
                return value;
        }
        return 0;
    }

    public int getInt(String name) {
        NBTNode child = getChild(name);
        if(Objects.equals(child.getType(), "int")){
            NBTValueNode<?> node = (NBTValueNode<?>) child;
            Object obj = node.getValue();
            if(obj instanceof Integer value)
                return value;
        }
        return 0;
    }

    public String getString(String name) {
        NBTNode child = getChild(name);
        if(Objects.equals(child.getType(), "string")){
            NBTValueNode<?> node = (NBTValueNode<?>) child;
            Object obj = node.getValue();
            if(obj instanceof String value)
                return value;
        }
        return "";
    }

    public long getLong(String name) {
        NBTNode child = getChild(name);
        if(Objects.equals(child.getType(), "long")){
            NBTValueNode<?> node = (NBTValueNode<?>) child;
            Object obj = node.getValue();
            if(obj instanceof Long value)
                return value;
        }
        return 0;
    }

    public float getFloat(String name) {
        NBTNode child = getChild(name);
        if(Objects.equals(child.getType(), "float")){
            NBTValueNode<?> node = (NBTValueNode<?>) child;
            Object obj = node.getValue();
            if(obj instanceof Float value)
                return value;
        }
        return 0;
    }

    public double getDouble(String name) {
        NBTNode child = getChild(name);
        if(Objects.equals(child.getType(), "double")){
            NBTValueNode<?> node = (NBTValueNode<?>) child;
            Object obj = node.getValue();
            if(obj instanceof Double value)
                return value;
        }
        return 0;
    }
}

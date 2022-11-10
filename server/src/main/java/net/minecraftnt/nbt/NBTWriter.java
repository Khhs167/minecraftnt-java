package net.minecraftnt.nbt;

import net.minecraftnt.nbt.writing.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public class NBTWriter {
    private final DataOutputStream outputStream;
    private final Stack<NBTNodeStore> nodeStack = new Stack<>();
    private final Queue<NBTNodeStore> nodes = new LinkedList<>();

    public NBTWriter(OutputStream outputStream) throws IOException {
        this.outputStream = new DataOutputStream(new GZIPOutputStream(outputStream));
    }

    public void beginCompound(String name){
        NBTNodeStore store = new NBTNodeStore();
        store.name = name;
        store.type = "compound";
        addNode(store);
        nodeStack.add(store);
    }

    public void endCompound() {
        NBTNodeStore store = new NBTNodeStore();
        store.type = "end";
        store.name = "";
        addNode(store);
        nodeStack.pop();
    }

    public void beginList(String name, String type) {
        NBTChildStore store = new NBTChildStore();
        store.name = name;
        store.type = "list";
        store.listDataType = type;
        addNode(store);
        nodeStack.add(store);
    }

    public void endList() {
        nodeStack.pop();
    }

    public void writeString(String name, String value){
        NBTDataStore<String> store = new NBTDataStore<>();
        store.value = value;
        store.type = "string";
        store.name = name;
        addNode(store);
    }

    public void addNode(NBTNodeStore store) {
        if(!nodeStack.empty() && Objects.equals(nodeStack.peek().type, "list")) {
            ((NBTChildStore) nodeStack.peek()).length++;
            store.isInList = true;
        }
        nodes.add(store);
    }

    public void flush() throws IOException {

        for (NBTNodeStore nodeStore : nodes){
            if(!nodeStore.isInList) {
                outputStream.writeByte(getNodeType(nodeStore.type));
                outputStream.writeShort(nodeStore.name.length());
                outputStream.writeBytes(nodeStore.name);
            }
            switch (nodeStore.type){
                case "byte":
                    outputStream.writeByte(((NBTDataStore<Byte>)nodeStore).value);
                    break;
                case "short":
                    outputStream.writeShort(((NBTDataStore<Short>)nodeStore).value);
                    break;
                case "int":
                    outputStream.writeInt(((NBTDataStore<Integer>)nodeStore).value);
                    break;
                case "long":
                    outputStream.writeLong(((NBTDataStore<Long>)nodeStore).value);
                    break;
                case "float":
                    outputStream.writeFloat(((NBTDataStore<Float>)nodeStore).value);
                    break;
                case "double":
                    outputStream.writeDouble(((NBTDataStore<Double>)nodeStore).value);
                    break;
                case "string":
                    outputStream.writeShort(((NBTDataStore<String>)nodeStore).value.length());
                    outputStream.writeBytes(((NBTDataStore<String>)nodeStore).value);
                    break;
                case "list":
                    outputStream.writeByte(getNodeType(((NBTChildStore)nodeStore).listDataType));
                    outputStream.writeInt(((NBTChildStore)nodeStore).length);
                    break;
                case "byte[]":
                    Byte[] byteData = ((NBTDataStore<Byte[]>)nodeStore).value;
                    outputStream.writeInt(byteData.length);
                    for (int i = 0; i < byteData.length; i++){
                        outputStream.writeByte(byteData[i]);
                    }
                    break;
                case "int[]":
                    Integer[] intData = ((NBTDataStore<Integer[]>)nodeStore).value;
                    outputStream.writeInt(intData.length);
                    for (int i = 0; i < intData.length; i++){
                        outputStream.writeInt(intData[i]);
                    }
                    break;
                case "long[]":
                    Long[] longData = ((NBTDataStore<Long[]>)nodeStore).value;
                    outputStream.writeInt(longData.length);
                    for (int i = 0; i < longData.length; i++){
                        outputStream.writeLong(longData[i]);
                    }
                    break;


            }

        }

        outputStream.flush();
        outputStream.close();

    }

    private byte getNodeType(String type) throws StreamCorruptedException {
        return switch (type) {
            case "end" -> 0;
            case "byte" -> 1;
            case "short" -> 2;
            case "int" -> 3;
            case "long" -> 4;
            case "float" -> 5;
            case "double" -> 6;
            case "byte[]" -> 7;
            case "string" -> 8;
            case "list" -> 9;
            case "compound" -> 10;
            case "int[]" -> 11;
            case "long[]" -> 12;
            default -> -1;
        };

    }
}

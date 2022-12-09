package net.minecraftnt.nbt;

import net.minecraftnt.nbt.exceptions.UnexpectedNBTNodeException;
import net.minecraftnt.nbt.nodes.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class NBTReader {

    private final DataInputStream inputStream;
    private NBTNode root;

    private int currentLevel = 0;

    public NBTReader(InputStream stream) throws IOException {
        inputStream = new DataInputStream(new GZIPInputStream(stream));
    }

    public void parse() throws IOException, UnexpectedNBTNodeException {
        root = parseNode();
    }

    public NBTNode getRoot() {
        return root;
    }


    private NBTNode parseNode() throws IOException, UnexpectedNBTNodeException {
        return parseNode(-1, true);
    }

    private NBTNode parseNode(int type, boolean named) throws IOException, UnexpectedNBTNodeException {
        if(type == -1)
            type = inputStream.readByte();

        String name = "";

        if(named && type != 0)
            name = parseName();

        switch (type) {
            case 0:
                return parseEnd().setName(name);
            case 1:
                return parseByte().setName(name);
            case 2:
                return parseShort().setName(name);
            case 3:
                return parseInt().setName(name);
            case 4:
                return parseLong().setName(name);
            case 5:
                return parseFloat().setName(name);
            case 6:
                return parseDouble().setName(name);
            case 7:
                return parseByteArray().setName(name);
            case 8:
                return parseString().setName(name);
            case 9:
                return parseList().setName(name);
            case 10:
                return parseCompound().setName(name);
            case 11:
                return parseIntArray().setName(name);
            case 12:
                return parseLongArray().setName(name);
        }

        throw new StreamCorruptedException("Invalid NBT Node type \"" + type + "\"!");

    }

    private String getNodeType(byte type) throws StreamCorruptedException {
        switch (type) {
            case 0:
                return "end";
            case 1:
                return "byte";
            case 2:
                return "short";
            case 3:
                return "int";
            case 4:
                return "long";
            case 5:
                return "float";
            case 6:
                return "double";
            case 7:
                return "byte[]";
            case 8:
                return "string";
            case 9:
                return "list";
            case 10:
                return "compound";
            case 11:
                return "int[]";
            case 12:
                return "long[]";
        }

        throw new StreamCorruptedException("Invalid NBT Node type \"" + type + "\"!");
    }

    private NBTNode parseEnd() throws UnexpectedNBTNodeException {

        if(currentLevel <= 0)
            throw new UnexpectedNBTNodeException("END node requires compound");

        currentLevel--;

        NBTEndNode node = new NBTEndNode();
        node.setType("end");
        return node;
    }

    private String parseName() throws IOException {
        int length = inputStream.readShort() & 0xFFFF;
        byte[] nameBytes = new byte[length];
        inputStream.readFully(nameBytes);

        return new String(nameBytes);

    }

    private NBTNode parseByte() throws IOException {
        NBTValueNode<Byte> node = new NBTValueNode<>();
        node.setType("byte");

        node.setValue(inputStream.readByte());
        return node;
    }

    private NBTNode parseShort() throws IOException {
        NBTValueNode<Short> node = new NBTValueNode<>();
        node.setType("short");

        node.setValue(inputStream.readShort());
        return node;
    }

    private NBTNode parseInt() throws IOException {
        NBTValueNode<Integer> node = new NBTValueNode<>();
        node.setType("int");

        node.setValue(inputStream.readInt());
        return node;
    }

    private NBTNode parseLong() throws IOException {
        NBTValueNode<Long> node = new NBTValueNode<>();
        node.setType("long");

        node.setValue(inputStream.readLong());
        return node;
    }

    private NBTNode parseFloat() throws IOException {
        NBTValueNode<Float> node = new NBTValueNode<>();
        node.setType("float");

        node.setValue(inputStream.readFloat());
        return node;
    }

    private NBTNode parseDouble() throws IOException {
        NBTValueNode<Double> node = new NBTValueNode<>();
        node.setType("double");

        node.setValue(inputStream.readDouble());
        return node;
    }

    private NBTNode parseByteArray() throws IOException {

        NBTListNode<Byte> node = new NBTListNode<>();
        node.setType("byte[]");

        int length = inputStream.readInt();
        byte[] bytes = new byte[length];
        inputStream.readFully(bytes);

        for (byte b : bytes)
            node.add(b);

        return node;
    }

    private NBTNode parseIntArray() throws IOException {

        NBTListNode<Integer> node = new NBTListNode<>();
        node.setType("int[]");

        int length = inputStream.readInt();

        for (int i = 0; i < length; i++){
            node.add(inputStream.readInt());
        }

        return node;
    }

    private NBTNode parseLongArray() throws IOException {

        NBTListNode<Long> node = new NBTListNode<>();
        node.setType("long[]");

        int length = inputStream.readInt();

        for (int i = 0; i < length; i++){
            node.add(inputStream.readLong());
        }

        return node;
    }

    private NBTNode parseString() throws IOException {
        NBTValueNode<String> node = new NBTValueNode<>();
        node.setType("string");

        int length = inputStream.readShort();
        byte[] bytes = new byte[length];
        inputStream.readFully(bytes);

        node.setValue(new String(bytes));

        return node;
    }

    private NBTNode parseList() throws IOException, UnexpectedNBTNodeException {

        NBTListNode<NBTNode> node = new NBTListNode<>();
        byte type = inputStream.readByte();
        String childType = getNodeType(type);
        int length = inputStream.readInt();
        node.setType("list");
        node.setContentType(childType);

        NBTNode[] nodes = new NBTNode[length];

        for (int i = 0; i < length; i++){
            nodes[i] = parseNode(type, false);

            if(Objects.equals(nodes[i].getType(), "end")){
                throw new UnexpectedNBTNodeException("Cannot have end node in list");
            } else if(!Objects.equals(nodes[i].getType(), childType)){
                throw new UnexpectedNBTNodeException("Cannot have different node types in list");
            }

        }

        node.addAll(nodes);

        return node;

    }

    private NBTNode parseCompound() throws IOException, UnexpectedNBTNodeException {
        NBTCompoundNode node = new NBTCompoundNode();
        node.setType("compound");

        ArrayList<NBTNode> nodes = new ArrayList<>();

        currentLevel++;

        while (true){
            NBTNode child = parseNode();

            if(Objects.equals(child.getType(), "end"))
                break;

            nodes.add(child);

        }

        node.addAll(nodes);

        return node;
    }

}

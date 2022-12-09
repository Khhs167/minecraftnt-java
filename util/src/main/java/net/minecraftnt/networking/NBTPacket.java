package net.minecraftnt.networking;

import net.minecraftnt.nbt.NBTReader;
import net.minecraftnt.nbt.NBTWriter;
import net.minecraftnt.nbt.nodes.*;
import net.minecraftnt.util.GenericUtil;
import net.minecraftnt.util.Identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

/**
 * An NBTPacket is a packet type that can store an NBT tag.
 */
public class NBTPacket extends Packet {

    public static final Identifier IDENTIFIER = new Identifier("minecraftnt", "packets.nbt");

    private NBTNode node;
    private Identifier type;

    public NBTPacket() {
        super(IDENTIFIER);
    }

    /**
     * Set the node of the packet
     * @param node The node to set it to
     * @return The current packet, for chain calls
     */
    public NBTPacket setNode(NBTNode node) {
        this.node = node;
        return this;
    }

    /**
     * Set the type of the NBT node
     * @param type The type to set it to
     * @return The current packet, for chain calls
     */
    public NBTPacket setType(Identifier type) {
        this.type = type;
        return this;
    }

    /**
     * Get the type of the NBT node, not the packet
     * @return The type identifier
     */
    public Identifier getType() {
        return type;
    }

    /**
     * Get the NBT node
     * @return The NBT node
     */
    public NBTNode getNode() {
        return node;
    }

    @Override
    public void load(DataInputStream stream) throws IOException {
        try {
            String name = GenericUtil.readString(stream);
            String namespace = GenericUtil.readString(stream);
            type = new Identifier(name, namespace);

            NBTReader reader = new NBTReader(stream);
            reader.parse();

            node = reader.getRoot();

        } catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void writeValue(NBTValueNode valueNode, NBTWriter writer) {
        switch (valueNode.getType()) {
            case "byte":
                writer.writeByte(valueNode.getName(), (byte)valueNode.getValue());
                break;
            case "double":
                writer.writeDouble(valueNode.getName(), (double)valueNode.getValue());
                break;
            case "float":
                writer.writeFloat(valueNode.getName(), (float)valueNode.getValue());
                break;
            case "int":
                writer.writeInt(valueNode.getName(), (int)valueNode.getValue());
                break;
            case "long":
                writer.writeLong(valueNode.getName(), (long)valueNode.getValue());
                break;
            case "short":
                writer.writeShort(valueNode.getName(), (short)valueNode.getValue());
                break;
            case "string":
                writer.writeString(valueNode.getName(), (String)valueNode.getValue());
                break;
            case "byte[]":
                writer.writeBytes(valueNode.getName(), (byte[])valueNode.getValue());
                break;
            case "int[]":
                writer.writeInts(valueNode.getName(), (int[])valueNode.getValue());
                break;
            case "long[]":
                writer.writeLongs(valueNode.getName(), (long[])valueNode.getValue());
                break;
        }
    }

    /**
     * An internally used function to write an NBT node
     * @param node The node to write
     * @param writer The writer to write with
     */
    public void writeNode(NBTNode node, NBTWriter writer) {

        if(node instanceof NBTCompoundNode compoundNode) {
            writer.beginCompound(compoundNode.getName());

            NBTNode[] children = compoundNode.getArray(new NBTNode[0]);
            for(NBTNode child : children) {
                writeNode(child, writer);
            }

            writer.endCompound();
        } else if(node instanceof NBTListNode<?> listNode) {

            if(node.getType().endsWith("[]")){

                switch (node.getType()){
                    case "byte[]":
                        byte[] bytes = new byte[listNode.getLength()];

                        for(int i = 0; i < listNode.getLength(); i++){
                            bytes[i] = (byte)listNode.get(i);
                        }
                        writer.writeBytes(node.getName(), bytes);
                        break;
                    case "int[]":
                        int[] ints = new int[listNode.getLength()];

                        for(int i = 0; i < listNode.getLength(); i++){
                            ints[i] = (int)listNode.get(i);
                        }
                        writer.writeInts(node.getName(), ints);
                        break;
                    case "long[]":
                        long[] longs = new long[listNode.getLength()];

                        for(int i = 0; i < listNode.getLength(); i++){
                            longs[i] = (byte)listNode.get(i);
                        }
                        writer.writeLongs(node.getName(), longs);
                        break;
                }

                return;
            }

            writer.beginList(listNode.getName(), listNode.getContentType());

            for(int i = 0; i < listNode.getLength(); i++) {
                Object valueObj = listNode.get(i);

                if(valueObj instanceof String value) {
                    writer.writeString("", value);
                } else if(valueObj instanceof Byte value) {
                    writer.writeByte("", value);
                } else if(valueObj instanceof Short value) {
                    writer.writeShort("", value);
                } else if(valueObj instanceof Integer value) {
                    writer.writeInt("", value);
                } else if(valueObj instanceof Double value) {
                    writer.writeDouble("", value);
                } else if(valueObj instanceof Float value) {
                    writer.writeFloat("", value);
                } else if(valueObj instanceof NBTNode value) {
                    writeNode(value, writer);
                }

            }

            writer.endList();
        } else if(node instanceof NBTValueNode<?> valueNode) {
            writeValue(valueNode, writer);
        }
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        stream.write(type.getName().getBytes(StandardCharsets.UTF_8));
        stream.write(0);
        stream.write(type.getNamespace().getBytes(StandardCharsets.UTF_8));
        stream.write(0);
        NBTWriter writer = new NBTWriter(stream);
        writeNode(node, writer);
        writer.flush();
    }
}

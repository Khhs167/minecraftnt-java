package net.minecraftnt.saving;

import net.minecraftnt.nbt.NBTReader;
import net.minecraftnt.nbt.NBTWriter;
import net.minecraftnt.nbt.exceptions.UnexpectedNBTNodeException;
import net.minecraftnt.nbt.nodes.NBTCompoundNode;
import net.minecraftnt.nbt.nodes.NBTListNode;
import net.minecraftnt.nbt.nodes.NBTValueNode;
import net.minecraftnt.server.world.Chunk;
import net.minecraftnt.server.world.ChunkPosition;
import net.minecraftnt.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;

public final class ChunkNBTInterface {
    public static Chunk load(NBTReader reader, ChunkPosition position) throws IOException, UnexpectedNBTNodeException {
        reader.parse();

        NBTCompoundNode rootNode = (NBTCompoundNode) reader.getRoot();

        Chunk chunk = new Chunk(position);

        assert rootNode.getChild("idList") instanceof NBTListNode;
        NBTListNode<?> idNode = (NBTListNode<?>) rootNode.getChild("idList");

        for (Object id : idNode.getData()) {
            if(id instanceof NBTCompoundNode compoundNode) {
                chunk.setBlockID(compoundNode.getShort("key"), new Identifier(compoundNode.getString("namespace"), compoundNode.getString("name")));
            }
        }

        int index = 0;

        assert rootNode.getChild("data") instanceof NBTListNode;
        NBTListNode<?> dataNode = (NBTListNode<?>) rootNode.getChild("data");

        for (int x = 0; x < Chunk.CHUNK_WIDTH; x++) {
            for (int z = 0; z < Chunk.CHUNK_WIDTH; z++) {
                for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
                    Object data = dataNode.get(index);
                    if(data instanceof NBTValueNode<?> valueNode) {
                        Object val = valueNode.getValue();
                        if(val instanceof Short value) {
                            chunk.setBlock(x, y, z, value);
                        }
                    }
                    index++;
                }
            }
        }
        return chunk;
    }

    public static void write(NBTWriter writer, Chunk chunk) throws IOException {
        writer.beginCompound("chunk");

        writer.beginList("idList", "compound");

        var idMap = chunk.getBlockMap();

        for(var key : idMap.keySet()) {
            writer.beginCompound("");
            writer.writeShort("key", key);
            writer.writeString("namespace", idMap.get(key).getNamespace());
            writer.writeString("name", idMap.get(key).getName());
            writer.endCompound();
        }
        writer.endList();

        writer.writeInt("width", Chunk.CHUNK_WIDTH);
        writer.writeInt("height", Chunk.CHUNK_HEIGHT);

        writer.beginList("data", "short");
        for(int x = 0; x < Chunk.CHUNK_WIDTH; x++){
            for(int z = 0; z < Chunk.CHUNK_WIDTH; z++){
                for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++){
                    writer.writeShort("", chunk.getID(x, y, z));
                }
            }
        }
        writer.endList();

        writer.endCompound();
        writer.flush();
    }
}

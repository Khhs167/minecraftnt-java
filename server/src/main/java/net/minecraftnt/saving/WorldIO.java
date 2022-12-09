package net.minecraftnt.saving;

import net.minecraftnt.nbt.NBTReader;
import net.minecraftnt.nbt.NBTWriter;
import net.minecraftnt.nbt.exceptions.UnexpectedNBTNodeException;
import net.minecraftnt.nbt.nodes.NBTCompoundNode;
import net.minecraftnt.nbt.nodes.NBTListNode;
import net.minecraftnt.nbt.nodes.NBTValueNode;
import net.minecraftnt.server.data.GameData;
import net.minecraftnt.server.world.Chunk;
import net.minecraftnt.server.world.ChunkPosition;
import net.minecraftnt.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WorldIO {
    public static final Logger LOGGER = LogManager.getLogger(WorldIO.class);
    private final String worldPath;
    private final File worldDirectory;
    private NBTCompoundNode worldNBT;

    public WorldIO(String world) {
        try {
            worldPath = GameData.getWorldDirectory(world);
            worldDirectory = new File(worldPath);
            if (!worldDirectory.exists()) {
                if (!worldDirectory.mkdir()) {
                    LOGGER.fatal("Could not create world directory");
                    throw new RuntimeException();
                }
            }

            if (new File(worldPath, "world.nbt").exists()) {
                loadNBT();
            } else {
                NBTWriter writer = new NBTWriter(new FileOutputStream(new File(worldPath, "world.nbt")));
                writer.beginCompound("world");
                writer.endCompound();
                writer.flush();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadNBT() throws IOException, UnexpectedNBTNodeException {
        File file = new File(worldPath, "world.nbt");
        NBTReader reader = new NBTReader(new FileInputStream(file));
        reader.parse();
        this.worldNBT = (NBTCompoundNode) reader.getRoot();
    }

    public Chunk loadChunk(ChunkPosition position) {
        try {

            File chunksDir = new File(worldPath, "chunks");
            File chunkFile = new File(chunksDir, position.toString() + ".nbt");

            if (chunkFile.exists()) {
                NBTReader reader = new NBTReader(new FileInputStream(chunkFile));
                reader.parse();

                NBTCompoundNode rootNode = (NBTCompoundNode) reader.getRoot();

                Chunk chunk = new Chunk(position);

                assert rootNode.getChild("idList") instanceof NBTListNode;
                NBTListNode<NBTCompoundNode> idNode = (NBTListNode<NBTCompoundNode>) rootNode.getChild("idList");

                for (NBTCompoundNode id : idNode.getData()) {
                    chunk.setBlockID(id.getShort("key"), new Identifier(id.getString("namespace"), id.getString("name")));
                }

                int index = 0;

                assert rootNode.getChild("data") instanceof NBTListNode;
                NBTListNode<NBTValueNode<Short>> dataNode = (NBTListNode<NBTValueNode<Short>>) rootNode.getChild("data");

                for (int x = 0; x < Chunk.CHUNK_WIDTH; x++) {
                    for (int z = 0; z < Chunk.CHUNK_WIDTH; z++) {
                        for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
                            chunk.setBlock(x, y, z, dataNode.get(index).getValue());
                            index++;
                        }
                    }
                }
                LOGGER.debug("Loaded chunk from file!");
                return chunk;


            }

        } catch (Exception exception) {
            LOGGER.error(exception.toString());
        }

        return null;
    }

    public void saveChunk(Chunk chunk, ChunkPosition position) {
        File chunksDir = new File(worldPath, "chunks");
        File chunkFile = new File(chunksDir, position.toString() + ".nbt");
        try {
            chunksDir.mkdirs();
            chunkFile.createNewFile();

            NBTWriter writer = new NBTWriter(new FileOutputStream(chunkFile));
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

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

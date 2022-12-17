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
                return ChunkNBTInterface.load(reader, position);


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
            ChunkNBTInterface.write(writer, chunk);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

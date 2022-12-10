package net.minecraftnt.server.packets;

import net.minecraftnt.nbt.nodes.NBTCompoundNode;
import net.minecraftnt.networking.NBTPacket;
import net.minecraftnt.networking.Packet;
import net.minecraftnt.server.world.Chunk;
import net.minecraftnt.server.world.ChunkPosition;
import net.minecraftnt.util.GenericUtil;
import net.minecraftnt.util.Identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.util.HashMap;

public class ChunksPacket extends Packet {
    public static final Identifier IDENTIFIER = new Identifier("minecraftnt", "packets.chunks");

    public short[][][] map;
    public float[][][] illuminationMap;
    public HashMap<Short, Identifier> idMap;
    public ChunkPosition position;

    public ChunksPacket() {
        super(IDENTIFIER);
    }

    public ChunksPacket(Chunk chunk) {
        super(IDENTIFIER);
        map = new short[Chunk.CHUNK_WIDTH][Chunk.CHUNK_HEIGHT][Chunk.CHUNK_WIDTH];
        for(int x = 0; x < Chunk.CHUNK_WIDTH; x++){
            for(int z = 0; z < Chunk.CHUNK_WIDTH; z++){
                for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++){
                    map[x][y][z] = chunk.getID(x, y, z);
                }
            }
        }

        /*illuminationMap = new float[Chunk.CHUNK_WIDTH][Chunk.CHUNK_HEIGHT][Chunk.CHUNK_WIDTH];
        for(int x = 0; x < Chunk.CHUNK_WIDTH; x++){
            for(int z = 0; z < Chunk.CHUNK_WIDTH; z++){
                for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++){
                    illuminationMap[x][y][z] = chunk.getIllumination(x, y, z);
                }
            }
        }*/

        idMap = chunk.getBlockMap();
        position = chunk.getPosition();
    }

    @Override
    public void load(DataInputStream stream) throws IOException {
        map = new short[Chunk.CHUNK_WIDTH][Chunk.CHUNK_HEIGHT][Chunk.CHUNK_WIDTH];
        for(int x = 0; x < Chunk.CHUNK_WIDTH; x++){
            for(int z = 0; z < Chunk.CHUNK_WIDTH; z++){
                for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++){
                    map[x][y][z] = stream.readShort();
                }
            }
        }

        /*illuminationMap = new float[Chunk.CHUNK_WIDTH][Chunk.CHUNK_HEIGHT][Chunk.CHUNK_WIDTH];
        for(int x = 0; x < Chunk.CHUNK_WIDTH; x++){
            for(int z = 0; z < Chunk.CHUNK_WIDTH; z++){
                for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++){
                    illuminationMap[x][y][z] = stream.readFloat();
                }
            }
        }*/
        idMap = new HashMap<>();
        int idCount = stream.readInt();
        for(int i = 0; i < idCount; i++) {
            short id = stream.readShort();
            String namespace = GenericUtil.readString(stream);
            String name = GenericUtil.readString(stream);
            idMap.put(id, new Identifier(namespace, name));
        }

        int x = stream.readInt();
        int z = stream.readInt();
        position = new ChunkPosition(x, z);
    }

    public Chunk getChunk() {
        Chunk chunk = new Chunk(position);
        for (short id : idMap.keySet()) {
            chunk.setBlockID(id, idMap.get(id));
        }

        for(int x = 0; x < Chunk.CHUNK_WIDTH; x++){
            for(int z = 0; z < Chunk.CHUNK_WIDTH; z++){
                for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++){
                    chunk.setBlock(x, y, z, map[x][y][z]);
                }
            }
        }

        return chunk;
    }

    @Override
    public void write(DataOutputStream stream) throws IOException {
        for(int x = 0; x < Chunk.CHUNK_WIDTH; x++){
            for(int z = 0; z < Chunk.CHUNK_WIDTH; z++){
                for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++){
                    stream.writeShort(map[x][y][z]);
                }
            }
        }

        /*for(int x = 0; x < Chunk.CHUNK_WIDTH; x++){
            for(int z = 0; z < Chunk.CHUNK_WIDTH; z++){
                for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++){
                    stream.writeFloat(illuminationMap[x][y][z]);
                }
            }
        }*/

        stream.writeInt(idMap.size());
        for(Short id : idMap.keySet()) {
            stream.writeShort(id);
            stream.writeBytes(idMap.get(id).getNamespace());
            stream.write(0);
            stream.writeBytes(idMap.get(id).getName());
            stream.write(0);
        }

        stream.writeInt(position.getX());
        stream.writeInt(position.getY());
    }
}

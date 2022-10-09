package net.minecraftnt.server.world.threading;

import net.minecraftnt.server.performance.ThreadedMethod;
import net.minecraftnt.server.world.World;
import net.minecraftnt.util.Vector2I;

public class RebuildChunkMethod implements ThreadedMethod {

    private final int cx;
    private final int cy;
    private final World world;

    public RebuildChunkMethod(int cx, int cy, World world){
        this.cx = cx;
        this.cy = cy;
        this.world = world;
    }

    @Override
    public void run() {
        world.chunks[cx][cy].rebuildLightmap(true);
        world.enqueueRebuild(new Vector2I(cx, cy));
    }
}

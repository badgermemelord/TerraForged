// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.tile.chunk;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.concurrent.batch.BatchTask;
import com.terraforged.engine.concurrent.batch.BatchTaskException;
import com.terraforged.engine.tile.Tile;
import com.terraforged.engine.world.heightmap.Heightmap;
import com.terraforged.engine.world.rivermap.Rivermap;

public class ChunkBatchTask implements BatchTask
{
    private static final String ERROR = "Failed to generate tile area: x=%s, z=%s, size=%sx%s";
    private final int x;
    private final int z;
    private final int size;
    private final Tile tile;
    private final Heightmap heightmap;
    private Notifier notifier;
    
    public ChunkBatchTask(final int x, final int z, final int size, final Tile tile, final Heightmap heightmap) {
        this.notifier = BatchTask.NONE;
        this.heightmap = heightmap;
        this.tile = tile;
        this.x = x;
        this.z = z;
        this.size = size;
    }
    
    @Override
    public void setNotifier(final Notifier notifier) {
        this.notifier = notifier;
    }
    
    @Override
    public void run() {
        try {
            this.drive();
        }
        catch (Throwable t) {
            t.printStackTrace();
            throw new BatchTaskException(String.format("Failed to generate tile area: x=%s, z=%s, size=%sx%s", this.x, this.z, this.size, this.size), t);
        }
        finally {
            this.notifier.markDone();
        }
    }
    
    private void drive() {
        final int maxX = Math.min(this.tile.getChunkSize().total, this.x + this.size);
        for (int maxZ = Math.min(this.tile.getChunkSize().total, this.z + this.size), cz = this.z; cz < maxZ; ++cz) {
            for (int cx = this.x; cx < maxX; ++cx) {
                this.driveOne(this.tile.getChunkWriter(cx, cz), this.heightmap);
            }
        }
    }
    
    protected void driveOne(final ChunkWriter chunk, final Heightmap heightmap) {
        Rivermap rivers = null;
        for (int dz = 0; dz < 16; ++dz) {
            for (int dx = 0; dx < 16; ++dx) {
                final Cell cell = chunk.genCell(dx, dz);
                final float x = (float)(chunk.getBlockX() + dx);
                final float z = (float)(chunk.getBlockZ() + dz);
                heightmap.applyBase(cell, x, z);
                rivers = Rivermap.get(cell, rivers, heightmap);
                heightmap.applyRivers(cell, x, z, rivers);
                heightmap.applyClimate(cell, x, z);
            }
        }
    }
    
    public static class Zoom extends ChunkBatchTask
    {
        private final float translateX;
        private final float translateZ;
        private final float zoom;
        
        public Zoom(final int x, final int z, final int size, final Tile tile, final Heightmap heightmap, final float translateX, final float translateZ, final float zoom) {
            super(x, z, size, tile, heightmap);
            this.translateX = translateX;
            this.translateZ = translateZ;
            this.zoom = zoom;
        }
        
        @Override
        protected void driveOne(final ChunkWriter chunk, final Heightmap heightmap) {
            Rivermap rivers = null;
            for (int dz = 0; dz < 16; ++dz) {
                for (int dx = 0; dx < 16; ++dx) {
                    final Cell cell = chunk.genCell(dx, dz);
                    final float x = (chunk.getBlockX() + dx) * this.zoom + this.translateX;
                    final float z = (chunk.getBlockZ() + dz) * this.zoom + this.translateZ;
                    heightmap.applyBase(cell, x, z);
                    rivers = Rivermap.get(cell, rivers, heightmap);
                    heightmap.applyRivers(cell, x, z, rivers);
                    heightmap.applyClimate(cell, x, z);
                }
            }
        }
    }
}

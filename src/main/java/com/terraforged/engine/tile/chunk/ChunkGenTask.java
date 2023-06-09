// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.tile.chunk;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.concurrent.batch.BatchTask;
import com.terraforged.engine.world.heightmap.Heightmap;
import com.terraforged.engine.world.rivermap.Rivermap;

public class ChunkGenTask implements BatchTask
{
    private final ChunkWriter chunk;
    private final Heightmap heightmap;
    private Notifier notifier;
    
    public ChunkGenTask(final ChunkWriter chunk, final Heightmap heightmap) {
        this.notifier = BatchTask.NONE;
        this.chunk = chunk;
        this.heightmap = heightmap;
    }
    
    @Override
    public void setNotifier(final Notifier notifier) {
        this.notifier = notifier;
    }
    
    @Override
    public void run() {
        try {
            this.driveOne(this.chunk, this.heightmap);
        }
        finally {
            this.notifier.markDone();
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
    
    public static class Zoom extends ChunkGenTask
    {
        private final float translateX;
        private final float translateZ;
        private final float zoom;
        
        public Zoom(final ChunkWriter chunk, final Heightmap heightmap, final float translateX, final float translateZ, final float zoom) {
            super(chunk, heightmap);
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

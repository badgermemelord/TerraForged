// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.tile.api;

import com.terraforged.engine.concurrent.Disposable;
import com.terraforged.engine.concurrent.task.LazyCallable;
import com.terraforged.engine.tile.Tile;
import com.terraforged.engine.tile.chunk.ChunkReader;

public interface TileProvider extends Disposable.Listener<Tile>
{
    int chunkToRegion(final int p0);
    
    void queueChunk(final int p0, final int p1);
    
    void queueRegion(final int p0, final int p1);
    
    LazyCallable<Tile> get(final long p0);
    
    LazyCallable<Tile> getOrCompute(final long p0);
    
    default Tile getTile(final long id) {
        return this.getOrCompute(id).get();
    }
    
    default Tile getTile(final int regionX, final int regionZ) {
        return this.getTile(Tile.getRegionId(regionX, regionZ));
    }
    
    default Tile getTileIfPresent(final long id) {
        final LazyCallable<Tile> entry = this.get(id);
        if (entry == null || !entry.isDone()) {
            return null;
        }
        return entry.get();
    }
    
    default Tile getTileIfPresent(final int regionX, final int regionZ) {
        return this.getTileIfPresent(Tile.getRegionId(regionX, regionZ));
    }
    
    default ChunkReader getChunk(final int chunkX, final int chunkZ) {
        final int regionX = this.chunkToRegion(chunkX);
        final int regionZ = this.chunkToRegion(chunkZ);
        return this.getTile(regionX, regionZ).getChunk(chunkX, chunkZ);
    }
}

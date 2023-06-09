// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.tile.gen;

import com.terraforged.engine.concurrent.cache.Cache;
import com.terraforged.engine.concurrent.cache.CacheEntry;
import com.terraforged.engine.concurrent.task.LazyCallable;
import com.terraforged.engine.concurrent.thread.ThreadPool;
import com.terraforged.engine.tile.Tile;
import com.terraforged.engine.tile.api.TileFactory;
import com.terraforged.engine.tile.api.TileProvider;

import java.util.concurrent.TimeUnit;

public class TileCache implements TileProvider
{
    public static final int QUEUING_MIN_POOL_SIZE = 4;
    private final boolean canQueue;
    private final TileFactory generator;
    private final ThreadPool threadPool;
    private final Cache<CacheEntry<Tile>> cache;
    
    public TileCache(final TileFactory generator, final ThreadPool threadPool) {
        this.canQueue = (threadPool.size() > 4);
        this.generator = generator;
        this.threadPool = threadPool;
        this.cache = new Cache<CacheEntry<Tile>>("TileCache", 256, 60L, 20L, TimeUnit.SECONDS);
        generator.setListener(this);
    }
    
    @Override
    public void onDispose(final Tile tile) {
        this.cache.remove(tile.getRegionId());
    }
    
    @Override
    public int chunkToRegion(final int coord) {
        return this.generator.chunkToRegion(coord);
    }
    
    @Override
    public CacheEntry<Tile> get(final long id) {
        return this.cache.get(id);
    }
    
    @Override
    public CacheEntry<Tile> getOrCompute(final long id) {
        return this.cache.computeIfAbsent(id, this::computeCacheEntry);
    }
    
    @Override
    public void queueChunk(final int chunkX, final int chunkZ) {
        if (!this.canQueue) {
            return;
        }
        this.queueRegion(this.chunkToRegion(chunkX), this.chunkToRegion(chunkZ));
    }
    
    @Override
    public void queueRegion(final int regionX, final int regionZ) {
        if (!this.canQueue) {
            return;
        }
        this.getOrCompute(Tile.getRegionId(regionX, regionZ));
    }
    
    protected CacheEntry<Tile> computeCacheEntry(final long id) {
        final int regionX = Tile.getRegionX(id);
        final int regionZ = Tile.getRegionZ(id);
        final LazyCallable<Tile> tile = this.generator.getTile(regionX, regionZ);
        return CacheEntry.computeAsync(tile, this.threadPool);
    }
}

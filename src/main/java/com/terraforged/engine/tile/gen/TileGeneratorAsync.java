// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.tile.gen;

import com.terraforged.engine.concurrent.Disposable;
import com.terraforged.engine.concurrent.task.LazyCallable;
import com.terraforged.engine.concurrent.thread.ThreadPool;
import com.terraforged.engine.tile.Tile;
import com.terraforged.engine.tile.api.TileFactory;

public class TileGeneratorAsync implements TileFactory
{
    protected final TileGenerator generator;
    protected final ThreadPool threadPool;
    
    public TileGeneratorAsync(final TileGenerator generator, final ThreadPool threadPool) {
        this.generator = generator;
        this.threadPool = threadPool;
    }
    
    @Override
    public int chunkToRegion(final int i) {
        return this.generator.chunkToRegion(i);
    }
    
    @Override
    public void setListener(final Disposable.Listener<Tile> listener) {
        this.generator.setListener(listener);
    }
    
    @Override
    public LazyCallable<Tile> getTile(final int regionX, final int regionZ) {
        return LazyCallable.callAsync(this.generator.getTile(regionX, regionZ), this.threadPool);
    }
    
    @Override
    public LazyCallable<Tile> getTile(final float centerX, final float centerZ, final float zoom, final boolean filter) {
        return LazyCallable.callAsync(this.generator.getTile(centerX, centerZ, zoom, filter), this.threadPool);
    }
    
    @Override
    public TileFactory async() {
        return this;
    }
    
    @Override
    public TileCache cached() {
        return new TileCache(this.generator, this.threadPool);
    }
}

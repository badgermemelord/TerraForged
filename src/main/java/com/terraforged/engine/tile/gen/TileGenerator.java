// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.tile.gen;

import com.terraforged.engine.concurrent.Disposable;
import com.terraforged.engine.concurrent.task.LazyCallable;
import com.terraforged.engine.concurrent.thread.ThreadPool;
import com.terraforged.engine.concurrent.thread.ThreadPools;
import com.terraforged.engine.tile.Tile;
import com.terraforged.engine.tile.api.TileFactory;
import com.terraforged.engine.tile.api.TileProvider;
import com.terraforged.engine.world.WorldGenerator;
import com.terraforged.engine.world.WorldGeneratorFactory;

public class TileGenerator implements TileFactory
{
    protected final int factor;
    protected final int border;
    protected final int batchSize;
    protected final ThreadPool threadPool;
    protected final WorldGenerator generator;
    private final TileResources resources;
    private Disposable.Listener<Tile> listener;
    
    protected TileGenerator(final Builder builder) {
        this.resources = new TileResources();
        this.listener = (r -> {});
        this.factor = builder.factor;
        this.border = builder.border;
        this.batchSize = builder.batchSize;
        this.generator = builder.factory.get();
        this.threadPool = Builder.getOrDefaultPool(builder);
    }
    
    public WorldGenerator getGenerator() {
        return this.generator;
    }
    
    @Override
    public void setListener(final Disposable.Listener<Tile> listener) {
        this.listener = listener;
    }
    
    @Override
    public int chunkToRegion(final int i) {
        return i >> this.factor;
    }
    
    @Override
    public LazyCallable<Tile> getTile(final int regionX, final int regionZ) {
        return new CallableTile(regionX, regionZ, this);
    }
    
    @Override
    public LazyCallable<Tile> getTile(final float centerX, final float centerZ, final float zoom, final boolean filter) {
        return new CallableZoomTile(centerX, centerZ, zoom, filter, this);
    }
    
    @Override
    public TileFactory async() {
        return new TileGeneratorAsync(this, this.threadPool);
    }
    
    @Override
    public TileProvider cached() {
        return new TileCache(this, this.threadPool);
    }
    
    public Tile generateRegion(final int regionX, final int regionZ) {
        final Tile tile = this.createEmptyRegion(regionX, regionZ);
        tile.generate(this.generator.getHeightmap());
        this.postProcess(tile);
        return tile;
    }
    
    public Tile generateRegion(final float centerX, final float centerZ, final float zoom, final boolean filter) {
        final Tile tile = this.createEmptyRegion(0, 0);
        tile.generate(this.generator.getHeightmap(), centerX, centerZ, zoom);
        this.postProcess(tile, filter);
        return tile;
    }
    
    public Tile createEmptyRegion(final int regionX, final int regionZ) {
        return new Tile(regionX, regionZ, this.factor, this.border, this.resources, this.listener);
    }
    
    protected void postProcess(final Tile tile) {
        this.generator.getFilters().apply(tile, true);
    }
    
    protected void postProcess(final Tile tile, final boolean filter) {
        this.generator.getFilters().apply(tile, filter);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder
    {
        protected int factor;
        protected int border;
        protected int batchSize;
        protected boolean striped;
        protected WorldGeneratorFactory factory;
        private ThreadPool threadPool;
        
        public Builder() {
            this.factor = 0;
            this.border = 0;
            this.batchSize = 0;
            this.striped = false;
        }
        
        public Builder size(final int factor, final int border) {
            return this.factor(factor).border(border);
        }
        
        public Builder factor(final int factor) {
            this.factor = factor;
            return this;
        }
        
        public Builder border(final int border) {
            this.border = border;
            return this;
        }
        
        public Builder pool(final ThreadPool threadPool) {
            this.threadPool = threadPool;
            return this;
        }
        
        public Builder factory(final WorldGeneratorFactory factory) {
            this.factory = factory;
            return this;
        }
        
        public Builder batch(final int batchSize) {
            this.batchSize = batchSize;
            return this;
        }
        
        public Builder striped() {
            this.striped = true;
            return this;
        }
        
        public TileGenerator build() {
            if (this.batchSize <= 0) {
                return new TileGenerator(this);
            }
            if (this.striped) {
                return new TileGeneratorStriped(this);
            }
            return new TileGeneratorBatched(this);
        }
        
        protected static ThreadPool getOrDefaultPool(final Builder builder) {
            return (builder.threadPool == null) ? ThreadPools.NONE : builder.threadPool;
        }
    }
}

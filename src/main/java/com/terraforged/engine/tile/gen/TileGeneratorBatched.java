// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.tile.gen;

import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.concurrent.batch.Batcher;
import com.terraforged.engine.tile.Tile;

public class TileGeneratorBatched extends TileGenerator
{
    public TileGeneratorBatched(final Builder builder) {
        super(builder);
    }
    
    @Override
    public Tile generateRegion(final int regionX, final int regionZ) {
        final Tile tile = this.createEmptyRegion(regionX, regionZ);
        try (final Resource<Batcher> batcher = this.threadPool.batcher()) {
            tile.generateArea(this.generator.getHeightmap(), batcher.get(), this.batchSize);
        }
        this.postProcess(tile);
        return tile;
    }
    
    @Override
    public Tile generateRegion(final float centerX, final float centerZ, final float zoom, final boolean filter) {
        final Tile tile = this.createEmptyRegion(0, 0);
        try (final Resource<Batcher> batcher = this.threadPool.batcher()) {
            tile.generateArea(this.generator.getHeightmap(), batcher.get(), this.batchSize, centerX, centerZ, zoom);
        }
        this.postProcess(tile, filter);
        return tile;
    }
}

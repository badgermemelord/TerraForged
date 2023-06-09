// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.tile.gen;

import com.terraforged.engine.concurrent.task.LazyCallable;
import com.terraforged.engine.tile.Tile;

public class CallableTile extends LazyCallable<Tile>
{
    private final int regionX;
    private final int regionZ;
    private final TileGenerator generator;
    
    public CallableTile(final int regionX, final int regionZ, final TileGenerator generator) {
        this.regionX = regionX;
        this.regionZ = regionZ;
        this.generator = generator;
    }
    
    @Override
    protected Tile create() {
        return this.generator.generateRegion(this.regionX, this.regionZ);
    }
}

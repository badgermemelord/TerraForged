// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.tile.api;

import com.terraforged.engine.concurrent.Disposable;
import com.terraforged.engine.concurrent.task.LazyCallable;
import com.terraforged.engine.tile.Tile;

public interface TileFactory
{
    int chunkToRegion(final int p0);
    
    void setListener(final Disposable.Listener<Tile> p0);
    
    LazyCallable<Tile> getTile(final int p0, final int p1);
    
    LazyCallable<Tile> getTile(final float p0, final float p1, final float p2, final boolean p3);
    
    TileFactory async();
    
    TileProvider cached();
}

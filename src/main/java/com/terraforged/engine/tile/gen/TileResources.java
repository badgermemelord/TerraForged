// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.tile.gen;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.concurrent.pool.ArrayPool;
import com.terraforged.engine.tile.Tile;

public class TileResources
{
    public final ArrayPool<Cell> blocks;
    public final ArrayPool<Tile.GenChunk> chunks;
    
    public TileResources() {
        this.blocks = ArrayPool.of(100, Cell[]::new);
        this.chunks = ArrayPool.of(100, Tile.GenChunk[]::new);
    }
}

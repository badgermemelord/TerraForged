// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.heightmap;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.tile.Tile;
import com.terraforged.engine.tile.api.TileProvider;
import com.terraforged.engine.tile.chunk.ChunkReader;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.terrain.TerrainType;

public class WorldLookup
{
    private final float waterLevel;
    private final float beachLevel;
    private final TileProvider cache;
    private final Heightmap heightmap;
    
    public WorldLookup(final GeneratorContext context) {
        this.cache = context.cache.get();
        this.heightmap = context.worldGenerator.get().getHeightmap();
        this.waterLevel = context.levels.water;
        this.beachLevel = context.levels.water(5);
    }
    
    public Resource<Cell> get(final int x, final int z) {
        final ChunkReader chunk = this.cache.getChunk(x >> 4, z >> 4);
        final Resource<Cell> cell = Cell.getResource();
        cell.get().copyFrom(chunk.getCell(x & 0xF, z & 0xF));
        return cell;
    }
    
    public Resource<Cell> getCell(final int x, final int z) {
        return this.getCell(x, z, false);
    }
    
    public Resource<Cell> getCell(final int x, final int z, final boolean load) {
        final Resource<Cell> resource = Cell.getResource();
        this.applyCell(resource.get(), x, z, load);
        return resource;
    }
    
    public void applyCell(final Cell cell, final int x, final int z) {
        this.applyCell(cell, x, z, false);
    }
    
    public void applyCell(final Cell cell, final int x, final int z, final boolean load) {
        if (load && this.computeAccurate(cell, x, z)) {
            return;
        }
        if (this.computeCached(cell, x, z)) {
            return;
        }
        this.compute(cell, x, z);
    }
    
    private boolean computeAccurate(final Cell cell, final int x, final int z) {
        final int rx = this.cache.chunkToRegion(x >> 4);
        final int rz = this.cache.chunkToRegion(z >> 4);
        final Tile tile = this.cache.getTile(rx, rz);
        final Cell c = tile.getCell(x, z);
        if (c != null) {
            cell.copyFrom(c);
        }
        return cell.terrain != null;
    }
    
    private boolean computeCached(final Cell cell, final int x, final int z) {
        final int rx = this.cache.chunkToRegion(x >> 4);
        final int rz = this.cache.chunkToRegion(z >> 4);
        final Tile tile = this.cache.getTileIfPresent(rx, rz);
        if (tile != null) {
            final Cell c = tile.getCell(x, z);
            if (c != null) {
                cell.copyFrom(c);
            }
            return cell.terrain != null;
        }
        return false;
    }
    
    private void compute(final Cell cell, final int x, final int z) {
        this.heightmap.apply(cell, (float)x, (float)z);
        if (cell.terrain == TerrainType.COAST && cell.value > this.waterLevel && cell.value <= this.beachLevel) {
            cell.terrain = TerrainType.BEACH;
        }
    }
}

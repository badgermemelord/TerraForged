//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.heightmap;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.tile.Tile;
import com.terraforged.engine.tile.api.TileProvider;
import com.terraforged.engine.tile.chunk.ChunkReader;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.WorldGeneratorFactory;
import com.terraforged.engine.world.terrain.TerrainType;

public class WorldLookup {
    private final float waterLevel;
    private final float beachLevel;
    private final TileProvider cache;
    private final Heightmap heightmap;

    public WorldLookup(GeneratorContext context) {
        this.cache = (TileProvider)context.cache.get();
        this.heightmap = ((WorldGeneratorFactory)context.worldGenerator.get()).getHeightmap();
        this.waterLevel = context.levels.water;
        this.beachLevel = context.levels.water(5);
    }

    public Resource<Cell> get(int x, int z) {
        ChunkReader chunk = this.cache.getChunk(x >> 4, z >> 4);
        Resource<Cell> cell = Cell.getResource();
        ((Cell)cell.get()).copyFrom(chunk.getCell(x & 15, z & 15));
        return cell;
    }

    public Resource<Cell> getCell(int x, int z) {
        return this.getCell(x, z, false);
    }

    public Resource<Cell> getCell(int x, int z, boolean load) {
        Resource<Cell> resource = Cell.getResource();
        this.applyCell((Cell)resource.get(), x, z, load);
        return resource;
    }

    public void applyCell(Cell cell, int x, int z) {
        this.applyCell(cell, x, z, false);
    }

    public void applyCell(Cell cell, int x, int z, boolean load) {
        if (!load || !this.computeAccurate(cell, x, z)) {
            if (!this.computeCached(cell, x, z)) {
                this.compute(cell, x, z);
            }
        }
    }

    private boolean computeAccurate(Cell cell, int x, int z) {
        int rx = this.cache.chunkToRegion(x >> 4);
        int rz = this.cache.chunkToRegion(z >> 4);
        Tile tile = this.cache.getTile(rx, rz);
        Cell c = tile.getCell(x, z);
        if (c != null) {
            cell.copyFrom(c);
        }

        return cell.terrain != null;
    }

    private boolean computeCached(Cell cell, int x, int z) {
        int rx = this.cache.chunkToRegion(x >> 4);
        int rz = this.cache.chunkToRegion(z >> 4);
        Tile tile = this.cache.getTileIfPresent(rx, rz);
        if (tile != null) {
            Cell c = tile.getCell(x, z);
            if (c != null) {
                cell.copyFrom(c);
            }

            return cell.terrain != null;
        } else {
            return false;
        }
    }

    private void compute(Cell cell, int x, int z) {
        this.heightmap.apply(cell, (float)x, (float)z);
        if (cell.terrain == TerrainType.COAST && cell.value > this.waterLevel && cell.value <= this.beachLevel) {
            cell.terrain = TerrainType.BEACH;
        }
    }
}

//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.heightmap;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.concurrent.cache.map.LoadBalanceLongMap;
import com.terraforged.engine.concurrent.cache.map.LongMap;
import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.engine.world.rivermap.Rivermap;
import com.terraforged.engine.world.terrain.TerrainType;
import java.util.function.LongFunction;

public class HeightmapCache {
    public static final int CACHE_SIZE = 4096;
    private final float waterLevel;
    private final float beachLevel;
    private final Heightmap heightmap;
    private final LongMap<Cell> cache;
    private final LongFunction<Cell> compute = this::compute;
    private final LongFunction<Cell> contextCompute = this::contextCompute;
    private final ThreadLocal<HeightmapCache.CachedContext> contextLocal = ThreadLocal.withInitial(() -> new HeightmapCache.CachedContext());

    public HeightmapCache(Heightmap heightmap) {
        this(heightmap, 4096);
    }

    public HeightmapCache(Heightmap heightmap, int size) {
        this.heightmap = heightmap;
        this.waterLevel = heightmap.getLevels().water;
        this.beachLevel = heightmap.getLevels().water(5);
        this.cache = new LoadBalanceLongMap(Runtime.getRuntime().availableProcessors(), size);
    }

    public Cell get(int x, int z) {
        long index = PosUtil.pack(x, z);
        return (Cell)this.cache.computeIfAbsent(index, this.compute);
    }

    public Rivermap generate(Cell cell, int x, int z, Rivermap rivermap) {
        HeightmapCache.CachedContext context = (HeightmapCache.CachedContext)this.contextLocal.get();

        Rivermap var9;
        try {
            context.cell = cell;
            context.rivermap = rivermap;
            long index = PosUtil.pack(x, z);
            Cell value = (Cell)this.cache.computeIfAbsent(index, this.contextCompute);
            if (value != cell) {
                cell.copyFrom(value);
            }

            var9 = context.rivermap;
        } finally {
            context.rivermap = null;
        }

        return var9;
    }

    private Cell compute(long index) {
        int x = PosUtil.unpackLeft(index);
        int z = PosUtil.unpackRight(index);
        Cell cell = new Cell();
        this.heightmap.apply(cell, (float)x, (float)z);
        if (cell.terrain == TerrainType.COAST && cell.value > this.waterLevel && cell.value <= this.beachLevel) {
            cell.terrain = TerrainType.BEACH;
        }

        return cell;
    }

    private Cell contextCompute(long index) {
        HeightmapCache.CachedContext context = (HeightmapCache.CachedContext)this.contextLocal.get();
        int x = PosUtil.unpackLeft(index);
        int z = PosUtil.unpackRight(index);
        this.heightmap.applyBase(context.cell, (float)x, (float)z);
        context.rivermap = Rivermap.get(context.cell, context.rivermap, this.heightmap);
        this.heightmap.applyRivers(context.cell, (float)x, (float)z, context.rivermap);
        this.heightmap.applyClimate(context.cell, (float)x, (float)z);
        return context.cell;
    }

    private static class CachedContext {
        private Cell cell;
        private Rivermap rivermap;

        private CachedContext() {
        }
    }
}

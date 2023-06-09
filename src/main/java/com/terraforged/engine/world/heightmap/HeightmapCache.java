// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.heightmap;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.concurrent.cache.map.LoadBalanceLongMap;
import com.terraforged.engine.concurrent.cache.map.LongMap;
import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.engine.world.rivermap.Rivermap;
import com.terraforged.engine.world.terrain.TerrainType;

import java.util.function.LongFunction;

public class HeightmapCache
{
    public static final int CACHE_SIZE = 4096;
    private final float waterLevel;
    private final float beachLevel;
    private final Heightmap heightmap;
    private final LongMap<Cell> cache;
    private final LongFunction<Cell> compute;
    private final LongFunction<Cell> contextCompute;
    private final ThreadLocal<CachedContext> contextLocal;
    
    public HeightmapCache(final Heightmap heightmap) {
        this(heightmap, 4096);
    }
    
    public HeightmapCache(final Heightmap heightmap, final int size) {
        this.compute = this::compute;
        this.contextCompute = this::contextCompute;
        this.contextLocal = ThreadLocal.withInitial(() -> new CachedContext());
        this.heightmap = heightmap;
        this.waterLevel = heightmap.getLevels().water;
        this.beachLevel = heightmap.getLevels().water(5);
        this.cache = new LoadBalanceLongMap<Cell>(Runtime.getRuntime().availableProcessors(), size);
    }
    
    public Cell get(final int x, final int z) {
        final long index = PosUtil.pack(x, z);
        return this.cache.computeIfAbsent(index, this.compute);
    }
    
    public Rivermap generate(final Cell cell, final int x, final int z, final Rivermap rivermap) {
        final CachedContext context = this.contextLocal.get();
        try {
            context.cell = cell;
            context.rivermap = rivermap;
            final long index = PosUtil.pack(x, z);
            final Cell value = this.cache.computeIfAbsent(index, this.contextCompute);
            if (value != cell) {
                cell.copyFrom(value);
            }
            return context.rivermap;
        }
        finally {
            context.rivermap = null;
        }
    }
    
    private Cell compute(final long index) {
        final int x = PosUtil.unpackLeft(index);
        final int z = PosUtil.unpackRight(index);
        final Cell cell = new Cell();
        this.heightmap.apply(cell, (float)x, (float)z);
        if (cell.terrain == TerrainType.COAST && cell.value > this.waterLevel && cell.value <= this.beachLevel) {
            cell.terrain = TerrainType.BEACH;
        }
        return cell;
    }
    
    private Cell contextCompute(final long index) {
        final CachedContext context = this.contextLocal.get();
        final int x = PosUtil.unpackLeft(index);
        final int z = PosUtil.unpackRight(index);
        this.heightmap.applyBase(context.cell, (float)x, (float)z);
        context.rivermap = Rivermap.get(context.cell, context.rivermap, this.heightmap);
        this.heightmap.applyRivers(context.cell, (float)x, (float)z, context.rivermap);
        this.heightmap.applyClimate(context.cell, (float)x, (float)z);
        return context.cell;
    }
    
    private static class CachedContext
    {
        private Cell cell;
        private Rivermap rivermap;
    }
}

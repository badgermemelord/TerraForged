// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.rivermap;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.concurrent.cache.Cache;
import com.terraforged.engine.concurrent.cache.map.LongMap;
import com.terraforged.engine.concurrent.cache.map.StampedLongMap;
import com.terraforged.engine.util.pos.PosUtil;

import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

public class RiverCache
{
    protected final RiverGenerator generator;
    protected final Cache<Rivermap> cache;
    
    public RiverCache(final RiverGenerator generator) {
        this.cache = new Cache<Rivermap>("RiverCache", 32, 5L, 1L, TimeUnit.MINUTES, (IntFunction<LongMap<Rivermap>>)StampedLongMap::new);
        this.generator = generator;
    }
    
    public Rivermap getRivers(final Cell cell) {
        return this.getRivers(cell.continentX, cell.continentZ);
    }
    
    public Rivermap getRivers(final int x, final int z) {
        return this.cache.computeIfAbsent(PosUtil.pack(x, z), id -> this.generator.generateRivers(PosUtil.unpackLeft(id), PosUtil.unpackRight(id), id));
    }
}

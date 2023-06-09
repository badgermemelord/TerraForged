// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world;

import com.terraforged.engine.world.heightmap.Heightmap;

public class WorldGenerator
{
    private final Heightmap heightmap;
    private final WorldFilters filters;
    
    public WorldGenerator(final Heightmap heightmap, final WorldFilters filters) {
        this.heightmap = heightmap;
        this.filters = filters;
    }
    
    public Heightmap getHeightmap() {
        return this.heightmap;
    }
    
    public WorldFilters getFilters() {
        return this.filters;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.BiomeContext;
import com.terraforged.engine.world.biome.type.BiomeType;

public class WarmLakeModifier implements BiomeModifier
{
    private final int match;
    private final int replace;
    
    public <T> WarmLakeModifier(final BiomeContext<T> context, final T match, final T replace) {
        this.match = context.getId(match);
        this.replace = context.getId(replace);
    }
    
    @Override
    public int priority() {
        return 0;
    }
    
    @Override
    public boolean test(final int biome, final Cell cell) {
        return biome == this.match && cell.biome != BiomeType.DESERT && cell.terrain.isLake();
    }
    
    @Override
    public int modify(final int in, final Cell cell, final int x, final int z) {
        return this.replace;
    }
}

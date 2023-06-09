// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.BiomeContext;
import com.terraforged.engine.world.biome.type.BiomeType;

public class WetlandModifier implements BiomeModifier
{
    private final int wetland;
    private final int coldWetland;
    private final int frozenWetland;
    
    public <T> WetlandModifier(final BiomeContext<T> context, final T normal, final T cold, final T frozen) {
        this.wetland = context.getId(normal);
        this.coldWetland = context.getId(cold);
        this.frozenWetland = context.getId(frozen);
    }
    
    @Override
    public int priority() {
        return 0;
    }
    
    @Override
    public boolean test(final int biome, final Cell cell) {
        if (cell.biome == BiomeType.TAIGA) {
            return biome == this.wetland || biome == this.frozenWetland;
        }
        return cell.biome == BiomeType.TUNDRA && biome == this.coldWetland;
    }
    
    @Override
    public int modify(final int in, final Cell cell, final int x, final int z) {
        if (cell.biome == BiomeType.TAIGA) {
            return this.coldWetland;
        }
        return this.frozenWetland;
    }
}

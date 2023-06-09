// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.biome.map.BiomeMap;

public class CoastModifier implements BiomeModifier
{
    private final float seaLevel;
    private final BiomeMap<?> biomeMap;
    
    public CoastModifier(final GeneratorContext context, final BiomeMap<?> biomeMap) {
        this.seaLevel = context.levels.water;
        this.biomeMap = biomeMap;
    }
    
    @Override
    public int priority() {
        return 10;
    }
    
    @Override
    public boolean test(final int biome, final Cell cell) {
        return cell.terrain.isCoast() || (cell.terrain.isShallowOcean() && cell.value > this.seaLevel);
    }
    
    @Override
    public int modify(final int in, final Cell cell, final int x, final int z) {
        final int coast = this.biomeMap.getCoast(cell);
        if (BiomeMap.isValid(coast)) {
            return coast;
        }
        return in;
    }
}

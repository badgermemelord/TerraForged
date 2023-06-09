// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.biome.map.BiomeMap;
import com.terraforged.engine.world.heightmap.Levels;

public class OceanModifier implements BiomeModifier
{
    private final Levels levels;
    private final float controlPoint;
    private final BiomeMap<?> biomeMap;
    
    public OceanModifier(final GeneratorContext context, final BiomeMap<?> biomeMap) {
        this.biomeMap = biomeMap;
        this.levels = context.levels;
        this.controlPoint = context.settings.world.controlPoints.beach;
    }
    
    @Override
    public int priority() {
        return 15;
    }
    
    @Override
    public boolean test(final int biome, final Cell cell) {
        return cell.terrain.isOverground() && cell.value < this.levels.water && cell.continentEdge < this.controlPoint;
    }
    
    @Override
    public int modify(final int in, final Cell cell, final int x, final int z) {
        final int ocean = this.biomeMap.getShallowOcean(cell);
        if (BiomeMap.isValid(ocean)) {
            return ocean;
        }
        return in;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.BiomeMap;

public class VolcanoModifier implements BiomeModifier
{
    private final float chance;
    private final BiomeMap biomes;
    
    public VolcanoModifier(final BiomeMap biomes, final float usage) {
        this.biomes = biomes;
        this.chance = usage;
    }
    
    @Override
    public int priority() {
        return 0;
    }
    
    @Override
    public boolean exitEarly() {
        return true;
    }
    
    @Override
    public boolean test(final int biome, final Cell cell) {
        return cell.terrain.isVolcano() && cell.terrainRegionId < this.chance;
    }
    
    @Override
    public int modify(final int in, final Cell cell, final int x, final int z) {
        final int volcano = this.biomes.getVolcano(cell);
        if (BiomeMap.isValid(volcano)) {
            return volcano;
        }
        return in;
    }
}

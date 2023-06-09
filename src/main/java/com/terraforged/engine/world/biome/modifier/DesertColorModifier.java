// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.DesertBiomes;

public class DesertColorModifier implements BiomeModifier
{
    private final DesertBiomes biomes;
    
    public DesertColorModifier(final DesertBiomes biomes) {
        this.biomes = biomes;
    }
    
    @Override
    public boolean exitEarly() {
        return true;
    }
    
    @Override
    public int priority() {
        return 5;
    }
    
    @Override
    public boolean test(final int biome, final Cell cell) {
        return this.biomes.isDesert(biome);
    }
    
    @Override
    public int modify(final int in, final Cell cell, final int x, final int z) {
        if (this.biomes.isRedDesert(in)) {
            if (cell.macroBiomeId <= 0.5f) {
                return this.biomes.getWhiteDesert(cell.biomeRegionId);
            }
        }
        else if (cell.macroBiomeId > 0.5f) {
            return this.biomes.getRedDesert(cell.biomeRegionId);
        }
        return in;
    }
}

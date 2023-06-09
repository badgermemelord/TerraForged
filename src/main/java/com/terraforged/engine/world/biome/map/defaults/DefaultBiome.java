// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.map.defaults;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.BiomeContext;

public interface DefaultBiome
{
    int getBiome(final float p0);
    
    default int getNone() {
        return Integer.MIN_VALUE;
    }
    
    default int getMedium() {
        return this.getNone();
    }
    
    default int getDefaultBiome(final Cell cell) {
        return this.getBiome(cell.temperature);
    }
    
    default int getDefaultBiome(final float temperature) {
        return this.getBiome(temperature);
    }
    
    public interface Factory<Biome>
    {
        DefaultBiome create(final BiomeContext<Biome> p0);
    }
}

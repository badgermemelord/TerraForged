// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.map.set;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.TempCategory;
import com.terraforged.engine.world.biome.map.BiomeContext;
import com.terraforged.engine.world.biome.map.BiomeMap;
import com.terraforged.engine.world.biome.map.defaults.DefaultBiome;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.Map;

public class WetlandSet extends TemperatureSet
{
    private final BiomeMap<?> fallback;
    
    public WetlandSet(final Map<TempCategory, IntList> map, final BiomeMap<?> fallback, final DefaultBiome defaultBiome, final BiomeContext<?> context) {
        super(map, defaultBiome, context);
        this.fallback = fallback;
    }
    
    @Override
    public int getBiome(final Cell cell) {
        final int biome = super.getBiome(cell);
        if (biome == Integer.MIN_VALUE) {
            return this.fallback.getLand(cell);
        }
        return biome;
    }
}

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
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Map;

public class RiverSet extends TemperatureSet
{
    private final BiomeMap<?> biomes;
    private final IntSet overrides;
    
    public RiverSet(final Map<TempCategory, IntList> map, final BiomeMap<?> biomes, final DefaultBiome defaultBiome, final BiomeContext<?> context) {
        super(map, defaultBiome, context);
        this.biomes = biomes;
        this.overrides = context.getRiverOverrides();
    }
    
    @Override
    public int getBiome(final Cell cell) {
        final int biome = this.biomes.getLand(cell);
        if (biome != Integer.MIN_VALUE && this.overrides.contains(biome)) {
            return biome;
        }
        return super.getBiome(cell);
    }
}

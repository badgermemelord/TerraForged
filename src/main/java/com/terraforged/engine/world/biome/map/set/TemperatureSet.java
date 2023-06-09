// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.map.set;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.TempCategory;
import com.terraforged.engine.world.biome.map.BiomeContext;
import com.terraforged.engine.world.biome.map.defaults.DefaultBiome;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.Map;
import java.util.function.BiConsumer;

public class TemperatureSet extends BiomeSet
{
    public TemperatureSet(final Map<TempCategory, IntList> map, final DefaultBiome defaultBiome, final BiomeContext<?> context) {
        super(BiomeSet.collect(map, 3, Enum::ordinal, (IntComparator)context), defaultBiome);
    }
    
    @Override
    public int getIndex(final Cell cell) {
        if (cell.temperature < 0.25f) {
            return 0;
        }
        if (cell.temperature > 0.75f) {
            return 2;
        }
        return 1;
    }
    
    @Override
    public void forEach(final BiConsumer<String, int[]> consumer) {
        for (final TempCategory temp : TempCategory.values()) {
            final int[] biomes = this.getSet(temp.ordinal());
            consumer.accept(temp.name(), biomes);
        }
    }
}

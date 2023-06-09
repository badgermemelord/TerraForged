// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.map.set;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.BiomeContext;
import com.terraforged.engine.world.biome.map.defaults.DefaultBiome;
import com.terraforged.engine.world.biome.type.BiomeType;
import com.terraforged.noise.util.NoiseUtil;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.Map;
import java.util.function.BiConsumer;

public class BiomeTypeSet extends BiomeSet
{
    public BiomeTypeSet(final Map<BiomeType, IntList> map, final DefaultBiome defaultBiome, final BiomeContext<?> context) {
        super(BiomeSet.collect(map, BiomeType.values().length, Enum::ordinal, (IntComparator)context), defaultBiome);
    }
    
    public int getBiome(final BiomeType type, final float temperature, final float identity) {
        final int[] set = this.getSet(type.ordinal());
        if (set.length == 0) {
            return this.defaultBiome.getDefaultBiome(temperature);
        }
        final int maxIndex = set.length - 1;
        final int index = NoiseUtil.round(maxIndex * identity);
        if (index < 0 || index >= set.length) {
            return this.defaultBiome.getDefaultBiome(temperature);
        }
        return set[index];
    }
    
    @Override
    public int getIndex(final Cell cell) {
        return cell.biome.ordinal();
    }
    
    @Override
    public void forEach(final BiConsumer<String, int[]> consumer) {
        for (final BiomeType type : BiomeType.values()) {
            final int[] biomes = this.getSet(type.ordinal());
            consumer.accept(type.name(), biomes);
        }
    }
}

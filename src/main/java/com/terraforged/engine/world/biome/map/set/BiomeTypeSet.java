//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.map.set;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.BiomeContext;
import com.terraforged.engine.world.biome.map.defaults.DefaultBiome;
import com.terraforged.engine.world.biome.type.BiomeType;
import com.terraforged.noise.util.NoiseUtil;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Map;
import java.util.function.BiConsumer;

public class BiomeTypeSet extends BiomeSet {
    public BiomeTypeSet(Map<BiomeType, IntList> map, DefaultBiome defaultBiome, BiomeContext<?> context) {
        super(BiomeSet.collect(map, BiomeType.values().length, Enum::ordinal, context), defaultBiome);
    }

    public int getBiome(BiomeType type, float temperature, float identity) {
        int[] set = this.getSet(type.ordinal());
        if (set.length == 0) {
            return this.defaultBiome.getDefaultBiome(temperature);
        } else {
            int maxIndex = set.length - 1;
            int index = NoiseUtil.round((float)maxIndex * identity);
            return index >= 0 && index < set.length ? set[index] : this.defaultBiome.getDefaultBiome(temperature);
        }
    }

    public int getIndex(Cell cell) {
        return cell.biome.ordinal();
    }

    public void forEach(BiConsumer<String, int[]> consumer) {
        for(BiomeType type : BiomeType.values()) {
            int[] biomes = this.getSet(type.ordinal());
            consumer.accept(type.name(), biomes);
        }
    }
}

//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.map.set;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.TempCategory;
import com.terraforged.engine.world.biome.map.BiomeContext;
import com.terraforged.engine.world.biome.map.defaults.DefaultBiome;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Map;
import java.util.function.BiConsumer;

public class TemperatureSet extends BiomeSet {
    public TemperatureSet(Map<TempCategory, IntList> map, DefaultBiome defaultBiome, BiomeContext<?> context) {
        super(BiomeSet.collect(map, 3, Enum::ordinal, context), defaultBiome);
    }

    public int getIndex(Cell cell) {
        if (cell.temperature < 0.25F) {
            return 0;
        } else {
            return cell.temperature > 0.75F ? 2 : 1;
        }
    }

    public void forEach(BiConsumer<String, int[]> consumer) {
        for(TempCategory temp : TempCategory.values()) {
            int[] biomes = this.getSet(temp.ordinal());
            consumer.accept(temp.name(), biomes);
        }
    }
}

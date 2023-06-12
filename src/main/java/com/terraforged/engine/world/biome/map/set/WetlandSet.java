//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.map.set;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.TempCategory;
import com.terraforged.engine.world.biome.map.BiomeContext;
import com.terraforged.engine.world.biome.map.BiomeMap;
import com.terraforged.engine.world.biome.map.defaults.DefaultBiome;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Map;

public class WetlandSet extends TemperatureSet {
    private final BiomeMap<?> fallback;

    public WetlandSet(Map<TempCategory, IntList> map, BiomeMap<?> fallback, DefaultBiome defaultBiome, BiomeContext<?> context) {
        super(map, defaultBiome, context);
        this.fallback = fallback;
    }

    public int getBiome(Cell cell) {
        int biome = super.getBiome(cell);
        return biome == Integer.MIN_VALUE ? this.fallback.getLand(cell) : biome;
    }
}

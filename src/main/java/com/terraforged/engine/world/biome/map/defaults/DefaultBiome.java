//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.map.defaults;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.BiomeContext;

public interface DefaultBiome {
    int getBiome(float var1);

    default int getNone() {
        return Integer.MIN_VALUE;
    }

    default int getMedium() {
        return this.getNone();
    }

    default int getDefaultBiome(Cell cell) {
        return this.getBiome(cell.temperature);
    }

    default int getDefaultBiome(float temperature) {
        return this.getBiome(temperature);
    }

    public interface Factory<Biome> {
        DefaultBiome create(BiomeContext<Biome> var1);
    }
}

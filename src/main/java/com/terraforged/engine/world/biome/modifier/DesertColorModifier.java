//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.DesertBiomes;

public class DesertColorModifier implements BiomeModifier {
    private final DesertBiomes biomes;

    public DesertColorModifier(DesertBiomes biomes) {
        this.biomes = biomes;
    }

    public boolean exitEarly() {
        return true;
    }

    public int priority() {
        return 5;
    }

    public boolean test(int biome, Cell cell) {
        return this.biomes.isDesert(biome);
    }

    public int modify(int in, Cell cell, int x, int z) {
        if (this.biomes.isRedDesert(in)) {
            if (cell.macroBiomeId <= 0.5F) {
                return this.biomes.getWhiteDesert(cell.biomeRegionId);
            }
        } else if (cell.macroBiomeId > 0.5F) {
            return this.biomes.getRedDesert(cell.biomeRegionId);
        }

        return in;
    }
}

//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.BiomeMap;

public class VolcanoModifier implements BiomeModifier {
    private final float chance;
    private final BiomeMap biomes;

    public VolcanoModifier(BiomeMap biomes, float usage) {
        this.biomes = biomes;
        this.chance = usage;
    }

    public int priority() {
        return 0;
    }

    public boolean exitEarly() {
        return true;
    }

    public boolean test(int biome, Cell cell) {
        return cell.terrain.isVolcano() && cell.terrainRegionId < this.chance;
    }

    public int modify(int in, Cell cell, int x, int z) {
        int volcano = this.biomes.getVolcano(cell);
        return BiomeMap.isValid(volcano) ? volcano : in;
    }
}

//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.BiomeMap;
import com.terraforged.engine.world.biome.type.BiomeType;
import com.terraforged.engine.world.terrain.TerrainCategory;

public class DesertWetlandModifier implements BiomeModifier {
    private final BiomeMap<?> biomes;

    public DesertWetlandModifier(BiomeMap<?> biomes) {
        this.biomes = biomes;
    }

    public int priority() {
        return 6;
    }

    public boolean exitEarly() {
        return true;
    }

    public boolean test(int biome, Cell cell) {
        return cell.terrain.getDelegate() == TerrainCategory.WETLAND && cell.biome == BiomeType.DESERT;
    }

    public int modify(int in, Cell cell, int x, int z) {
        return this.biomes.getLandSet().getBiome(getBiomeType(cell), cell.temperature, cell.biomeRegionId);
    }

    private static BiomeType getBiomeType(Cell cell) {
        return cell.biomeRegionId < 0.5F ? BiomeType.SAVANNA : BiomeType.STEPPE;
    }
}

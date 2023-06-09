// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.BiomeMap;
import com.terraforged.engine.world.biome.type.BiomeType;
import com.terraforged.engine.world.terrain.TerrainCategory;

public class DesertWetlandModifier implements BiomeModifier
{
    private final BiomeMap<?> biomes;
    
    public DesertWetlandModifier(final BiomeMap<?> biomes) {
        this.biomes = biomes;
    }
    
    @Override
    public int priority() {
        return 6;
    }
    
    @Override
    public boolean exitEarly() {
        return true;
    }
    
    @Override
    public boolean test(final int biome, final Cell cell) {
        return cell.terrain.getDelegate() == TerrainCategory.WETLAND && cell.biome == BiomeType.DESERT;
    }
    
    @Override
    public int modify(final int in, final Cell cell, final int x, final int z) {
        return this.biomes.getLandSet().getBiome(getBiomeType(cell), cell.temperature, cell.biomeRegionId);
    }
    
    private static BiomeType getBiomeType(final Cell cell) {
        return (cell.biomeRegionId < 0.5f) ? BiomeType.SAVANNA : BiomeType.STEPPE;
    }
}

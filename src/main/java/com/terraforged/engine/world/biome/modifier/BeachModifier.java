// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.biome.map.BiomeMap;
import com.terraforged.engine.world.biome.type.BiomeType;
import com.terraforged.engine.world.terrain.TerrainCategory;
import com.terraforged.noise.Source;

public class BeachModifier implements BiomeModifier
{
    private final float height;
    private final Module noise;
    private final BiomeMap biomes;
    private final int mushroomFields;
    private final int mushroomFieldShore;
    
    public BeachModifier(final BiomeMap biomeMap, final GeneratorContext context, final int mushroomFields, final int mushroomFieldShore) {
        this.biomes = biomeMap;
        this.height = context.levels.water(5);
        this.noise = Source.build(context.seed.next(), 20, 1).perlin2().scale(context.levels.scale(5));
        this.mushroomFields = mushroomFields;
        this.mushroomFieldShore = mushroomFieldShore;
    }
    
    @Override
    public int priority() {
        return 9;
    }
    
    @Override
    public boolean test(final int biome, final Cell cell) {
        return cell.terrain.getDelegate() == TerrainCategory.BEACH && cell.biome != BiomeType.DESERT;
    }
    
    @Override
    public int modify(final int in, final Cell cell, final int x, final int z) {
        if (cell.value + this.noise.getValue((float)x, (float)z) >= this.height) {
            return in;
        }
        if (in == this.mushroomFields) {
            return this.mushroomFieldShore;
        }
        return this.biomes.getBeach(cell);
    }
}

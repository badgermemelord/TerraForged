// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.biome.map.BiomeMap;
import com.terraforged.noise.Source;

public class MountainModifier implements BiomeModifier
{
    public static final float MOUNTAIN_CHANCE = 0.4f;
    private static final int MOUNTAIN_START_HEIGHT = 48;
    private final float chance;
    private final float height;
    private final float range;
    private final Module noise;
    private final BiomeMap biomes;
    
    public MountainModifier(final GeneratorContext context, final BiomeMap biomes, final float usage) {
        this.biomes = biomes;
        this.chance = usage;
        this.range = context.levels.scale(10);
        this.height = context.levels.ground(48);
        this.noise = Source.perlin(context.seed.next(), 80, 2).scale(this.range);
    }
    
    @Override
    public int priority() {
        return 0;
    }
    
    @Override
    public boolean exitEarly() {
        return true;
    }
    
    @Override
    public boolean test(final int biome, final Cell cell) {
        return cell.terrain.isMountain() && cell.macroBiomeId < this.chance;
    }
    
    @Override
    public int modify(final int in, final Cell cell, final int x, final int z) {
        if (this.canModify(cell, x, z)) {
            final int mountain = this.biomes.getMountain(cell);
            if (BiomeMap.isValid(mountain)) {
                return mountain;
            }
        }
        return in;
    }
    
    private boolean canModify(final Cell cell, final int x, final int z) {
        return cell.value > this.height || (cell.value + this.range >= this.height && cell.value + this.noise.getValue((float)x, (float)z) > this.height);
    }
}

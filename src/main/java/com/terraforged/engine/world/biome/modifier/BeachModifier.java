//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.biome.map.BiomeMap;
import com.terraforged.engine.world.biome.type.BiomeType;
import com.terraforged.engine.world.terrain.TerrainCategory;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;

public class BeachModifier implements BiomeModifier {
    private final float height;
    private final Module noise;
    private final BiomeMap biomes;
    private final int mushroomFields;
    private final int mushroomFieldShore;

    public BeachModifier(BiomeMap biomeMap, GeneratorContext context, int mushroomFields, int mushroomFieldShore) {
        this.biomes = biomeMap;
        this.height = context.levels.water(5);
        this.noise = Source.build(context.seed.next(), 20, 1).perlin2().scale((double)context.levels.scale(5));
        this.mushroomFields = mushroomFields;
        this.mushroomFieldShore = mushroomFieldShore;
    }

    public int priority() {
        return 9;
    }

    public boolean test(int biome, Cell cell) {
        return cell.terrain.getDelegate() == TerrainCategory.BEACH && cell.biome != BiomeType.DESERT;
    }

    public int modify(int in, Cell cell, int x, int z) {
        if (cell.value + this.noise.getValue((float)x, (float)z) < this.height) {
            return in == this.mushroomFields ? this.mushroomFieldShore : this.biomes.getBeach(cell);
        } else {
            return in;
        }
    }
}

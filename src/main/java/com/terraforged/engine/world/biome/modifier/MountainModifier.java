//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.biome.map.BiomeMap;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;

public class MountainModifier implements BiomeModifier {
    public static final float MOUNTAIN_CHANCE = 0.4F;
    private static final int MOUNTAIN_START_HEIGHT = 48;
    private final float chance;
    private final float height;
    private final float range;
    private final Module noise;
    private final BiomeMap biomes;

    public MountainModifier(GeneratorContext context, BiomeMap biomes, float usage) {
        this.biomes = biomes;
        this.chance = usage;
        this.range = context.levels.scale(10);
        this.height = context.levels.ground(48);
        this.noise = Source.perlin(context.seed.next(), 80, 2).scale((double)this.range);
    }

    public int priority() {
        return 0;
    }

    public boolean exitEarly() {
        return true;
    }

    public boolean test(int biome, Cell cell) {
        return cell.terrain.isMountain() && cell.macroBiomeId < this.chance;
    }

    public int modify(int in, Cell cell, int x, int z) {
        if (this.canModify(cell, x, z)) {
            int mountain = this.biomes.getMountain(cell);
            if (BiomeMap.isValid(mountain)) {
                return mountain;
            }
        }

        return in;
    }

    private boolean canModify(Cell cell, int x, int z) {
        if (cell.value > this.height) {
            return true;
        } else if (cell.value + this.range < this.height) {
            return false;
        } else {
            return cell.value + this.noise.getValue((float)x, (float)z) > this.height;
        }
    }
}

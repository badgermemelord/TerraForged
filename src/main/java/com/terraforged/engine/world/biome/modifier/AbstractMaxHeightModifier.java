//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.Seed;
import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.climate.Climate;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;

public abstract class AbstractMaxHeightModifier extends AbstractOffsetModifier {
    private final float minHeight;
    private final float maxHeight;
    private final float range;
    private final Module variance;

    public AbstractMaxHeightModifier(Seed seed, Climate climate, int scale, int octaves, float variance, float minHeight, float maxHeight) {
        super(climate);
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.range = maxHeight - minHeight;
        this.variance = Source.perlin(seed.next(), scale, octaves).scale((double)variance);
    }

    protected final int modify(int in, Cell cell, int x, int z, float ox, float oz) {
        float var = this.variance.getValue((float)x, (float)z);
        float value = cell.value + var;
        if (value < this.minHeight) {
            return in;
        } else if (value > this.maxHeight) {
            return this.getModifiedBiome(in, cell, x, z, ox, oz);
        } else {
            float alpha = (value - this.minHeight) / this.range;
            cell.biomeRegionEdge *= alpha;
            return in;
        }
    }

    protected abstract int getModifiedBiome(int var1, Cell var2, int var3, int var4, float var5, float var6);
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.Seed;
import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.climate.Climate;
import com.terraforged.noise.Source;

public abstract class AbstractMaxHeightModifier extends AbstractOffsetModifier
{
    private final float minHeight;
    private final float maxHeight;
    private final float range;
    private final Module variance;
    
    public AbstractMaxHeightModifier(final Seed seed, final Climate climate, final int scale, final int octaves, final float variance, final float minHeight, final float maxHeight) {
        super(climate);
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.range = maxHeight - minHeight;
        this.variance = Source.perlin(seed.next(), scale, octaves).scale(variance);
    }
    
    @Override
    protected final int modify(final int in, final Cell cell, final int x, final int z, final float ox, final float oz) {
        final float var = this.variance.getValue((float)x, (float)z);
        final float value = cell.value + var;
        if (value < this.minHeight) {
            return in;
        }
        if (value > this.maxHeight) {
            return this.getModifiedBiome(in, cell, x, z, ox, oz);
        }
        final float alpha = (value - this.minHeight) / this.range;
        cell.biomeRegionEdge *= alpha;
        return in;
    }
    
    protected abstract int getModifiedBiome(final int p0, final Cell p1, final int p2, final int p3, final float p4, final float p5);
}

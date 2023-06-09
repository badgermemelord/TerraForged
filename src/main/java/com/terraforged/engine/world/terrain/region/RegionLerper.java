// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain.region;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.cell.Populator;
import com.terraforged.noise.util.NoiseUtil;

public class RegionLerper implements Populator
{
    private final Populator lower;
    private final Populator upper;
    
    public RegionLerper(final Populator lower, final Populator upper) {
        this.lower = lower;
        this.upper = upper;
    }
    
    @Override
    public void apply(final Cell cell, final float x, final float y) {
        final float alpha = cell.terrainRegionEdge;
        if (alpha == 0.0f) {
            this.lower.apply(cell, x, y);
            return;
        }
        if (alpha == 1.0f) {
            this.upper.apply(cell, x, y);
            return;
        }
        this.lower.apply(cell, x, y);
        final float lowerValue = cell.value;
        this.upper.apply(cell, x, y);
        final float upperValue = cell.value;
        cell.value = NoiseUtil.lerp(lowerValue, upperValue, alpha);
    }
}

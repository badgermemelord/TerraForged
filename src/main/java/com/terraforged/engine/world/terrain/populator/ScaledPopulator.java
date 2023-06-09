// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain.populator;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.terrain.Terrain;

public class ScaledPopulator extends TerrainPopulator
{
    private final float baseScale;
    private final float varianceScale;
    
    public ScaledPopulator(final Terrain type, final Module base, final Module variance, final float baseScale, final float varianceScale, final float weight) {
        super(type, base, variance, weight);
        this.baseScale = baseScale;
        this.varianceScale = varianceScale;
    }
    
    @Override
    public void apply(final Cell cell, final float x, final float z) {
        final float base = this.base.getValue(x, z) * this.baseScale;
        final float variance = this.variance.getValue(x, z) * this.varianceScale;
        cell.value = base + variance;
        if (cell.value < 0.0f) {
            cell.value = 0.0f;
        }
        else if (cell.value > 1.0f) {
            cell.value = 1.0f;
        }
        cell.terrain = this.type;
    }
}

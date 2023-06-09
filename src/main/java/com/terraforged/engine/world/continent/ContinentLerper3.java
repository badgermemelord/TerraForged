// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.continent;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.cell.Populator;
import com.terraforged.noise.func.Interpolation;
import com.terraforged.noise.util.NoiseUtil;

public class ContinentLerper3 implements Populator
{
    private final Populator lower;
    private final Populator middle;
    private final Populator upper;
    private final Interpolation interpolation;
    private final float midpoint;
    private final float blendLower;
    private final float blendUpper;
    private final float lowerRange;
    private final float upperRange;
    
    public ContinentLerper3(final Populator lower, final Populator middle, final Populator upper, final float min, final float mid, final float max) {
        this(lower, middle, upper, min, mid, max, Interpolation.CURVE3);
    }
    
    public ContinentLerper3(final Populator lower, final Populator middle, final Populator upper, final float min, final float mid, final float max, final Interpolation interpolation) {
        this.lower = lower;
        this.upper = upper;
        this.middle = middle;
        this.interpolation = interpolation;
        this.midpoint = mid;
        this.blendLower = min;
        this.blendUpper = max;
        this.lowerRange = this.midpoint - this.blendLower;
        this.upperRange = this.blendUpper - this.midpoint;
    }
    
    @Override
    public void apply(final Cell cell, final float x, final float y) {
        final float select = cell.continentEdge;
        if (select < this.blendLower) {
            this.lower.apply(cell, x, y);
            return;
        }
        if (select > this.blendUpper) {
            this.upper.apply(cell, x, y);
            return;
        }
        if (select < this.midpoint) {
            final float alpha = this.interpolation.apply((select - this.blendLower) / this.lowerRange);
            this.lower.apply(cell, x, y);
            final float lowerVal = cell.value;
            this.middle.apply(cell, x, y);
            cell.value = NoiseUtil.lerp(lowerVal, cell.value, alpha);
        }
        else {
            final float alpha = this.interpolation.apply((select - this.midpoint) / this.upperRange);
            this.middle.apply(cell, x, y);
            final float lowerVal = cell.value;
            this.upper.apply(cell, x, y);
            cell.value = NoiseUtil.lerp(lowerVal, cell.value, alpha);
        }
    }
}

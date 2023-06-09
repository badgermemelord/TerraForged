// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.module;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.cell.Populator;
import com.terraforged.engine.world.climate.Climate;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.noise.func.Interpolation;
import com.terraforged.noise.util.NoiseUtil;

public class MultiBlender extends Select implements Populator
{
    private final Climate climate;
    private final Populator lower;
    private final Populator middle;
    private final Populator upper;
    private final float midpoint;
    private final float blendLower;
    private final float blendUpper;
    private final float lowerRange;
    private final float upperRange;
    
    public MultiBlender(final Climate climate, final Populator control, final Populator lower, final Populator middle, final Populator upper, final float min, final float mid, final float max) {
        super(control);
        this.climate = climate;
        this.lower = lower;
        this.upper = upper;
        this.middle = middle;
        this.midpoint = mid;
        this.blendLower = min;
        this.blendUpper = max;
        this.lowerRange = this.midpoint - this.blendLower;
        this.upperRange = this.blendUpper - this.midpoint;
    }
    
    @Override
    public void apply(final Cell cell, final float x, final float y) {
        final float select = this.getSelect(cell, x, y);
        if (select < this.blendLower) {
            this.lower.apply(cell, x, y);
            return;
        }
        if (select > this.blendUpper) {
            this.upper.apply(cell, x, y);
            return;
        }
        if (select < this.midpoint) {
            final float alpha = Interpolation.CURVE3.apply((select - this.blendLower) / this.lowerRange);
            this.lower.apply(cell, x, y);
            final float lowerVal = cell.value;
            final Terrain lowerType = cell.terrain;
            this.middle.apply(cell, x, y);
            final float upperVal = cell.value;
            cell.value = NoiseUtil.lerp(lowerVal, upperVal, alpha);
        }
        else {
            final float alpha = Interpolation.CURVE3.apply((select - this.midpoint) / this.upperRange);
            this.middle.apply(cell, x, y);
            final float lowerVal = cell.value;
            this.upper.apply(cell, x, y);
            cell.value = NoiseUtil.lerp(lowerVal, cell.value, alpha);
        }
    }
}

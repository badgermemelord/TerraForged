// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.module;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.cell.Populator;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.noise.func.Interpolation;
import com.terraforged.noise.util.NoiseUtil;

public class Blender extends Select implements Populator
{
    private final Populator lower;
    private final Populator upper;
    private final float blendLower;
    private final float blendUpper;
    private final float blendRange;
    private final float midpoint;
    private final float tagThreshold;
    
    public Blender(final Module control, final Populator lower, final Populator upper, final float min, final float max, final float split) {
        super(control);
        this.lower = lower;
        this.upper = upper;
        this.blendLower = min;
        this.blendUpper = max;
        this.blendRange = this.blendUpper - this.blendLower;
        this.midpoint = this.blendLower + this.blendRange * split;
        this.tagThreshold = this.midpoint;
    }
    
    public Blender(final Populator control, final Populator lower, final Populator upper, final float min, final float max, final float split, final float tagThreshold) {
        super(control);
        this.lower = lower;
        this.upper = upper;
        this.blendLower = min;
        this.blendUpper = max;
        this.blendRange = this.blendUpper - this.blendLower;
        this.midpoint = this.blendLower + this.blendRange * split;
        this.tagThreshold = tagThreshold;
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
        final float alpha = Interpolation.LINEAR.apply((select - this.blendLower) / this.blendRange);
        this.lower.apply(cell, x, y);
        final float lowerVal = cell.value;
        final Terrain lowerType = cell.terrain;
        this.upper.apply(cell, x, y);
        final float upperVal = cell.value;
        cell.value = NoiseUtil.lerp(lowerVal, upperVal, alpha);
        if (select < this.midpoint) {
            cell.terrain = lowerType;
        }
    }
}

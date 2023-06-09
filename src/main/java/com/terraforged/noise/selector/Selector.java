// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.selector;

import com.terraforged.noise.Module;
import com.terraforged.noise.combiner.Combiner;
import com.terraforged.noise.func.Interpolation;
import com.terraforged.noise.util.NoiseUtil;

public abstract class Selector extends Combiner
{
    protected final Module selector;
    protected final Interpolation interpolation;
    
    public Selector(final Module control, final Module[] sources, final Interpolation interpolation) {
        super(sources);
        this.selector = control;
        this.interpolation = interpolation;
    }
    
    @Override
    public float getValue(final float x, final float y) {
        final float select = this.selector.getValue(x, y);
        return this.selectValue(x, y, select);
    }
    
    @Override
    protected float minTotal(final float result, final Module next) {
        return Math.min(result, next.minValue());
    }
    
    @Override
    protected float maxTotal(final float result, final Module next) {
        return Math.max(result, next.maxValue());
    }
    
    @Override
    protected float combine(final float result, final float value) {
        return 0.0f;
    }
    
    protected float blendValues(final float lower, final float upper, float alpha) {
        alpha = this.interpolation.apply(alpha);
        return NoiseUtil.lerp(lower, upper, alpha);
    }
    
    protected abstract float selectValue(final float p0, final float p1, final float p2);
}

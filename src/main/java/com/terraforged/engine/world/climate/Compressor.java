// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.climate;

import com.terraforged.noise.Module;

public class Compressor implements Module
{
    private final float lowerStart;
    private final float lowerEnd;
    private final float lowerRange;
    private final float lowerExpandRange;
    private final float upperStart;
    private final float upperEnd;
    private final float upperRange;
    private final float upperExpandedRange;
    private final float compression;
    private final float compressionRange;
    private final Module module;
    
    public Compressor(final Module module, final float inset, final float amount) {
        this(module, inset, inset + amount, 1.0f - inset - amount, 1.0f - inset);
    }
    
    public Compressor(final Module module, final float lowerStart, final float lowerEnd, final float upperStart, final float upperEnd) {
        this.module = module;
        this.lowerStart = lowerStart;
        this.lowerEnd = lowerEnd;
        this.lowerRange = lowerStart;
        this.lowerExpandRange = lowerEnd;
        this.upperStart = upperStart;
        this.upperEnd = upperEnd;
        this.upperRange = 1.0f - upperEnd;
        this.upperExpandedRange = 1.0f - upperStart;
        this.compression = upperStart - lowerEnd;
        this.compressionRange = upperEnd - lowerStart;
    }
    
    @Override
    public float getValue(final float x, final float y) {
        final float value = this.module.getValue(x, y);
        if (value <= this.lowerStart) {
            final float alpha = value / this.lowerRange;
            return alpha * this.lowerExpandRange;
        }
        if (value >= this.upperEnd) {
            final float delta = value - this.upperEnd;
            final float alpha2 = delta / this.upperRange;
            return this.upperStart + alpha2 * this.upperExpandedRange;
        }
        final float delta = value - this.lowerStart;
        final float alpha2 = delta / this.compressionRange;
        return this.lowerEnd + alpha2 * this.compression;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util.fastpoisson;

import com.terraforged.noise.util.NoiseUtil;

public class FastPoissonContext
{
    public final int radius;
    public final int radius2;
    public final float jitter;
    public final float pad;
    public final float frequency;
    public final float scale;
    public final Module density;
    
    public FastPoissonContext(final int radius, final float jitter, final float frequency, final Module density) {
        this.radius = radius;
        this.density = density;
        this.frequency = Math.min(0.5f, frequency);
        this.scale = 1.0f / this.frequency;
        this.jitter = NoiseUtil.clamp(jitter, 0.0f, 1.0f);
        this.pad = (1.0f - this.jitter) * 0.5f;
        this.radius2 = radius * radius;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.climate;

import com.terraforged.noise.Source;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.Module;


public class Moisture implements Module
{
    private final Module source;
    private final int power;
    
    public Moisture(final int seed, final int scale, final int power) {
        this(Source.simplex(seed, scale, 1).clamp(0.125, 0.875).map(0.0, 1.0), power);
    }
    
    public Moisture(final Module source, final int power) {
        this.source = source.freq(0.5, 1.0);
        this.power = power;
    }
    
    @Override
    public float getValue(final float x, final float y) {
        float noise = this.source.getValue(x, y);
        if (this.power < 2) {
            return noise;
        }
        noise = (noise - 0.5f) * 2.0f;
        float value = NoiseUtil.pow(noise, this.power);
        value = NoiseUtil.copySign(value, noise);
        return NoiseUtil.map(value, -1.0f, 1.0f, 2.0f);
    }
}

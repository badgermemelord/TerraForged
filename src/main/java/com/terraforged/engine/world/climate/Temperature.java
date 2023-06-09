// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.climate;

import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.Module;


public class Temperature implements Module
{
    private final int power;
    private final float frequency;
    
    public Temperature(final float frequency, final int power) {
        this.frequency = frequency;
        this.power = power;
    }
    
    @Override
    public float getValue(final float x, float y) {
        y *= this.frequency;
        float sin = NoiseUtil.sin(y);
        sin = NoiseUtil.clamp(sin, -1.0f, 1.0f);
        float value = NoiseUtil.pow(sin, this.power);
        value = NoiseUtil.copySign(value, sin);
        return NoiseUtil.map(value, -1.0f, 1.0f, 2.0f);
    }
}

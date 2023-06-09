// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise;

public interface Noise
{
    float getValue(final float p0, final float p1);
    
    default float maxValue() {
        return 1.0f;
    }
    
    default float minValue() {
        return 0.0f;
    }
}

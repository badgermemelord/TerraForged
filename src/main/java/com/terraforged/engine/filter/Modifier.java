// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.filter;

import com.terraforged.engine.cell.Cell;
import com.terraforged.noise.util.NoiseUtil;

public interface Modifier
{
    float getValueModifier(final float p0);
    
    default float modify(final Cell cell, final float value) {
        float strengthModifier = 1.0f;
        final float erosionModifier = cell.terrain.erosionModifier();
        if (erosionModifier != 1.0f) {
            final float alpha = NoiseUtil.map(cell.terrainRegionEdge, 0.0f, 0.15f, 0.15f);
            strengthModifier = NoiseUtil.lerp(1.0f, erosionModifier, alpha);
        }
        if (cell.riverMask < 0.1f) {
            strengthModifier *= NoiseUtil.map(cell.riverMask, 0.002f, 0.1f, 0.098f);
        }
        return this.getValueModifier(cell.value) * strengthModifier * value;
    }
    
    default Modifier invert() {
        return v -> 1.0f - this.getValueModifier(v);
    }
    
    default Modifier range(final float minValue, final float maxValue) {
        return new Modifier() {
            private final float min = minValue;
            private final float max = maxValue;
            private final float range = maxValue - minValue;
            
            @Override
            public float getValueModifier(final float value) {
                if (value > this.max) {
                    return 1.0f;
                }
                if (value < this.min) {
                    return 0.0f;
                }
                return (value - this.min) / this.range;
            }
        };
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.func;

import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

public enum CellFunc
{
    CELL_VALUE {
        @Override
        public float apply(final int xc, final int yc, final float distance, final int seed, final Vec2f vec2f, final Module lookup) {
            return NoiseUtil.valCoord2D(seed, xc, yc);
        }
    }, 
    NOISE_LOOKUP {
        @Override
        public float apply(final int xc, final int yc, final float distance, final int seed, final Vec2f vec2f, final Module lookup) {
            return lookup.getValue(xc + vec2f.x, yc + vec2f.y);
        }
        
        @Override
        public float mapValue(final float value, final float min, final float max, final float range) {
            return value;
        }
    }, 
    DISTANCE {
        @Override
        public float apply(final int xc, final int yc, final float distance, final int seed, final Vec2f vec2f, final Module lookup) {
            return distance - 1.0f;
        }
        
        @Override
        public float mapValue(final float value, final float min, final float max, final float range) {
            return 0.0f;
        }
    };
    
    public abstract float apply(final int p0, final int p1, final float p2, final int p3, final Vec2f p4, final Module p5);
    
    public float mapValue(final float value, final float min, final float max, final float range) {
        return NoiseUtil.map(value, min, max, range);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.func;

public enum DistanceFunc
{
    EUCLIDEAN {
        @Override
        public float apply(final float vecX, final float vecY) {
            return vecX * vecX + vecY * vecY;
        }
    }, 
    MANHATTAN {
        @Override
        public float apply(final float vecX, final float vecY) {
            return Math.abs(vecX) + Math.abs(vecY);
        }
    }, 
    NATURAL {
        @Override
        public float apply(final float vecX, final float vecY) {
            return Math.abs(vecX) + Math.abs(vecY) + (vecX * vecX + vecY * vecY);
        }
    };
    
    public abstract float apply(final float p0, final float p1);
}

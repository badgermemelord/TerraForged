// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.domain;

import com.terraforged.cereal.spec.SpecName;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;

public interface Domain extends SpecName
{
    public static final Domain DIRECT = new Domain() {
        @Override
        public String getSpecName() {
            return "Direct";
        }
        
        @Override
        public float getOffsetX(final float x, final float y) {
            return 0.0f;
        }
        
        @Override
        public float getOffsetY(final float x, final float y) {
            return 0.0f;
        }
    };
    
    float getOffsetX(final float p0, final float p1);
    
    float getOffsetY(final float p0, final float p1);
    
    default float getX(final float x, final float y) {
        return x + this.getOffsetX(x, y);
    }
    
    default float getY(final float x, final float y) {
        return y + this.getOffsetY(x, y);
    }
    
    default Domain cache() {
        return new CacheWarp(this);
    }
    
    default Domain add(final Domain next) {
        return new AddWarp(this, next);
    }
    
    default Domain warp(final Domain next) {
        return new CompoundWarp(this, next);
    }
    
    default Domain then(final Domain next) {
        return new CumulativeWarp(this, next);
    }
    
    default Domain warp(final Module x, final Module y, final Module distance) {
        return new DomainWarp(x, y, distance);
    }
    
    default Domain warp(final int seed, final int scale, final int octaves, final double strength) {
        return warp(Source.PERLIN, seed, scale, octaves, strength);
    }
    
    default Domain warp(final Source type, final int seed, final int scale, final int octaves, final double strength) {
        return warp(Source.build(seed, scale, octaves).build(type), Source.build(seed + 1, scale, octaves).build(type), Source.constant(strength));
    }
    
    default Domain direction(final Module direction, final Module distance) {
        return new DirectionWarp(direction, distance);
    }
    
    default Domain direction(final int seed, final int scale, final int octaves, final double strength) {
        return direction(Source.PERLIN, seed, scale, octaves, strength);
    }
    
    default Domain direction(final Source type, final int seed, final int scale, final int octaves, final double strength) {
        return direction(Source.build(seed, scale, octaves).build(type), Source.constant(strength));
    }
}

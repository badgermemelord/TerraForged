// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util;

public class Boundsf
{
    public static final Boundsf NONE;
    public final float minX;
    public final float minY;
    public final float maxX;
    public final float maxY;
    
    public Boundsf(final float minX, final float minY, final float maxX, final float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }
    
    public boolean contains(final float x, final float y) {
        return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    static {
        NONE = new Boundsf(1.0f, 1.0f, -1.0f, -1.0f);
    }
    
    public static class Builder
    {
        private float minX;
        private float minY;
        private float maxX;
        private float maxY;
        
        public Builder() {
            this.minX = Float.MAX_VALUE;
            this.minY = Float.MAX_VALUE;
            this.maxX = Float.MIN_VALUE;
            this.maxY = Float.MIN_VALUE;
        }
        
        public void record(final float x, final float y) {
            this.minX = Math.min(this.minX, x);
            this.minY = Math.min(this.minY, y);
            this.maxX = Math.max(this.maxX, x);
            this.maxY = Math.max(this.maxY, y);
        }
        
        public Boundsf build() {
            return new Boundsf(this.minX, this.minY, this.maxX, this.maxY);
        }
    }
}

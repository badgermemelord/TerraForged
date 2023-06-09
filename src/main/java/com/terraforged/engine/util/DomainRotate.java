// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util;

import com.terraforged.noise.domain.Domain;

public class DomainRotate implements Domain
{
    protected final float angle;
    protected final float cos;
    protected final float sin;
    
    public DomainRotate(final float angle) {
        this.angle = angle;
        this.cos = (float)Math.cos(Math.toRadians(angle));
        this.sin = (float)Math.sin(Math.toRadians(angle));
    }
    
    @Override
    public String getSpecName() {
        return "DomainRotate";
    }
    
    @Override
    public float getX(final float x, final float y) {
        return this.getOffsetX(x, y);
    }
    
    @Override
    public float getY(final float x, final float y) {
        return this.getOffsetY(x, y);
    }
    
    @Override
    public float getOffsetX(final float x, final float y) {
        return x * this.cos - y * this.sin;
    }
    
    @Override
    public float getOffsetY(final float x, final float y) {
        return x * this.sin + y * this.cos;
    }
}

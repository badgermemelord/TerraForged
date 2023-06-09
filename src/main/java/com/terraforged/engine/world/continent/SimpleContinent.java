// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.continent;

public interface SimpleContinent extends Continent
{
    float getEdgeValue(final float p0, final float p1);
    
    default float getDistanceToEdge(final int cx, final int cz, final float dx, final float dy) {
        return 1.0f;
    }
    
    default float getDistanceToOcean(final int cx, final int cz, final float dx, final float dy) {
        return 1.0f;
    }
}

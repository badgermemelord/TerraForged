// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain;

public class CompositeTerrain extends Terrain
{
    private final boolean flat;
    private final float erosion;
    
    CompositeTerrain(final int id, final Terrain a, final Terrain b) {
        super(id, a.getName() + "-" + b.getName(), getDominant(a, b));
        this.flat = (a.isFlat() && b.isFlat());
        this.erosion = Math.min(a.erosionModifier(), b.erosionModifier());
    }
    
    @Override
    public float erosionModifier() {
        return this.erosion;
    }
    
    @Override
    public boolean isFlat() {
        return this.flat;
    }
    
    private static Terrain getDominant(final Terrain a, final Terrain b) {
        final TerrainCategory typeA = a.getCategory();
        final TerrainCategory typeB = a.getCategory();
        final TerrainCategory dom = typeA.getDominant(typeB);
        if (dom == typeA) {
            return a;
        }
        return b;
    }
}

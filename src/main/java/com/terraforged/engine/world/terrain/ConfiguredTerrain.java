// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain;

public class ConfiguredTerrain extends Terrain
{
    private final float erosionModifier;
    private final boolean isMountain;
    private final boolean overridesRiver;
    
    ConfiguredTerrain(final int id, final String name, final TerrainCategory category, final float erosionModifier) {
        this(id, name, category, erosionModifier, category.isMountain(), category.overridesRiver());
    }
    
    ConfiguredTerrain(final int id, final String name, final TerrainCategory category, final boolean overridesRiver) {
        this(id, name, category, category.erosionModifier(), category.isMountain(), overridesRiver);
    }
    
    ConfiguredTerrain(final int id, final String name, final TerrainCategory category, final boolean isMountain, final boolean overridesRiver) {
        this(id, name, category, category.erosionModifier(), isMountain, overridesRiver);
    }
    
    ConfiguredTerrain(final int id, final String name, final TerrainCategory category, final float erosionModifier, final boolean isMountain, final boolean overridesRiver) {
        super(id, name, category);
        this.erosionModifier = erosionModifier;
        this.isMountain = isMountain;
        this.overridesRiver = overridesRiver;
    }
    
    @Override
    public boolean overridesRiver() {
        return this.overridesRiver;
    }
    
    @Override
    public boolean isMountain() {
        return this.isMountain;
    }
    
    @Override
    public float erosionModifier() {
        return this.erosionModifier;
    }
}

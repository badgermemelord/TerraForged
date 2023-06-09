// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain;

import com.terraforged.noise.util.NoiseUtil;

public class Terrain implements ITerrain.Delegate
{
    private final int id;
    private final String name;
    private final TerrainCategory type;
    private final ITerrain delegate;
    
    Terrain(final int id, final String name, final Terrain terrain) {
        this(id, name, terrain.getCategory(), terrain);
    }
    
    Terrain(final int id, final String name, final TerrainCategory type) {
        this(id, name, type, type);
    }
    
    Terrain(final int id, final String name, final TerrainCategory type, final ITerrain delegate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.delegate = delegate;
    }
    
    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public TerrainCategory getCategory() {
        return this.type;
    }
    
    public float getRenderHue() {
        return NoiseUtil.valCoord2D(this.name.hashCode(), 0, 0);
    }
    
    @Override
    public ITerrain getDelegate() {
        return this.delegate;
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    public Terrain withId(final int id) {
        final ITerrain delegate = (this.delegate instanceof Terrain) ? this.delegate : this;
        return new Terrain(id, this.name, this.type, delegate);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain;

public class TerrainHelper
{
    public static Terrain getOrCreate(final String name, final Terrain parent) {
        if (parent == null || parent == TerrainType.NONE) {
            return TerrainType.NONE;
        }
        final Terrain current = TerrainType.get(name);
        if (current != null) {
            return current;
        }
        return TerrainType.register(new Terrain(0, name, parent));
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.map.defaults;

public class FallbackBiomes<T>
{
    public final T river;
    public final T lake;
    public final T beach;
    public final T ocean;
    public final T deepOcean;
    public final T wetland;
    public final T land;
    
    public FallbackBiomes(final T river, final T lake, final T beach, final T ocean, final T deepOcean, final T wetland, final T land) {
        this.river = river;
        this.lake = lake;
        this.beach = beach;
        this.ocean = ocean;
        this.deepOcean = deepOcean;
        this.wetland = wetland;
        this.land = land;
    }
}

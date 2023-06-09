// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.heightmap;

public class RegionConfig
{
    public final int seed;
    public final int scale;
    public final Module warpX;
    public final Module warpZ;
    public final double warpStrength;
    
    public RegionConfig(final int seed, final int scale, final Module warpX, final Module warpZ, final double warpStrength) {
        this.seed = seed;
        this.scale = scale;
        this.warpX = warpX;
        this.warpZ = warpZ;
        this.warpStrength = warpStrength;
    }
}

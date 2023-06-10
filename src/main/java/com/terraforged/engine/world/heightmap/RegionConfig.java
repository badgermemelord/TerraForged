//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.engine.world.heightmap;

import com.terraforged.noise.Module;

public class RegionConfig {
    public final int seed;
    public final int scale;
    public final Module warpX;
    public final Module warpZ;
    public final double warpStrength;

    public RegionConfig(int seed, int scale, Module warpX, Module warpZ, double warpStrength) {
        this.seed = seed;
        this.scale = scale;
        this.warpX = warpX;
        this.warpZ = warpZ;
        this.warpStrength = warpStrength;
    }
}

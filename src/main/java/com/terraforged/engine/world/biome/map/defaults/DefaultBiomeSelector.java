//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.map.defaults;

public class DefaultBiomeSelector implements DefaultBiome {
    protected final float lower;
    protected final float upper;
    protected final int cold;
    protected final int medium;
    protected final int warm;

    public DefaultBiomeSelector(int cold, int medium, int warm, float lower, float upper) {
        this.cold = cold;
        this.medium = medium;
        this.warm = warm;
        this.lower = lower;
        this.upper = upper;
    }

    public int getMedium() {
        return this.medium;
    }

    public int getBiome(float temperature) {
        if (temperature < this.lower) {
            return this.cold;
        } else {
            return temperature > this.upper ? this.warm : this.medium;
        }
    }
}

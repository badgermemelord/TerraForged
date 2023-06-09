// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.map.defaults;

public class DefaultBiomeSelector implements DefaultBiome
{
    protected final float lower;
    protected final float upper;
    protected final int cold;
    protected final int medium;
    protected final int warm;
    
    public DefaultBiomeSelector(final int cold, final int medium, final int warm, final float lower, final float upper) {
        this.cold = cold;
        this.medium = medium;
        this.warm = warm;
        this.lower = lower;
        this.upper = upper;
    }
    
    @Override
    public int getMedium() {
        return this.medium;
    }
    
    @Override
    public int getBiome(final float temperature) {
        if (temperature < this.lower) {
            return this.cold;
        }
        if (temperature > this.upper) {
            return this.warm;
        }
        return this.medium;
    }
}

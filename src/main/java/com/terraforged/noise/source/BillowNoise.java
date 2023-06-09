// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataSpec;

public class BillowNoise extends RidgeNoise
{
    public BillowNoise(final Builder builder) {
        super(builder);
    }
    
    @Override
    public String getSpecName() {
        return "Billow";
    }
    
    @Override
    public float getValue(final float x, final float y, final int seed) {
        return 1.0f - super.getValue(x, y, seed);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    public static DataSpec<BillowNoise> billowSpec() {
        return NoiseSource.specBuilder("Billow", BillowNoise.class, BillowNoise::new).build();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.rivermap.lake;

import com.terraforged.engine.settings.RiverSettings;
import com.terraforged.engine.world.heightmap.Levels;

public class LakeConfig
{
    public final float depth;
    public final float chance;
    public final float sizeMin;
    public final float sizeMax;
    public final float sizeRange;
    public final float bankMin;
    public final float bankMax;
    public final float distanceMin;
    public final float distanceMax;
    
    private LakeConfig(final Builder builder) {
        this.depth = builder.depth;
        this.chance = builder.chance;
        this.sizeMin = builder.sizeMin;
        this.sizeMax = builder.sizeMax;
        this.sizeRange = this.sizeMax - this.sizeMin;
        this.bankMin = builder.bankMin;
        this.bankMax = builder.bankMax;
        this.distanceMin = builder.distanceMin;
        this.distanceMax = builder.distanceMax;
    }
    
    public static LakeConfig of(final RiverSettings.Lake settings, final Levels levels) {
        final Builder builder = new Builder();
        builder.chance = settings.chance;
        builder.sizeMin = (float)settings.sizeMin;
        builder.sizeMax = (float)settings.sizeMax;
        builder.depth = levels.water(-settings.depth);
        builder.distanceMin = settings.minStartDistance;
        builder.distanceMax = settings.maxStartDistance;
        builder.bankMin = levels.water(settings.minBankHeight);
        builder.bankMax = levels.water(settings.maxBankHeight);
        return new LakeConfig(builder);
    }
    
    public static class Builder
    {
        public float chance;
        public float depth;
        public float sizeMin;
        public float sizeMax;
        public float bankMin;
        public float bankMax;
        public float distanceMin;
        public float distanceMax;
        
        public Builder() {
            this.depth = 10.0f;
            this.sizeMin = 30.0f;
            this.sizeMax = 100.0f;
            this.bankMin = 1.0f;
            this.bankMax = 8.0f;
            this.distanceMin = 0.025f;
            this.distanceMax = 0.05f;
        }
    }
}

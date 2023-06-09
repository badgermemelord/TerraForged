// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.rivermap.river;

import com.terraforged.engine.world.heightmap.Levels;
import com.terraforged.noise.util.NoiseUtil;

public class RiverConfig
{
    public final int order;
    public final boolean main;
    public final int bedWidth;
    public final int bankWidth;
    public final float bedHeight;
    public final float minBankHeight;
    public final float maxBankHeight;
    public final int length;
    public final int length2;
    public final float fade;
    
    private RiverConfig(final Builder builder) {
        this.main = builder.main;
        this.order = builder.order;
        this.bedWidth = builder.bedWidth;
        this.bankWidth = builder.bankWidth;
        this.bedHeight = builder.levels.water(-builder.bedDepth);
        this.minBankHeight = builder.levels.water(builder.minBankHeight);
        this.maxBankHeight = builder.levels.water(builder.maxBankHeight);
        this.length = builder.length;
        this.length2 = builder.length * builder.length;
        this.fade = (float)builder.fade;
    }
    
    public RiverConfig(final boolean main, final int order, final int bedWidth, final int bankWidth, final float bedHeight, final float minBankHeight, final float maxBankHeight, final int length, final int length2, final double fade) {
        this.main = main;
        this.order = order;
        this.bedWidth = bedWidth;
        this.bankWidth = bankWidth;
        this.bedHeight = bedHeight;
        this.minBankHeight = minBankHeight;
        this.maxBankHeight = maxBankHeight;
        this.length = length;
        this.length2 = length2;
        this.fade = (float)fade;
    }
    
    public RiverConfig createFork(final float connectWidth, final Levels levels) {
        if (this.bankWidth < connectWidth) {
            return this;
        }
        final float scale = this.bankWidth / connectWidth;
        return this.createFork(levels.scale(this.bedHeight), NoiseUtil.round(this.bedWidth / scale), NoiseUtil.round(this.bankWidth / scale), levels);
    }
    
    public RiverConfig createFork(final int bedHeight, final int bedWidth, final int bankWidth, final Levels levels) {
        final int minBankHeight = Math.max(levels.groundLevel, levels.scale(this.minBankHeight) - 1);
        final int maxBankHeight = Math.max(minBankHeight, levels.scale(this.maxBankHeight) - 1);
        return new RiverConfig(false, this.order + 1, bedWidth, bankWidth, levels.scale(bedHeight), levels.scale(minBankHeight), levels.scale(maxBankHeight), this.length, this.length2, this.fade);
    }
    
    public static Builder builder(final Levels levels) {
        return new Builder(levels);
    }
    
    public static class Builder
    {
        private boolean main;
        private int order;
        private int bedWidth;
        private int bankWidth;
        private int bedDepth;
        private int maxBankHeight;
        private int minBankHeight;
        private int length;
        private double fade;
        private final Levels levels;
        
        private Builder(final Levels levels) {
            this.main = false;
            this.order = 0;
            this.bedWidth = 4;
            this.bankWidth = 15;
            this.bedDepth = 5;
            this.maxBankHeight = 1;
            this.minBankHeight = 1;
            this.length = 1000;
            this.fade = 0.2;
            this.levels = levels;
        }
        
        public Builder order(final int order) {
            this.order = order;
            return this;
        }
        
        public Builder main(final boolean value) {
            this.main = value;
            return this;
        }
        
        public Builder bedWidth(final int value) {
            this.bedWidth = value;
            return this;
        }
        
        public Builder bankWidth(final int value) {
            this.bankWidth = value;
            return this;
        }
        
        public Builder bedDepth(final int depth) {
            this.bedDepth = depth;
            return this;
        }
        
        public Builder bankHeight(final int min, final int max) {
            this.minBankHeight = Math.min(min, max);
            this.maxBankHeight = Math.max(min, max);
            return this;
        }
        
        public Builder length(final int value) {
            this.length = value;
            return this;
        }
        
        public Builder fade(final double value) {
            this.fade = value;
            return this;
        }
        
        public RiverConfig build() {
            return new RiverConfig(this, null);
        }
    }
}

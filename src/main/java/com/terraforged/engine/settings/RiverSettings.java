// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.settings;

import com.terraforged.engine.serialization.annotation.Comment;
import com.terraforged.engine.serialization.annotation.Rand;
import com.terraforged.engine.serialization.annotation.Range;
import com.terraforged.engine.serialization.annotation.Serializable;

@Serializable
public class RiverSettings
{
    @Rand
    @Comment({ "A seed offset used to randomise river distribution" })
    public int seedOffset;
    @Range(min = 0.0f, max = 30.0f)
    @Comment({ "Controls the number of main rivers per continent." })
    public int riverCount;
    public River mainRivers;
    public River branchRivers;
    public Lake lakes;
    public Wetland wetlands;
    
    public RiverSettings() {
        this.seedOffset = 0;
        this.riverCount = 8;
        this.mainRivers = new River(5, 2, 6, 20, 8, 0.75f);
        this.branchRivers = new River(4, 1, 4, 14, 5, 0.975f);
        this.lakes = new Lake();
        this.wetlands = new Wetland();
    }
    
    @Serializable
    public static class River
    {
        @Range(min = 1.0f, max = 10.0f)
        @Comment({ "Controls the depth of the river" })
        public int bedDepth;
        @Range(min = 0.0f, max = 10.0f)
        @Comment({ "Controls the height of river banks" })
        public int minBankHeight;
        @Range(min = 1.0f, max = 10.0f)
        @Comment({ "Controls the height of river banks" })
        public int maxBankHeight;
        @Range(min = 1.0f, max = 20.0f)
        @Comment({ "Controls the river-bed width" })
        public int bedWidth;
        @Range(min = 1.0f, max = 50.0f)
        @Comment({ "Controls the river-banks width" })
        public int bankWidth;
        @Range(min = 0.0f, max = 1.0f)
        @Comment({ "Controls how much rivers taper" })
        public float fade;
        
        public River() {
        }
        
        public River(final int depth, final int minBank, final int maxBank, final int outer, final int inner, final float fade) {
            this.minBankHeight = minBank;
            this.maxBankHeight = maxBank;
            this.bankWidth = outer;
            this.bedWidth = inner;
            this.bedDepth = depth;
            this.fade = fade;
        }
    }
    
    @Serializable
    public static class Lake
    {
        @Range(min = 0.0f, max = 1.0f)
        @Comment({ "Controls the chance of a lake spawning" })
        public float chance;
        @Range(min = 0.0f, max = 1.0f)
        @Comment({ "The minimum distance along a river that a lake will spawn" })
        public float minStartDistance;
        @Range(min = 0.0f, max = 1.0f)
        @Comment({ "The maximum distance along a river that a lake will spawn" })
        public float maxStartDistance;
        @Range(min = 1.0f, max = 20.0f)
        @Comment({ "The max depth of the lake" })
        public int depth;
        @Range(min = 10.0f, max = 100.0f)
        @Comment({ "The minimum size of the lake" })
        public int sizeMin;
        @Range(min = 50.0f, max = 500.0f)
        @Comment({ "The maximum size of the lake" })
        public int sizeMax;
        @Range(min = 1.0f, max = 10.0f)
        @Comment({ "The minimum bank height" })
        public int minBankHeight;
        @Range(min = 1.0f, max = 10.0f)
        @Comment({ "The maximum bank height" })
        public int maxBankHeight;
        
        public Lake() {
            this.chance = 0.3f;
            this.minStartDistance = 0.0f;
            this.maxStartDistance = 0.03f;
            this.depth = 10;
            this.sizeMin = 75;
            this.sizeMax = 150;
            this.minBankHeight = 2;
            this.maxBankHeight = 10;
        }
    }
    
    @Serializable
    public static class Wetland
    {
        @Range(min = 0.0f, max = 1.0f)
        @Comment({ "Controls how common wetlands are" })
        public float chance;
        @Range(min = 50.0f, max = 500.0f)
        @Comment({ "The minimum size of the wetlands" })
        public int sizeMin;
        @Range(min = 50.0f, max = 500.0f)
        @Comment({ "The maximum size of the wetlands" })
        public int sizeMax;
        
        public Wetland() {
            this.chance = 0.6f;
            this.sizeMin = 175;
            this.sizeMax = 225;
        }
    }
}

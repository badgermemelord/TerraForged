// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.settings;

import com.terraforged.engine.serialization.annotation.Comment;
import com.terraforged.engine.serialization.annotation.Rand;
import com.terraforged.engine.serialization.annotation.Range;
import com.terraforged.engine.serialization.annotation.Serializable;
import com.terraforged.engine.world.terrain.populator.TerrainPopulator;

@Serializable
public class TerrainSettings
{
    public General general;
    public Terrain steppe;
    public Terrain plains;
    public Terrain hills;
    public Terrain dales;
    public Terrain plateau;
    public Terrain badlands;
    public Terrain torridonian;
    public Terrain mountains;
    public Terrain volcano;
    
    public TerrainSettings() {
        this.general = new General();
        this.steppe = new Terrain(1.0f, 1.0f, 1.0f);
        this.plains = new Terrain(2.0f, 1.0f, 1.0f);
        this.hills = new Terrain(2.0f, 1.0f, 1.0f);
        this.dales = new Terrain(1.5f, 1.0f, 1.0f);
        this.plateau = new Terrain(1.5f, 1.0f, 1.0f);
        this.badlands = new Terrain(1.0f, 1.0f, 1.0f);
        this.torridonian = new Terrain(2.0f, 1.0f, 1.0f);
        this.mountains = new Terrain(2.5f, 1.0f, 1.0f);
        this.volcano = new Terrain(5.0f, 1.0f, 1.0f);
    }
    
    @Serializable
    public static class General
    {
        @Rand
        @Comment({ "A seed offset used to randomise terrain distribution" })
        public int terrainSeedOffset;
        @Range(min = 125.0f, max = 5000.0f)
        @Comment({ "Controls the size of terrain regions" })
        public int terrainRegionSize;
        @Range(min = 0.01f, max = 1.0f)
        @Comment({ "Globally controls the vertical scaling of terrain" })
        public float globalVerticalScale;
        @Range(min = 0.01f, max = 5.0f)
        @Comment({ "Globally controls the horizontal scaling of terrain" })
        public float globalHorizontalScale;
        @Comment({ "Carries out extra processing on mountains to make them look even nicer.", "Can be disabled to improve performance slightly." })
        public boolean fancyMountains;
        
        public General() {
            this.terrainSeedOffset = 0;
            this.terrainRegionSize = 1200;
            this.globalVerticalScale = 0.98f;
            this.globalHorizontalScale = 1.0f;
            this.fancyMountains = true;
        }
    }
    
    @Serializable
    public static class Terrain
    {
        @Range(min = 0.0f, max = 10.0f)
        @Comment({ "Controls how common this terrain type is" })
        public float weight;
        @Range(min = 0.0f, max = 2.0f)
        @Comment({ "Controls the base height of this terrain" })
        public float baseScale;
        @Range(min = 0.0f, max = 10.0f)
        @Comment({ "Stretches or compresses the terrain vertically" })
        public float verticalScale;
        @Range(min = 0.0f, max = 10.0f)
        @Comment({ "Stretches or compresses the terrain horizontally" })
        public float horizontalScale;
        
        public Terrain() {
            this.weight = 1.0f;
            this.baseScale = 1.0f;
            this.verticalScale = 1.0f;
            this.horizontalScale = 1.0f;
        }
        
        public Terrain(final float weight, final float vertical, final float horizontal) {
            this.weight = 1.0f;
            this.baseScale = 1.0f;
            this.verticalScale = 1.0f;
            this.horizontalScale = 1.0f;
            this.weight = weight;
            this.verticalScale = vertical;
            this.horizontalScale = horizontal;
        }
        
        public Module apply(final double bias, final double scale, final Module module) {
            final double moduleBias = bias * this.baseScale;
            final double moduleScale = scale * this.verticalScale;
            final Module outputModule = module.scale(moduleScale).bias(moduleBias);
            return TerrainPopulator.clamp(outputModule);
        }
    }
}

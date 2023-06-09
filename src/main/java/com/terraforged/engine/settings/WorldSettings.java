// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.settings;

import com.terraforged.engine.serialization.annotation.*;
import com.terraforged.engine.world.continent.ContinentType;
import com.terraforged.engine.world.continent.SpawnType;
import com.terraforged.noise.func.DistanceFunc;

@Serializable
public class WorldSettings
{
    public static final int DEFAULT_CONTINENT_SCALE = 3000;
    public transient long seed;
    public Continent continent;
    public ControlPoints controlPoints;
    public Properties properties;
    
    public WorldSettings() {
        this.seed = 0L;
        this.continent = new Continent();
        this.controlPoints = new ControlPoints();
        this.properties = new Properties();
    }
    
    @Serializable
    public static class Continent
    {
        @Comment({ "Controls the continent generator type" })
        public ContinentType continentType;
        @Restricted(name = "continentType", value = { "MULTI", "SINGLE" })
        @Comment({ "Controls how continent shapes are calculated.", "You may also need to adjust the transition points to ensure beaches etc still form." })
        public DistanceFunc continentShape;
        @Range(min = 100.0f, max = 10000.0f)
        @Comment({ "Controls the size of continents.", "You may also need to adjust the transition points to ensure beaches etc still form." })
        public int continentScale;
        @Range(min = 0.5f, max = 1.0f)
        @Comment({ "Controls how much continent centers are offset from the underlying noise grid." })
        public float continentJitter;
        @Range(min = 0.0f, max = 1.0f)
        @Restricted(name = "continentType", value = { "MULTI_IMPROVED" })
        @Comment({ "Reduces the number of continents to create more vast oceans." })
        public float continentSkipping;
        @Range(min = 0.0f, max = 0.75f)
        @Restricted(name = "continentType", value = { "MULTI_IMPROVED" })
        @Comment({ "Increases the variance of continent sizes." })
        public float continentSizeVariance;
        @Range(min = 1.0f, max = 5.0f)
        @Restricted(name = "continentType", value = { "MULTI_IMPROVED" })
        @Comment({ "The number of octaves of noise used to distort the continent." })
        public int continentNoiseOctaves;
        @Range(min = 0.0f, max = 0.5f)
        @Restricted(name = "continentType", value = { "MULTI_IMPROVED" })
        @Comment({ "The contribution strength of each noise octave." })
        public float continentNoiseGain;
        @Range(min = 1.0f, max = 10.0f)
        @Restricted(name = "continentType", value = { "MULTI_IMPROVED" })
        @Comment({ "The frequency multiplier for each noise octave." })
        public float continentNoiseLacunarity;
        
        public Continent() {
            this.continentType = ContinentType.MULTI_IMPROVED;
            this.continentShape = DistanceFunc.EUCLIDEAN;
            this.continentScale = 3000;
            this.continentJitter = 0.7f;
            this.continentSkipping = 0.25f;
            this.continentSizeVariance = 0.25f;
            this.continentNoiseOctaves = 5;
            this.continentNoiseGain = 0.26f;
            this.continentNoiseLacunarity = 4.33f;
        }
    }
    
    @Serializable
    public static class ControlPoints
    {
        @Range(min = 0.0f, max = 1.0f)
        @Limit(upper = "shallowOcean")
        @Comment({ "Controls the point above which deep oceans transition into shallow oceans.", "The greater the gap to the shallow ocean slider, the more gradual the transition." })
        public float deepOcean;
        @Range(min = 0.0f, max = 1.0f)
        @Limit(lower = "deepOcean", upper = "beach")
        @Comment({ "Controls the point above which shallow oceans transition into coastal terrain.", "The greater the gap to the coast slider, the more gradual the transition." })
        public float shallowOcean;
        @Range(min = 0.0f, max = 1.0f)
        @Limit(lower = "shallowOcean", upper = "coast")
        @Comment({ "Controls how much of the coastal terrain is assigned to beach biomes." })
        public float beach;
        @Range(min = 0.0f, max = 1.0f)
        @Limit(lower = "beach", upper = "inland")
        @Comment({ "Controls the size of coastal regions and is also the point below", "which inland terrain transitions into oceans. Certain biomes such", "as Mushroom Fields only generate in coastal areas." })
        public float coast;
        @Range(min = 0.0f, max = 1.0f)
        @Limit(lower = "coast")
        @Comment({ "Controls the overall transition from ocean to inland terrain." })
        public float inland;
        
        public ControlPoints() {
            this.deepOcean = 0.1f;
            this.shallowOcean = 0.25f;
            this.beach = 0.327f;
            this.coast = 0.448f;
            this.inland = 0.502f;
        }
    }
    
    @Serializable
    public static class Properties
    {
        @Comment({ "Set whether spawn should be close to x=0,z=0 or the centre of the nearest continent" })
        public SpawnType spawnType;
        @Range(min = 0.0f, max = 256.0f)
        @Comment({ "Controls the world height" })
        public int worldHeight;
        @Range(min = 0.0f, max = 255.0f)
        @Comment({ "Controls the sea level" })
        public int seaLevel;
        
        public Properties() {
            this.spawnType = SpawnType.CONTINENT_CENTER;
            this.worldHeight = 256;
            this.seaLevel = 63;
        }
    }
}

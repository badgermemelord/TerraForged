// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.settings;

import com.terraforged.engine.serialization.annotation.Comment;
import com.terraforged.engine.serialization.annotation.Rand;
import com.terraforged.engine.serialization.annotation.Range;
import com.terraforged.engine.serialization.annotation.Serializable;
import com.terraforged.noise.Source;
import com.terraforged.noise.util.NoiseUtil;

@Serializable
public class ClimateSettings
{
    public RangeValue temperature;
    public RangeValue moisture;
    public BiomeShape biomeShape;
    public BiomeNoise biomeEdgeShape;
    
    public ClimateSettings() {
        this.temperature = new RangeValue(6, 2, 0.0f, 0.98f, 0.05f);
        this.moisture = new RangeValue(6, 1, 0.0f, 1.0f, 0.0f);
        this.biomeShape = new BiomeShape();
        this.biomeEdgeShape = new BiomeNoise();
    }
    
    @Serializable
    public static class RangeValue
    {
        @Rand
        @Comment({ "A seed offset used to randomise climate distribution" })
        public int seedOffset;
        @Range(min = 1.0f, max = 20.0f)
        @Comment({ "The horizontal scale" })
        public int scale;
        @Range(min = 1.0f, max = 10.0f)
        @Comment({ "How quickly values transition from an extremity" })
        public int falloff;
        @Range(min = 0.0f, max = 1.0f)
        @Comment({ "The lower limit of the range" })
        public float min;
        @Range(min = 0.0f, max = 1.0f)
        @Comment({ "The upper limit of the range" })
        public float max;
        @Range(min = -1.0f, max = 1.0f)
        @Comment({ "The bias towards either end of the range" })
        public float bias;
        
        public RangeValue() {
            this(1, 0.0f, 1.0f, 0.0f);
        }
        
        public RangeValue(final int falloff, final float min, final float max, final float bias) {
            this(7, falloff, min, max, bias);
        }
        
        public RangeValue(final int scale, final int falloff, final float min, final float max, final float bias) {
            this.seedOffset = 0;
            this.scale = 7;
            this.falloff = 2;
            this.bias = -0.1f;
            this.min = min;
            this.max = max;
            this.bias = bias;
            this.scale = scale;
            this.falloff = falloff;
        }
        
        public float getMin() {
            return NoiseUtil.clamp(Math.min(this.min, this.max), 0.0f, 1.0f);
        }
        
        public float getMax() {
            return NoiseUtil.clamp(Math.max(this.min, this.max), this.getMin(), 1.0f);
        }
        
        public float getBias() {
            return NoiseUtil.clamp(this.bias, -1.0f, 1.0f);
        }
        
        public Module apply(final Module module) {
            final float min = this.getMin();
            final float max = this.getMax();
            final float bias = this.getBias() / 2.0f;
            return module.bias(bias).clamp(min, max);
        }
    }
    
    @Serializable
    public static class BiomeShape
    {
        public static final int DEFAULT_BIOME_SIZE = 225;
        @Range(min = 50.0f, max = 2000.0f)
        @Comment({ "Controls the size of individual biomes" })
        public int biomeSize;
        @Range(min = 1.0f, max = 20.0f)
        @Comment({ "Macro noise is used to group large areas of biomes into a single type (such as deserts)" })
        public int macroNoiseSize;
        @Range(min = 1.0f, max = 500.0f)
        @Comment({ "Controls the scale of shape distortion for biomes" })
        public int biomeWarpScale;
        @Range(min = 1.0f, max = 500.0f)
        @Comment({ "Controls the strength of shape distortion for biomes" })
        public int biomeWarpStrength;
        
        public BiomeShape() {
            this.biomeSize = 225;
            this.macroNoiseSize = 8;
            this.biomeWarpScale = 150;
            this.biomeWarpStrength = 80;
        }
    }
    
    @Serializable
    public static class BiomeNoise
    {
        @Comment({ "The noise type" })
        public Source type;
        @Range(min = 1.0f, max = 500.0f)
        @Comment({ "Controls the scale of the noise" })
        public int scale;
        @Range(min = 1.0f, max = 5.0f)
        @Comment({ "Controls the number of noise octaves" })
        public int octaves;
        @Range(min = 0.0f, max = 5.5f)
        @Comment({ "Controls the gain subsequent noise octaves" })
        public float gain;
        @Range(min = 0.0f, max = 10.5f)
        @Comment({ "Controls the lacunarity of subsequent noise octaves" })
        public float lacunarity;
        @Range(min = 1.0f, max = 500.0f)
        @Comment({ "Controls the strength of the noise" })
        public int strength;
        
        public BiomeNoise() {
            this.type = Source.SIMPLEX;
            this.scale = 24;
            this.octaves = 2;
            this.gain = 0.5f;
            this.lacunarity = 2.65f;
            this.strength = 14;
        }
        
        public Module build(final int seed) {
            return Source.build(seed, this.scale, this.octaves).gain(this.gain).lacunarity(this.lacunarity).build(this.type).bias(-0.5);
        }
    }
}

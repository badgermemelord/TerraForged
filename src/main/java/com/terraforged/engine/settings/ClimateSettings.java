//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.settings;

import com.terraforged.engine.serialization.annotation.Comment;
import com.terraforged.engine.serialization.annotation.Rand;
import com.terraforged.engine.serialization.annotation.Range;
import com.terraforged.engine.serialization.annotation.Serializable;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;
import com.terraforged.noise.util.NoiseUtil;

@Serializable
public class ClimateSettings {
    public ClimateSettings.RangeValue temperature = new ClimateSettings.RangeValue(6, 2, 0.0F, 0.98F, 0.05F);
    public ClimateSettings.RangeValue moisture = new ClimateSettings.RangeValue(6, 1, 0.0F, 1.0F, 0.0F);
    public ClimateSettings.BiomeShape biomeShape = new ClimateSettings.BiomeShape();
    public ClimateSettings.BiomeNoise biomeEdgeShape = new ClimateSettings.BiomeNoise();

    public ClimateSettings() {
    }

    @Serializable
    public static class BiomeNoise {
        @Comment({"The noise type"})
        public Source type = Source.SIMPLEX;
        @Range(
                min = 1.0F,
                max = 500.0F
        )
        @Comment({"Controls the scale of the noise"})
        public int scale = 24;
        @Range(
                min = 1.0F,
                max = 5.0F
        )
        @Comment({"Controls the number of noise octaves"})
        public int octaves = 2;
        @Range(
                min = 0.0F,
                max = 5.5F
        )
        @Comment({"Controls the gain subsequent noise octaves"})
        public float gain = 0.5F;
        @Range(
                min = 0.0F,
                max = 10.5F
        )
        @Comment({"Controls the lacunarity of subsequent noise octaves"})
        public float lacunarity = 2.65F;
        @Range(
                min = 1.0F,
                max = 500.0F
        )
        @Comment({"Controls the strength of the noise"})
        public int strength = 14;

        public BiomeNoise() {
        }

        public Module build(int seed) {
            return Source.build(seed, this.scale, this.octaves).gain((double)this.gain).lacunarity((double)this.lacunarity).build(this.type).bias(-0.5);
        }
    }

    @Serializable
    public static class BiomeShape {
        public static final int DEFAULT_BIOME_SIZE = 225;
        @Range(
                min = 50.0F,
                max = 2000.0F
        )
        @Comment({"Controls the size of individual biomes"})
        public int biomeSize = 225;
        @Range(
                min = 1.0F,
                max = 20.0F
        )
        @Comment({"Macro noise is used to group large areas of biomes into a single type (such as deserts)"})
        public int macroNoiseSize = 8;
        @Range(
                min = 1.0F,
                max = 500.0F
        )
        @Comment({"Controls the scale of shape distortion for biomes"})
        public int biomeWarpScale = 150;
        @Range(
                min = 1.0F,
                max = 500.0F
        )
        @Comment({"Controls the strength of shape distortion for biomes"})
        public int biomeWarpStrength = 80;

        public BiomeShape() {
        }
    }

    @Serializable
    public static class RangeValue {
        @Rand
        @Comment({"A seed offset used to randomise climate distribution"})
        public int seedOffset = 0;
        @Range(
                min = 1.0F,
                max = 20.0F
        )
        @Comment({"The horizontal scale"})
        public int scale = 7;
        @Range(
                min = 1.0F,
                max = 10.0F
        )
        @Comment({"How quickly values transition from an extremity"})
        public int falloff = 2;
        @Range(
                min = 0.0F,
                max = 1.0F
        )
        @Comment({"The lower limit of the range"})
        public float min;
        @Range(
                min = 0.0F,
                max = 1.0F
        )
        @Comment({"The upper limit of the range"})
        public float max;
        @Range(
                min = -1.0F,
                max = 1.0F
        )
        @Comment({"The bias towards either end of the range"})
        public float bias = -0.1F;

        public RangeValue() {
            this(1, 0.0F, 1.0F, 0.0F);
        }

        public RangeValue(int falloff, float min, float max, float bias) {
            this(7, falloff, min, max, bias);
        }

        public RangeValue(int scale, int falloff, float min, float max, float bias) {
            this.min = min;
            this.max = max;
            this.bias = bias;
            this.scale = scale;
            this.falloff = falloff;
        }

        public float getMin() {
            return NoiseUtil.clamp(Math.min(this.min, this.max), 0.0F, 1.0F);
        }

        public float getMax() {
            return NoiseUtil.clamp(Math.max(this.min, this.max), this.getMin(), 1.0F);
        }

        public float getBias() {
            return NoiseUtil.clamp(this.bias, -1.0F, 1.0F);
        }

        public Module apply(Module module) {
            float min = this.getMin();
            float max = this.getMax();
            float bias = this.getBias() / 2.0F;
            return module.bias((double)bias).clamp((double)min, (double)max);
        }
    }
}

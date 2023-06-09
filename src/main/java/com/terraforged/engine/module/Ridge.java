// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.module;

import com.terraforged.cereal.spec.Context;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataObject;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;
import com.terraforged.noise.Module;

import java.util.Arrays;

public class Ridge
{
    private final int seed;
    private final int octaves;
    private final float strength;
    private final float gridSize;
    private final float amplitude;
    private final float lacunarity;
    private final float distanceFallOff;
    private final Mode blendMode;
    
    public Ridge(final int seed, final float strength, final float gridSize, final Mode blendMode) {
        this(seed, 1, strength, gridSize, blendMode);
    }
    
    public Ridge(final int seed, final int octaves, final float strength, final float gridSize, final Mode blendMode) {
        this(seed, octaves, strength, gridSize, 1.0f / (octaves + 1), 2.25f, 0.75f, blendMode);
    }
    
    public Ridge(final int seed, final int octaves, final float strength, final float gridSize, final float amplitude, final float lacunarity, final float distanceFallOff, final Mode blendMode) {
        this.seed = seed;
        this.octaves = octaves;
        this.strength = strength;
        this.gridSize = gridSize;
        this.amplitude = amplitude;
        this.lacunarity = lacunarity;
        this.distanceFallOff = distanceFallOff;
        this.blendMode = blendMode;
    }
    
    public Noise wrap(final Module source) {
        return new Noise(this, source);
    }
    
    public float getValue(final float x, final float y, final Module source) {
        return this.getValue(x, y, source, new float[25]);
    }
    
    public float getValue(final float x, final float y, final Module source, final float[] cache) {
        final float value = source.getValue(x, y);
        final float erosion = this.getErosionValue(x, y, source, cache);
        return NoiseUtil.lerp(erosion, value, this.blendMode.blend(value, erosion, this.strength));
    }
    
    public float getErosionValue(float x, float y, final Module source, final float[] cache) {
        float sum = 0.0f;
        float max = 0.0f;
        float gain = 1.0f;
        float distance = this.gridSize;
        for (int i = 0; i < this.octaves; ++i) {
            float value = this.getSingleErosionValue(x, y, distance, source, cache);
            value *= gain;
            sum += value;
            max += gain;
            gain *= this.amplitude;
            distance *= this.distanceFallOff;
            x *= this.lacunarity;
            y *= this.lacunarity;
        }
        return sum / max;
    }
    
    public float getSingleErosionValue(final float x, final float y, final float gridSize, final Module source, final float[] cache) {
        Arrays.fill(cache, -1.0f);
        final int pix = NoiseUtil.floor(x / gridSize);
        final int piy = NoiseUtil.floor(y / gridSize);
        float minHeight2 = Float.MAX_VALUE;
        for (int dy1 = -1; dy1 <= 1; ++dy1) {
            for (int dx1 = -1; dx1 <= 1; ++dx1) {
                final int pax = pix + dx1;
                final int pay = piy + dy1;
                final Vec2f vec1 = NoiseUtil.cell(this.seed, pax, pay);
                final float ax = (pax + vec1.x) * gridSize;
                final float ay = (pay + vec1.y) * gridSize;
                float bx = ax;
                float by = ay;
                float lowestNeighbour = Float.MAX_VALUE;
                for (int dy2 = -1; dy2 <= 1; ++dy2) {
                    for (int dx2 = -1; dx2 <= 1; ++dx2) {
                        final int pbx = pax + dx2;
                        final int pby = pay + dy2;
                        final Vec2f vec2 = (pbx == pax && pby == pay) ? vec1 : NoiseUtil.cell(this.seed, pbx, pby);
                        final float candidateX = (pbx + vec2.x) * gridSize;
                        final float candidateY = (pby + vec2.y) * gridSize;
                        final float height = getNoiseValue(dx1 + dx2, dy1 + dy2, candidateX, candidateY, source, cache);
                        if (height < lowestNeighbour) {
                            lowestNeighbour = height;
                            bx = candidateX;
                            by = candidateY;
                        }
                    }
                }
                final float height2 = sd(x, y, ax, ay, bx, by);
                if (height2 < minHeight2) {
                    minHeight2 = height2;
                }
            }
        }
        return NoiseUtil.clamp(sqrt(minHeight2) / gridSize, 0.0f, 1.0f);
    }
    
    private static float getNoiseValue(final int dx, final int dy, final float px, final float py, final Module module, final float[] cache) {
        final int index = (dy + 2) * 5 + (dx + 2);
        float value = cache[index];
        if (value == -1.0f) {
            value = module.getValue(px, py);
            cache[index] = value;
        }
        return value;
    }
    
    private static float sd(final float px, final float py, final float ax, final float ay, final float bx, final float by) {
        final float padx = px - ax;
        final float pady = py - ay;
        final float badx = bx - ax;
        final float bady = by - ay;
        final float paba = padx * badx + pady * bady;
        final float baba = badx * badx + bady * bady;
        final float h = NoiseUtil.clamp(paba / baba, 0.0f, 1.0f);
        return len2(padx, pady, badx * h, bady * h);
    }
    
    private static float len2(final float x1, final float y1, final float x2, final float y2) {
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        return dx * dx + dy * dy;
    }
    
    private static float sqrt(final float value) {
        return (float)Math.sqrt(value);
    }
    
    public static DataSpec<?> spec() {
        return DataSpec.builder("Valley", Noise.class, Ridge::create).add("seed", (Object)1337, f -> f.ridge.seed).add("octaves", (Object)1, f -> f.ridge.octaves).add("strength", (Object)1, f -> f.ridge.strength).add("grid_size", (Object)100, f -> f.ridge.gridSize).add("amplitude", (Object)0.5f, f -> f.ridge.amplitude).add("lacunarity", (Object)2.25f, f -> f.ridge.lacunarity).add("fall_off", (Object)0.75f, f -> f.ridge.distanceFallOff).add("blend", (Object)Mode.CONSTANT, f -> f.ridge.blendMode).addObj("source", f -> f.source).build();
    }
    
    private static Noise create(final DataObject data, final DataSpec<Noise> spec, final Context context) {
        final Ridge ridge = new Ridge(spec.get("seed", data, DataValue::asInt), spec.get("octaves", data, DataValue::asInt), spec.get("strength", data, DataValue::asFloat), spec.get("grid_size", data, DataValue::asFloat), spec.get("amplitude", data, DataValue::asFloat), spec.get("lacunarity", data, DataValue::asFloat), spec.get("fall_off", data, DataValue::asFloat), spec.getEnum("blend", data, Mode.class));
        final Module source = spec.get("source", data, Module.class);
        return ridge.wrap(source);
    }
    
    public static class Noise implements Module
    {
        private final Ridge ridge;
        private final Module source;
        private final ThreadLocal<float[]> cache;
        
        private Noise(final Ridge ridge, final Module source) {
            this.cache = ThreadLocal.withInitial(() -> new float[25]);
            this.ridge = ridge;
            this.source = source;
        }
        
        @Override
        public String getSpecName() {
            return "Valley";
        }
        
        @Override
        public float getValue(final float x, final float y) {
            return this.ridge.getValue(x, y, this.source, this.cache.get());
        }
    }
    
    public enum Mode
    {
        CONSTANT {
            @Override
            public float blend(final float value, final float erosion, final float strength) {
                return 1.0f - strength;
            }
        }, 
        INPUT_LINEAR {
            @Override
            public float blend(final float value, final float erosion, final float strength) {
                return 1.0f - strength * value;
            }
        }, 
        OUTPUT_LINEAR {
            @Override
            public float blend(final float value, final float erosion, final float strength) {
                return 1.0f - strength * erosion;
            }
        };
        
        public abstract float blend(final float p0, final float p1, final float p2);
    }
}

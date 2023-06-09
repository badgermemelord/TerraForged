// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.render;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.concurrent.pool.ObjectPool;
import com.terraforged.engine.world.heightmap.Levels;
import com.terraforged.engine.world.terrain.TerrainType;
import com.terraforged.noise.util.NoiseUtil;

import java.awt.*;

public enum RenderMode
{
    BIOME_TYPE {
        @Override
        public void fill(final Cell cell, final float height, final RenderBuffer buffer, final RenderSettings context) {
            try (final Resource<HSBBuf> buf = RenderMode.hsbBufs.get()) {
                final float[] hsb = buf.get().hsb;
                if (cell.terrain == TerrainType.BEACH) {
                    hsb[0] = 0.15f;
                    hsb[1] = 0.55f;
                    hsb[2] = 1.0f;
                }
                else {
                    final Color c = cell.biome.getColor();
                    Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
                }
                color(buffer, hsb[0] * 100.0f, hsb[1] * 100.0f, hsb[2] * 100.0f, height, 0.5f, context.levels);
            }
        }
    }, 
    ELEVATION {
        @Override
        public void fill(final Cell cell, final float height, final RenderBuffer buffer, final RenderSettings context) {
            final float temp = cell.temperature;
            final float moist = Math.min(temp, cell.moisture);
            final float hue = 35.0f - temp * (1.0f - moist) * 25.0f;
            color(buffer, hue, 70.0f, 80.0f, height, 0.3f, context.levels);
        }
    }, 
    TEMPERATURE {
        @Override
        public void fill(final Cell cell, final float height, final RenderBuffer buffer, final RenderSettings context) {
            if (cell.temperature < 0.0f || cell.temperature > 1.0f) {
                System.out.println(cell.temperature);
            }
            final float hue = hue(1.0f - cell.temperature, 8, 70);
            color(buffer, hue, 70.0f, 80.0f, height, 0.35f, context.levels);
        }
    }, 
    MOISTURE {
        @Override
        public void fill(final Cell cell, final float height, final RenderBuffer buffer, final RenderSettings context) {
            final float hue = hue(cell.moisture, 64, 70);
            color(buffer, hue, 70.0f, 80.0f, height, 0.35f, context.levels);
        }
    }, 
    BIOME {
        @Override
        public void fill(final Cell cell, final float height, final RenderBuffer buffer, final RenderSettings context) {
            final float hue = cell.biomeRegionId * 70.0f;
            color(buffer, hue, 70.0f, 80.0f, height, 0.4f, context.levels);
        }
    }, 
    MACRO_NOISE {
        @Override
        public void fill(final Cell cell, final float height, final RenderBuffer buffer, final RenderSettings context) {
            final float hue = hue(1.0f - cell.macroBiomeId, 64, 70);
            color(buffer, hue, 70.0f, 70.0f, height, 0.4f, context.levels);
        }
    }, 
    STEEPNESS {
        @Override
        public void fill(final Cell cell, final float height, final RenderBuffer buffer, final RenderSettings context) {
            final float hue = hue(1.0f - cell.gradient, 64, 70);
            color(buffer, hue, 70.0f, 70.0f, height, 0.4f, context.levels);
        }
    }, 
    TERRAIN_TYPE {
        @Override
        public void fill(final Cell cell, final float height, final RenderBuffer buffer, final RenderSettings context) {
            float hue = 20.0f + cell.terrain.getRenderHue() * 80.0f;
            if (cell.terrain == TerrainType.COAST) {
                hue = 15.0f;
            }
            if (cell.continentEdge < 0.01f) {
                hue = 70.0f;
            }
            color(buffer, hue, 70.0f, 70.0f, height, 0.4f, context.levels);
        }
    }, 
    CONTINENT {
        @Override
        public void fill(final Cell cell, final float height, final RenderBuffer buffer, final RenderSettings context) {
            final float hue = cell.continentId * 70.0f;
            color(buffer, hue, 70.0f, 70.0f, height, 0.4f, context.levels);
        }
    };
    
    private static final ObjectPool<HSBBuf> hsbBufs;
    
    public abstract void fill(final Cell p0, final float p1, final RenderBuffer p2, final RenderSettings p3);
    
    public void fillColor(final Cell cell, final float height, final RenderBuffer buffer, final RenderSettings context) {
        if (height <= context.levels.waterLevel) {
            final float temp = cell.temperature;
            final float tempDelta = (temp > 0.5) ? (temp - 0.5f) : (-(0.5f - temp));
            final float tempAlpha = tempDelta / 0.5f;
            final float hueMod = 4.0f * tempAlpha;
            final float depth = (context.levels.waterLevel - height) / 90.0f;
            final float darkness = 1.0f - depth;
            final float darknessMod = 0.5f + darkness * 0.5f;
            buffer.color(60.0f - hueMod, 65.0f, 90.0f * darknessMod);
        }
        else {
            this.fill(cell, height, buffer, context);
        }
    }
    
    private static float hue(float value, final int steps, final int max) {
        value = (float)Math.round(value * (steps - 1));
        value /= steps - 1;
        return value * max;
    }
    
    private static void color(final RenderBuffer buffer, final float hue, final float saturation, final float brightness, final float height, final float strength, final Levels levels) {
        final float value = NoiseUtil.clamp((height - levels.waterLevel) / (levels.worldHeight - levels.waterLevel), 0.0f, 1.0f);
        final float shade = 1.0f - strength + value * strength;
        final float sat = saturation * (1.0f - shade * 0.1f);
        final float bri = brightness * shade;
        buffer.color(hue, sat, bri);
    }
    
    private static float brightness(final float value, final Cell cell, final Levels levels, final float strength) {
        if (cell.value <= levels.water) {
            return value;
        }
        float alpha = (cell.value - levels.water) / (1.0f - levels.water);
        alpha = 1.0f - strength + alpha * strength;
        return value * alpha;
    }
    
    static {
        hsbBufs = new ObjectPool<HSBBuf>(5, HSBBuf::new);
    }
}

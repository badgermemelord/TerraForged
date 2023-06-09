// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.climate;

import com.terraforged.engine.Seed;
import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.settings.Settings;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.biome.type.BiomeType;
import com.terraforged.engine.world.continent.Continent;
import com.terraforged.engine.world.heightmap.ControlPoints;
import com.terraforged.engine.world.terrain.TerrainType;
import com.terraforged.noise.Source;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.func.EdgeFunc;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

public class ClimateModule
{
    private static final float MOISTURE_SIZE = 2.5f;
    private final int seed;
    private final float edgeClamp;
    private final float edgeScale;
    private final float biomeFreq;
    private final float warpStrength;
    private final Module warpX;
    private final Module warpZ;
    private final Module moisture;
    private final Module temperature;
    private final Module macroBiomeNoise;
    private final Continent continent;
    private final ControlPoints controlPoints;
    
    public ClimateModule(final Continent continent, final GeneratorContext context) {
        final Seed seed = context.seed;
        final Settings settings = context.settings;
        final int biomeSize = settings.climate.biomeShape.biomeSize;
        final float tempScaler = (float)settings.climate.temperature.scale;
        final float moistScaler = settings.climate.moisture.scale * 2.5f;
        final float biomeFreq = 1.0f / biomeSize;
        final float moistureSize = moistScaler * biomeSize;
        final float temperatureSize = tempScaler * biomeSize;
        final int moistScale = NoiseUtil.round(moistureSize * biomeFreq);
        final int tempScale = NoiseUtil.round(temperatureSize * biomeFreq);
        final int warpScale = settings.climate.biomeShape.biomeWarpScale;
        this.continent = continent;
        this.seed = seed.next();
        this.edgeClamp = 1.0f;
        this.edgeScale = 1.0f / this.edgeClamp;
        this.biomeFreq = 1.0f / biomeSize;
        this.controlPoints = new ControlPoints(context.settings.world.controlPoints);
        this.warpStrength = (float)settings.climate.biomeShape.biomeWarpStrength;
        this.warpX = Source.simplex(seed.next(), warpScale, 2).bias(-0.5);
        this.warpZ = Source.simplex(seed.next(), warpScale, 2).bias(-0.5);
        final Seed moistureSeed = seed.offset(settings.climate.moisture.seedOffset);
        final Module moisture = new Moisture(moistureSeed.next(), moistScale, settings.climate.moisture.falloff);
        this.moisture = settings.climate.moisture.apply(moisture).warp(moistureSeed.next(), Math.max(1, moistScale / 2), 1, moistScale / 4.0).warp(moistureSeed.next(), Math.max(1, moistScale / 6), 2, moistScale / 12.0);
        final Seed tempSeed = seed.offset(settings.climate.temperature.seedOffset);
        final Module temperature = new Temperature(1.0f / tempScale, settings.climate.temperature.falloff);
        this.temperature = settings.climate.temperature.apply(temperature).warp(tempSeed.next(), tempScale * 4, 2, tempScale * 4).warp(tempSeed.next(), tempScale, 1, tempScale);
        this.macroBiomeNoise = Source.cell(seed.next(), context.settings.climate.biomeShape.macroNoiseSize);
    }
    
    public void apply(final Cell cell, final float x, final float y) {
        this.apply(cell, x, y, true);
    }
    
    public void apply(final Cell cell, float x, float y, final boolean mask) {
        final float ox = this.warpX.getValue(x, y) * this.warpStrength;
        final float oz = this.warpZ.getValue(x, y) * this.warpStrength;
        x += ox;
        y += oz;
        x *= this.biomeFreq;
        y *= this.biomeFreq;
        final int xr = NoiseUtil.floor(x);
        final int yr = NoiseUtil.floor(y);
        int cellX = xr;
        int cellY = yr;
        float centerX = x;
        float centerY = y;
        float edgeDistance = 999999.0f;
        float edgeDistance2 = 999999.0f;
        final DistanceFunc dist = DistanceFunc.EUCLIDEAN;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                final int cx = xr + dx;
                final int cy = yr + dy;
                final Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                final float cxf = cx + vec.x;
                final float cyf = cy + vec.y;
                final float distance = dist.apply(cxf - x, cyf - y);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                    centerX = cxf;
                    centerY = cyf;
                    cellX = cx;
                    cellY = cy;
                }
                else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
        cell.biomeRegionId = this.cellValue(this.seed, cellX, cellY);
        cell.moisture = this.moisture.getValue(centerX, centerY);
        cell.temperature = this.temperature.getValue(centerX, centerY);
        cell.macroBiomeId = this.macroBiomeNoise.getValue(centerX, centerY);
        final int posX = NoiseUtil.floor(centerX / this.biomeFreq);
        final int posZ = NoiseUtil.floor(centerY / this.biomeFreq);
        final float continentEdge = this.continent.getLandValue((float)posX, (float)posZ);
        if (mask) {
            cell.biomeRegionEdge = this.edgeValue(edgeDistance, edgeDistance2);
            this.modifyTerrain(cell, continentEdge);
        }
        this.modifyMoisture(cell, continentEdge);
        cell.biome = BiomeType.get(cell.temperature, cell.moisture);
    }
    
    private void modifyMoisture(final Cell cell, final float continentEdge) {
        final float limit = 0.75f;
        final float range = 1.0f - limit;
        if (continentEdge < limit) {
            final float alpha = (limit - continentEdge) / range;
            final float multiplier = 1.0f + alpha * range;
            cell.moisture = NoiseUtil.clamp(cell.moisture * multiplier, 0.0f, 1.0f);
        }
        else {
            final float alpha = (continentEdge - limit) / range;
            final float multiplier = 1.0f - alpha * range;
            cell.moisture *= multiplier;
        }
    }
    
    private void modifyTerrain(final Cell cell, final float continentEdge) {
        if (cell.terrain.isOverground() && !cell.terrain.overridesCoast() && continentEdge <= this.controlPoints.coastMarker) {
            cell.terrain = TerrainType.COAST;
        }
    }
    
    private float cellValue(final int seed, final int cellX, final int cellY) {
        final float value = NoiseUtil.valCoord2D(seed, cellX, cellY);
        return NoiseUtil.map(value, -1.0f, 1.0f, 2.0f);
    }
    
    private float edgeValue(final float distance, final float distance2) {
        final EdgeFunc edge = EdgeFunc.DISTANCE_2_DIV;
        float value = edge.apply(distance, distance2);
        value = 1.0f - NoiseUtil.map(value, edge.min(), edge.max(), edge.range());
        return value;
    }
}

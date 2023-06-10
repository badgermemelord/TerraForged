//
// Source code recreated from a .class file by Quiltflower
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
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.func.EdgeFunc;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

public class ClimateModule {
    private static final float MOISTURE_SIZE = 2.5F;
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

    public ClimateModule(Continent continent, GeneratorContext context) {
        Seed seed = context.seed;
        Settings settings = context.settings;
        int biomeSize = settings.climate.biomeShape.biomeSize;
        float tempScaler = (float)settings.climate.temperature.scale;
        float moistScaler = (float)settings.climate.moisture.scale * 2.5F;
        float biomeFreq = 1.0F / (float)biomeSize;
        float moistureSize = moistScaler * (float)biomeSize;
        float temperatureSize = tempScaler * (float)biomeSize;
        int moistScale = NoiseUtil.round(moistureSize * biomeFreq);
        int tempScale = NoiseUtil.round(temperatureSize * biomeFreq);
        int warpScale = settings.climate.biomeShape.biomeWarpScale;
        this.continent = continent;
        this.seed = seed.next();
        this.edgeClamp = 1.0F;
        this.edgeScale = 1.0F / this.edgeClamp;
        this.biomeFreq = 1.0F / (float)biomeSize;
        this.controlPoints = new ControlPoints(context.settings.world.controlPoints);
        this.warpStrength = (float)settings.climate.biomeShape.biomeWarpStrength;
        this.warpX = Source.simplex(seed.next(), warpScale, 2).bias(-0.5);
        this.warpZ = Source.simplex(seed.next(), warpScale, 2).bias(-0.5);
        Seed moistureSeed = seed.offset(settings.climate.moisture.seedOffset);
        Module moisture = new Moisture(moistureSeed.next(), moistScale, settings.climate.moisture.falloff);
        this.moisture = settings.climate
                .moisture
                .apply(moisture)
                .warp(moistureSeed.next(), Math.max(1, moistScale / 2), 1, (double)moistScale / 4.0)
                .warp(moistureSeed.next(), Math.max(1, moistScale / 6), 2, (double)moistScale / 12.0);
        Seed tempSeed = seed.offset(settings.climate.temperature.seedOffset);
        Module temperature = new Temperature(1.0F / (float)tempScale, settings.climate.temperature.falloff);
        this.temperature = settings.climate
                .temperature
                .apply(temperature)
                .warp(tempSeed.next(), tempScale * 4, 2, (double)(tempScale * 4))
                .warp(tempSeed.next(), tempScale, 1, (double)tempScale);
        this.macroBiomeNoise = Source.cell(seed.next(), context.settings.climate.biomeShape.macroNoiseSize);
    }

    public void apply(Cell cell, float x, float y) {
        this.apply(cell, x, y, true);
    }

    public void apply(Cell cell, float x, float y, boolean mask) {
        float ox = this.warpX.getValue(x, y) * this.warpStrength;
        float oz = this.warpZ.getValue(x, y) * this.warpStrength;
        x += ox;
        y += oz;
        x *= this.biomeFreq;
        y *= this.biomeFreq;
        int xr = NoiseUtil.floor(x);
        int yr = NoiseUtil.floor(y);
        int cellX = xr;
        int cellY = yr;
        float centerX = x;
        float centerY = y;
        float edgeDistance = 999999.0F;
        float edgeDistance2 = 999999.0F;
        DistanceFunc dist = DistanceFunc.EUCLIDEAN;

        for(int dy = -1; dy <= 1; ++dy) {
            for(int dx = -1; dx <= 1; ++dx) {
                int cx = xr + dx;
                int cy = yr + dy;
                Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                float cxf = (float)cx + vec.x;
                float cyf = (float)cy + vec.y;
                float distance = dist.apply(cxf - x, cyf - y);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                    centerX = cxf;
                    centerY = cyf;
                    cellX = cx;
                    cellY = cy;
                } else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }

        cell.biomeRegionId = this.cellValue(this.seed, cellX, cellY);
        cell.moisture = this.moisture.getValue(centerX, centerY);
        cell.temperature = this.temperature.getValue(centerX, centerY);
        cell.macroBiomeId = this.macroBiomeNoise.getValue(centerX, centerY);
        int posX = NoiseUtil.floor(centerX / this.biomeFreq);
        int posZ = NoiseUtil.floor(centerY / this.biomeFreq);
        float continentEdge = this.continent.getLandValue((float)posX, (float)posZ);
        if (mask) {
            cell.biomeRegionEdge = this.edgeValue(edgeDistance, edgeDistance2);
            this.modifyTerrain(cell, continentEdge);
        }

        this.modifyMoisture(cell, continentEdge);
        cell.biome = BiomeType.get(cell.temperature, cell.moisture);
    }

    private void modifyMoisture(Cell cell, float continentEdge) {
        float limit = 0.75F;
        float range = 1.0F - limit;
        if (continentEdge < limit) {
            float alpha = (limit - continentEdge) / range;
            float multiplier = 1.0F + alpha * range;
            cell.moisture = NoiseUtil.clamp(cell.moisture * multiplier, 0.0F, 1.0F);
        } else {
            float alpha = (continentEdge - limit) / range;
            float multiplier = 1.0F - alpha * range;
            cell.moisture *= multiplier;
        }
    }

    private void modifyTerrain(Cell cell, float continentEdge) {
        if (cell.terrain.isOverground() && !cell.terrain.overridesCoast() && continentEdge <= this.controlPoints.coastMarker) {
            cell.terrain = TerrainType.COAST;
        }
    }

    private float cellValue(int seed, int cellX, int cellY) {
        float value = NoiseUtil.valCoord2D(seed, cellX, cellY);
        return NoiseUtil.map(value, -1.0F, 1.0F, 2.0F);
    }

    private float edgeValue(float distance, float distance2) {
        EdgeFunc edge = EdgeFunc.DISTANCE_2_DIV;
        float value = edge.apply(distance, distance2);
        return 1.0F - NoiseUtil.map(value, edge.min(), edge.max(), edge.range());
    }
}

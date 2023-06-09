// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.climate;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.continent.Continent;
import com.terraforged.engine.world.heightmap.Levels;
import com.terraforged.engine.world.terrain.TerrainType;
import com.terraforged.noise.Source;
import com.terraforged.noise.source.Rand;
import com.terraforged.noise.util.NoiseUtil;

public class Climate
{
    private final float seaLevel;
    private final float lowerHeight;
    private final float midHeight = 0.45f;
    private final float upperHeight = 0.75f;
    private final float moistureModifier = 0.1f;
    private final float temperatureModifier = 0.05f;
    private final Rand rand;
    private final Module offsetX;
    private final Module offsetY;
    private final int offsetDistance;
    private final Levels levels;
    private final ClimateModule biomeNoise;
    
    public Climate(final Continent continent, final GeneratorContext context) {
        this.biomeNoise = new ClimateModule(continent, context);
        this.levels = context.levels;
        this.offsetDistance = context.settings.climate.biomeEdgeShape.strength;
        this.rand = new Rand(Source.builder().seed(context.seed.next()));
        this.offsetX = context.settings.climate.biomeEdgeShape.build(context.seed.next());
        this.offsetY = context.settings.climate.biomeEdgeShape.build(context.seed.next());
        this.seaLevel = context.levels.water;
        this.lowerHeight = context.levels.ground;
    }
    
    public Rand getRand() {
        return this.rand;
    }
    
    public float getOffsetX(final float x, final float z, final float distance) {
        return this.offsetX.getValue(x, z) * distance;
    }
    
    public float getOffsetZ(final float x, final float z, final float distance) {
        return this.offsetY.getValue(x, z) * distance;
    }
    
    public void apply(final Cell cell, float x, float z) {
        this.biomeNoise.apply(cell, x, z, true);
        final float edgeBlend = 0.4f;
        if (cell.value <= this.levels.water) {
            if (cell.terrain == TerrainType.COAST) {
                cell.terrain = TerrainType.SHALLOW_OCEAN;
            }
        }
        else if (cell.biomeRegionEdge < edgeBlend || cell.terrain == TerrainType.MOUNTAIN_CHAIN) {
            final float modifier = 1.0f - NoiseUtil.map(cell.biomeRegionEdge, 0.0f, edgeBlend, edgeBlend);
            final float distance = this.offsetDistance * modifier;
            final float dx = this.getOffsetX(x, z, distance);
            final float dz = this.getOffsetZ(x, z, distance);
            x += dx;
            z += dz;
            this.biomeNoise.apply(cell, x, z, false);
        }
        this.modifyTemp(cell, x, z);
    }
    
    private void modifyTemp(final Cell cell, final float x, final float z) {
        float height = cell.value;
        if (height > 0.75f) {
            cell.temperature = Math.max(0.0f, cell.temperature - 0.05f);
            return;
        }
        if (height > 0.45f) {
            final float delta = (height - 0.45f) / 0.3f;
            cell.temperature = Math.max(0.0f, cell.temperature - delta * 0.05f);
            return;
        }
        height = Math.max(this.lowerHeight, height);
        if (height >= this.lowerHeight) {
            final float delta = 1.0f - (height - this.lowerHeight) / (0.45f - this.lowerHeight);
            cell.temperature = Math.min(1.0f, cell.temperature + delta * 0.05f);
        }
    }
    
    private void modifyMoisture(final Cell cell, final float x, final float z) {
    }
}

//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.climate;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.continent.Continent;
import com.terraforged.engine.world.heightmap.Levels;
import com.terraforged.engine.world.terrain.TerrainType;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;
import com.terraforged.noise.source.Rand;
import com.terraforged.noise.util.NoiseUtil;

public class Climate {
    private final float seaLevel;
    private final float lowerHeight;
    private final float midHeight = 0.45F;
    private final float upperHeight = 0.75F;
    private final float moistureModifier = 0.1F;
    private final float temperatureModifier = 0.05F;
    private final Rand rand;
    private final Module offsetX;
    private final Module offsetY;
    private final int offsetDistance;
    private final Levels levels;
    private final ClimateModule biomeNoise;

    public Climate(Continent continent, GeneratorContext context) {
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

    public float getOffsetX(float x, float z, float distance) {
        return this.offsetX.getValue(x, z) * distance;
    }

    public float getOffsetZ(float x, float z, float distance) {
        return this.offsetY.getValue(x, z) * distance;
    }

    public void apply(Cell cell, float x, float z) {
        this.biomeNoise.apply(cell, x, z, true);
        float edgeBlend = 0.4F;
        if (cell.value <= this.levels.water) {
            if (cell.terrain == TerrainType.COAST) {
                cell.terrain = TerrainType.SHALLOW_OCEAN;
            }
        } else if (cell.biomeRegionEdge < edgeBlend || cell.terrain == TerrainType.MOUNTAIN_CHAIN) {
            float modifier = 1.0F - NoiseUtil.map(cell.biomeRegionEdge, 0.0F, edgeBlend, edgeBlend);
            float distance = (float)this.offsetDistance * modifier;
            float dx = this.getOffsetX(x, z, distance);
            float dz = this.getOffsetZ(x, z, distance);
            x += dx;
            z += dz;
            this.biomeNoise.apply(cell, x, z, false);
        }

        this.modifyTemp(cell, x, z);
    }

    private void modifyTemp(Cell cell, float x, float z) {
        float height = cell.value;
        if (height > 0.75F) {
            cell.temperature = Math.max(0.0F, cell.temperature - 0.05F);
        } else if (height > 0.45F) {
            float delta = (height - 0.45F) / 0.3F;
            cell.temperature = Math.max(0.0F, cell.temperature - delta * 0.05F);
        } else {
            height = Math.max(this.lowerHeight, height);
            if (height >= this.lowerHeight) {
                float delta = 1.0F - (height - this.lowerHeight) / (0.45F - this.lowerHeight);
                cell.temperature = Math.min(1.0F, cell.temperature + delta * 0.05F);
            }
        }
    }

    private void modifyMoisture(Cell cell, float x, float z) {
    }
}

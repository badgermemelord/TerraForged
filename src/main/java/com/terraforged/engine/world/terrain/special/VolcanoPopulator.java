// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain.special;

import com.terraforged.engine.Seed;
import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.heightmap.Levels;
import com.terraforged.engine.world.heightmap.RegionConfig;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.engine.world.terrain.TerrainType;
import com.terraforged.engine.world.terrain.populator.TerrainPopulator;
import com.terraforged.noise.Source;
import com.terraforged.noise.func.EdgeFunc;

public class VolcanoPopulator extends TerrainPopulator
{
    private static final float throat_value = 0.925f;
    public static final float RIVER_MASK = 0.85f;
    public static final float COAST_MASK = 0.85f;
    private final Module cone;
    private final Module height;
    private final Module lowlands;
    private final float inversionPoint;
    private final float blendLower;
    private final float blendUpper;
    private final float blendRange;
    private final float bias;
    private final Terrain inner;
    private final Terrain outer;
    
    public VolcanoPopulator(final Seed seed, final RegionConfig region, final Levels levels, final float weight) {
        super(TerrainType.VOLCANO, Source.ZERO, Source.ZERO, weight);
        final float midpoint = 0.3f;
        final float range = 0.3f;
        final Module heightNoise = Source.perlin(seed.next(), 2, 1).map(0.45, 0.65);
        this.height = Source.cellNoise(region.seed, region.scale, heightNoise).warp(region.warpX, region.warpZ, region.warpStrength);
        this.cone = Source.cellEdge(region.seed, region.scale, EdgeFunc.DISTANCE_2_DIV).invert().warp(region.warpX, region.warpZ, region.warpStrength).powCurve(11.0).clamp(0.475, 1.0).map(0.0, 1.0).grad(0.0, 0.5, 0.5).warp(seed.next(), 15, 2, 10.0).scale(this.height);
        this.lowlands = Source.ridge(seed.next(), 150, 3).warp(seed.next(), 30, 1, 30.0).scale(0.1);
        this.inversionPoint = 0.94f;
        this.blendLower = midpoint - range / 2.0f;
        this.blendUpper = this.blendLower + range;
        this.blendRange = this.blendUpper - this.blendLower;
        this.outer = TerrainType.VOLCANO;
        this.inner = TerrainType.VOLCANO_PIPE;
        this.bias = levels.ground;
    }
    
    @Override
    public void apply(final Cell cell, final float x, final float z) {
        float value = this.cone.getValue(x, z);
        final float limit = this.height.getValue(x, z);
        final float maxHeight = limit * this.inversionPoint;
        if (value > maxHeight) {
            final float steepnessModifier = 1.0f;
            final float delta = (value - maxHeight) * steepnessModifier;
            final float range = limit - maxHeight;
            final float alpha = delta / range;
            if (alpha > 0.925f) {
                cell.terrain = this.inner;
            }
            value = maxHeight - maxHeight / 5.0f * alpha;
        }
        else if (value < this.blendLower) {
            value += this.lowlands.getValue(x, z);
            cell.terrain = this.outer;
        }
        else if (value < this.blendUpper) {
            final float alpha2 = 1.0f - (value - this.blendLower) / this.blendRange;
            value += this.lowlands.getValue(x, z) * alpha2;
            cell.terrain = this.outer;
        }
        cell.value = this.bias + value;
    }
    
    public static void modifyVolcanoType(final Cell cell, final Levels levels) {
        if (cell.terrain == TerrainType.VOLCANO_PIPE && (cell.value < levels.water || cell.riverMask < 0.85f)) {
            cell.terrain = TerrainType.VOLCANO;
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain.region;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.cell.Populator;
import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.engine.world.heightmap.RegionConfig;
import com.terraforged.noise.Source;
import com.terraforged.noise.domain.Domain;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.func.EdgeFunc;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

public class RegionModule implements Populator
{
    private static final float JITTER = 0.7f;
    private final int seed;
    private final float frequency;
    private final float edgeMin;
    private final float edgeMax;
    private final float edgeRange;
    private final Domain warp;
    
    public RegionModule(final RegionConfig regionConfig) {
        this.seed = regionConfig.seed + 7;
        this.edgeMin = 0.0f;
        this.edgeMax = 0.5f;
        this.edgeRange = this.edgeMax - this.edgeMin;
        this.frequency = 1.0f / regionConfig.scale;
        this.warp = Domain.warp(regionConfig.warpX, regionConfig.warpZ, Source.constant(regionConfig.warpStrength));
    }
    
    @Override
    public void apply(final Cell cell, final float x, final float y) {
        final float ox = this.warp.getOffsetX(x, y);
        final float oz = this.warp.getOffsetY(x, y);
        float px = x + ox;
        float py = y + oz;
        px *= this.frequency;
        py *= this.frequency;
        int cellX = 0;
        int cellY = 0;
        float centerX = 0.0f;
        float centerY = 0.0f;
        final int xi = NoiseUtil.floor(px);
        final int yi = NoiseUtil.floor(py);
        float edgeDistance = Float.MAX_VALUE;
        float edgeDistance2 = Float.MAX_VALUE;
        final DistanceFunc dist = DistanceFunc.NATURAL;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                final int cx = xi + dx;
                final int cy = yi + dy;
                final Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                final float vecX = cx + vec.x * 0.7f;
                final float vecY = cy + vec.y * 0.7f;
                final float distance = dist.apply(vecX - px, vecY - py);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                    centerX = vecX;
                    centerY = vecY;
                    cellX = cx;
                    cellY = cy;
                }
                else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
        cell.terrainRegionId = this.cellValue(this.seed, cellX, cellY);
        cell.terrainRegionEdge = this.edgeValue(edgeDistance, edgeDistance2);
        cell.terrainRegionCenter = PosUtil.pack(centerX / this.frequency, centerY / this.frequency);
    }
    
    private float cellValue(final int seed, final int cellX, final int cellY) {
        final float value = NoiseUtil.valCoord2D(seed, cellX, cellY);
        return NoiseUtil.map(value, -1.0f, 1.0f, 2.0f);
    }
    
    private float edgeValue(final float distance, final float distance2) {
        final EdgeFunc edge = EdgeFunc.DISTANCE_2_DIV;
        final float value = edge.apply(distance, distance2);
        float edgeValue = 1.0f - NoiseUtil.map(value, edge.min(), edge.max(), edge.range());
        edgeValue = NoiseUtil.pow(edgeValue, 1.5f);
        if (edgeValue < this.edgeMin) {
            return 0.0f;
        }
        if (edgeValue > this.edgeMax) {
            return 1.0f;
        }
        return (edgeValue - this.edgeMin) / this.edgeRange;
    }
}

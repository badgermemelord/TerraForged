// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.rivermap.lake;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.util.Boundsf;
import com.terraforged.engine.world.terrain.TerrainType;
import com.terraforged.engine.world.terrain.populator.TerrainPopulator;
import com.terraforged.noise.Source;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

public class Lake extends TerrainPopulator
{
    protected final float valley;
    protected final float valley2;
    protected final float lakeDistance2;
    protected final float valleyDistance2;
    protected final float bankAlphaMin;
    protected final float bankAlphaMax;
    protected final float bankAlphaRange;
    private final float depth;
    private final float bankMin;
    private final float bankMax;
    protected final Vec2f center;
    
    public Lake(final Vec2f center, final float radius, final float multiplier, final LakeConfig config) {
        super(TerrainType.LAKE, Source.ZERO, Source.ZERO, 1.0f);
        final float lake = radius * multiplier;
        final float valley = 275.0f * multiplier;
        this.valley = valley;
        this.valley2 = valley * valley;
        this.center = center;
        this.depth = config.depth;
        this.bankMin = config.bankMin;
        this.bankMax = config.bankMax;
        this.bankAlphaMin = config.bankMin;
        this.bankAlphaMax = Math.min(1.0f, this.bankAlphaMin + 0.275f);
        this.bankAlphaRange = this.bankAlphaMax - this.bankAlphaMin;
        this.lakeDistance2 = lake * lake;
        this.valleyDistance2 = this.valley2 - this.lakeDistance2;
    }
    
    @Override
    public void apply(final Cell cell, final float x, final float z) {
        final float distance2 = this.getDistance2(x, z);
        if (distance2 > this.valley2) {
            return;
        }
        final float bankHeight = this.getBankHeight(cell);
        if (distance2 <= this.lakeDistance2) {
            cell.value = Math.min(bankHeight, cell.value);
            if (distance2 < this.lakeDistance2) {
                float depthAlpha = 1.0f - distance2 / this.lakeDistance2;
                if (depthAlpha < 0.0f) {
                    depthAlpha = 0.0f;
                }
                else if (depthAlpha > 1.0f) {
                    depthAlpha = 1.0f;
                }
                final float lakeDepth = Math.min(cell.value, this.depth);
                cell.value = NoiseUtil.lerp(cell.value, lakeDepth, depthAlpha);
                cell.terrain = TerrainType.LAKE;
                cell.riverMask = Math.min(cell.riverMask, 1.0f - depthAlpha);
            }
            return;
        }
        if (cell.value < bankHeight) {
            return;
        }
        float valleyAlpha = 1.0f - (distance2 - this.lakeDistance2) / this.valleyDistance2;
        if (valleyAlpha < 0.0f) {
            valleyAlpha = 0.0f;
        }
        else if (valleyAlpha > 1.0f) {
            valleyAlpha = 1.0f;
        }
        cell.value = NoiseUtil.lerp(cell.value, bankHeight, valleyAlpha);
        cell.riverMask *= 1.0f - valleyAlpha;
        cell.riverMask = Math.min(cell.riverMask, 1.0f - valleyAlpha);
    }
    
    public void recordBounds(final Boundsf.Builder builder) {
        builder.record(this.center.x - this.valley * 1.2f, this.center.y - this.valley * 1.2f);
        builder.record(this.center.x + this.valley * 1.2f, this.center.y + this.valley * 1.2f);
    }
    
    public boolean overlaps(final float x, final float z, final float radius2) {
        final float dist2 = this.getDistance2(x, z);
        return dist2 < this.lakeDistance2 + radius2;
    }
    
    protected float getDistance2(final float x, final float z) {
        final float dx = this.center.x - x;
        final float dz = this.center.y - z;
        return dx * dx + dz * dz;
    }
    
    protected float getBankHeight(final Cell cell) {
        final float bankHeightAlpha = NoiseUtil.map(cell.value, this.bankAlphaMin, this.bankAlphaMax, this.bankAlphaRange);
        return NoiseUtil.lerp(this.bankMin, this.bankMax, bankHeightAlpha);
    }
}

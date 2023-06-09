// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.rivermap.wetland;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.util.Boundsf;
import com.terraforged.engine.world.heightmap.Levels;
import com.terraforged.engine.world.terrain.TerrainType;
import com.terraforged.engine.world.terrain.populator.TerrainPopulator;
import com.terraforged.noise.Source;
import com.terraforged.noise.source.Line;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

public class Wetland extends TerrainPopulator
{
    public static final float WIDTH_MIN = 50.0f;
    public static final float WIDTH_MAX = 150.0f;
    private static final float VALLEY = 0.65f;
    private static final float POOLS = 0.7f;
    private static final float BANKS = 0.050000012f;
    private final Vec2f a;
    private final Vec2f b;
    private final float radius;
    private final float radius2;
    private final float bed;
    private final float banks;
    private final float moundMin;
    private final float moundMax;
    private final float moundVariance;
    private final Module moundShape;
    private final Module moundHeight;
    private final Module terrainEdge;
    
    public Wetland(int seed, final Vec2f a, final Vec2f b, final float radius, final Levels levels) {
        super(TerrainType.WETLAND, Source.ZERO, Source.ZERO, 1.0f);
        this.a = a;
        this.b = b;
        this.radius = radius;
        this.radius2 = radius * radius;
        this.bed = levels.water(-1) - 0.5f / levels.worldHeight;
        this.banks = levels.ground(3);
        this.moundMin = levels.water(1);
        this.moundMax = levels.water(2);
        this.moundVariance = this.moundMax - this.moundMin;
        this.moundShape = Source.perlin(++seed, 10, 1).clamp(0.3, 0.6).map(0.0, 1.0);
        this.moundHeight = Source.simplex(++seed, 20, 1).clamp(0.0, 0.3).map(0.0, 1.0);
        this.terrainEdge = Source.perlin(++seed, 8, 1).clamp(0.2, 0.8).map(0.0, 0.9);
    }
    
    @Override
    public void apply(final Cell cell, final float x, final float z) {
        this.apply(cell, x, z, x, z);
    }
    
    public void apply(final Cell cell, final float rx, final float rz, final float x, final float z) {
        if (cell.value < this.bed) {
            return;
        }
        final float t = Line.distanceOnLine(rx, rz, this.a.x, this.a.y, this.b.x, this.b.y);
        final float d2 = getDistance2(rx, rz, this.a.x, this.a.y, this.b.x, this.b.y, t);
        if (d2 > this.radius2) {
            return;
        }
        final float dist = 1.0f - d2 / this.radius2;
        if (dist <= 0.0f) {
            return;
        }
        final float valleyAlpha = NoiseUtil.map(dist, 0.0f, 0.65f, 0.65f);
        if (cell.value > this.banks) {
            cell.value = NoiseUtil.lerp(cell.value, this.banks, valleyAlpha);
        }
        final float poolsAlpha = NoiseUtil.map(dist, 0.65f, 0.7f, 0.050000012f);
        if (cell.value > this.bed && cell.value <= this.banks) {
            cell.value = NoiseUtil.lerp(cell.value, this.bed, poolsAlpha);
        }
        if (poolsAlpha >= 1.0f) {
            cell.erosionMask = true;
        }
        if (dist > 0.65f && poolsAlpha > this.terrainEdge.getValue(x, z)) {
            cell.terrain = this.getType();
        }
        if (cell.value >= this.bed && cell.value < this.moundMax) {
            final float shapeAlpha = this.moundShape.getValue(x, z) * poolsAlpha;
            final float mounds = this.moundMin + this.moundHeight.getValue(x, z) * this.moundVariance;
            cell.value = NoiseUtil.lerp(cell.value, mounds, shapeAlpha);
        }
        cell.riverMask = Math.min(cell.riverMask, 1.0f - valleyAlpha);
    }
    
    public void recordBounds(final Boundsf.Builder builder) {
        builder.record(Math.min(this.a.x, this.b.x) - this.radius, Math.min(this.a.y, this.b.y) - this.radius);
        builder.record(Math.max(this.a.x, this.b.x) + this.radius, Math.max(this.a.y, this.b.y) + this.radius);
    }
    
    private static float getDistance2(final float x, final float y, final float ax, final float ay, final float bx, final float by, final float t) {
        if (t <= 0.0f) {
            return Line.dist2(x, y, ax, ay);
        }
        if (t >= 1.0f) {
            return Line.dist2(x, y, bx, by);
        }
        final float px = ax + t * (bx - ax);
        final float py = ay + t * (by - ay);
        return Line.dist2(x, y, px, py);
    }
}

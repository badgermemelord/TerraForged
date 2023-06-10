//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.rivermap.wetland;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.util.Boundsf.Builder;
import com.terraforged.engine.world.heightmap.Levels;
import com.terraforged.engine.world.terrain.TerrainType;
import com.terraforged.engine.world.terrain.populator.TerrainPopulator;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;
import com.terraforged.noise.source.Line;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

public class Wetland extends TerrainPopulator {
    public static final float WIDTH_MIN = 50.0F;
    public static final float WIDTH_MAX = 150.0F;
    private static final float VALLEY = 0.65F;
    private static final float POOLS = 0.7F;
    private static final float BANKS = 0.050000012F;
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

    public Wetland(int seed, Vec2f a, Vec2f b, float radius, Levels levels) {
        super(TerrainType.WETLAND, Source.ZERO, Source.ZERO, 1.0F);
        this.a = a;
        this.b = b;
        this.radius = radius;
        this.radius2 = radius * radius;
        this.bed = levels.water(-1) - 0.5F / (float)levels.worldHeight;
        this.banks = levels.ground(3);
        this.moundMin = levels.water(1);
        this.moundMax = levels.water(2);
        this.moundVariance = this.moundMax - this.moundMin;
        this.moundShape = Source.perlin(++seed, 10, 1).clamp(0.3, 0.6).map(0.0, 1.0);
        this.moundHeight = Source.simplex(++seed, 20, 1).clamp(0.0, 0.3).map(0.0, 1.0);
        this.terrainEdge = Source.perlin(++seed, 8, 1).clamp(0.2, 0.8).map(0.0, 0.9);
    }

    public void apply(Cell cell, float x, float z) {
        this.apply(cell, x, z, x, z);
    }

    public void apply(Cell cell, float rx, float rz, float x, float z) {
        if (!(cell.value < this.bed)) {
            float t = Line.distanceOnLine(rx, rz, this.a.x, this.a.y, this.b.x, this.b.y);
            float d2 = getDistance2(rx, rz, this.a.x, this.a.y, this.b.x, this.b.y, t);
            if (!(d2 > this.radius2)) {
                float dist = 1.0F - d2 / this.radius2;
                if (!(dist <= 0.0F)) {
                    float valleyAlpha = NoiseUtil.map(dist, 0.0F, 0.65F, 0.65F);
                    if (cell.value > this.banks) {
                        cell.value = NoiseUtil.lerp(cell.value, this.banks, valleyAlpha);
                    }

                    float poolsAlpha = NoiseUtil.map(dist, 0.65F, 0.7F, 0.050000012F);
                    if (cell.value > this.bed && cell.value <= this.banks) {
                        cell.value = NoiseUtil.lerp(cell.value, this.bed, poolsAlpha);
                    }

                    if (poolsAlpha >= 1.0F) {
                        cell.erosionMask = true;
                    }

                    if (dist > 0.65F && poolsAlpha > this.terrainEdge.getValue(x, z)) {
                        cell.terrain = this.getType();
                    }

                    if (cell.value >= this.bed && cell.value < this.moundMax) {
                        float shapeAlpha = this.moundShape.getValue(x, z) * poolsAlpha;
                        float mounds = this.moundMin + this.moundHeight.getValue(x, z) * this.moundVariance;
                        cell.value = NoiseUtil.lerp(cell.value, mounds, shapeAlpha);
                    }

                    cell.riverMask = Math.min(cell.riverMask, 1.0F - valleyAlpha);
                }
            }
        }
    }

    public void recordBounds(Builder builder) {
        builder.record(Math.min(this.a.x, this.b.x) - this.radius, Math.min(this.a.y, this.b.y) - this.radius);
        builder.record(Math.max(this.a.x, this.b.x) + this.radius, Math.max(this.a.y, this.b.y) + this.radius);
    }

    private static float getDistance2(float x, float y, float ax, float ay, float bx, float by, float t) {
        if (t <= 0.0F) {
            return Line.dist2(x, y, ax, ay);
        } else if (t >= 1.0F) {
            return Line.dist2(x, y, bx, by);
        } else {
            float px = ax + t * (bx - ax);
            float py = ay + t * (by - ay);
            return Line.dist2(x, y, px, py);
        }
    }
}

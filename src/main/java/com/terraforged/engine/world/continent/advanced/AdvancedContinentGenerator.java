//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.continent.advanced;

import com.terraforged.engine.Seed;
import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.settings.WorldSettings;
import com.terraforged.engine.settings.WorldSettings.Continent;
import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.continent.SimpleContinent;
import com.terraforged.engine.world.rivermap.Rivermap;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;
import com.terraforged.noise.domain.Domain;
import com.terraforged.noise.source.Line;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

public class AdvancedContinentGenerator extends AbstractContinent implements SimpleContinent {
    protected static final float CENTER_CORRECTION = 0.35F;
    protected final float frequency;
    protected final float variance;
    protected final int varianceSeed;
    protected final Domain warp;
    protected final Module cliffNoise;
    protected final Module bayNoise;

    public AdvancedContinentGenerator(Seed seed, GeneratorContext context) {
        super(seed, context);
        WorldSettings settings = context.settings.world;
        int tectonicScale = settings.continent.continentScale * 4;
        this.frequency = 1.0F / (float)tectonicScale;
        this.varianceSeed = seed.next();
        this.variance = settings.continent.continentSizeVariance;
        this.warp = this.createWarp(seed, tectonicScale, settings.continent);
        this.cliffNoise = Source.build(seed.next(), this.continentScale / 2, 2)
                .build(Source.SIMPLEX2)
                .clamp(0.1, 0.25)
                .map(0.0, 1.0)
                .freq((double)(1.0F / this.frequency), (double)(1.0F / this.frequency));
        this.bayNoise = Source.simplex(seed.next(), 100, 1).scale(0.1).bias(0.9).freq((double)(1.0F / this.frequency), (double)(1.0F / this.frequency));
    }

    public void apply(Cell cell, float x, float y) {
        float wx = this.warp.getX(x, y);
        float wy = this.warp.getY(x, y);
        x = wx * this.frequency;
        y = wy * this.frequency;
        int xi = NoiseUtil.floor(x);
        int yi = NoiseUtil.floor(y);
        int cellX = xi;
        int cellY = yi;
        float cellPointX = x;
        float cellPointY = y;
        float nearest = Float.MAX_VALUE;

        for(int cy = yi - 1; cy <= yi + 1; ++cy) {
            for(int cx = xi - 1; cx <= xi + 1; ++cx) {
                Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                float px = (float)cx + vec.x * this.jitter;
                float py = (float)cy + vec.y * this.jitter;
                float dist2 = Line.dist2(x, y, px, py);
                if (dist2 < nearest) {
                    cellPointX = px;
                    cellPointY = py;
                    cellX = cx;
                    cellY = cy;
                    nearest = dist2;
                }
            }
        }

        nearest = Float.MAX_VALUE;
        float sumX = 0.0F;
        float sumY = 0.0F;

        for(int cy = cellY - 1; cy <= cellY + 1; ++cy) {
            for(int cx = cellX - 1; cx <= cellX + 1; ++cx) {
                if (cx != cellX || cy != cellY) {
                    Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                    float px = (float)cx + vec.x * this.jitter;
                    float py = (float)cy + vec.y * this.jitter;
                    float dist2 = getDistance(x, y, cellPointX, cellPointY, px, py);
                    sumX += px;
                    sumY += py;
                    if (dist2 < nearest) {
                        nearest = dist2;
                    }
                }
            }
        }

        if (!this.shouldSkip(cellX, cellY)) {
            cell.continentId = getCellValue(this.seed, cellX, cellY);
            cell.continentEdge = this.getDistanceValue(x, y, cellX, cellY, nearest);
            cell.continentX = this.getCorrectedContinentCentre(cellPointX, sumX / 8.0F);
            cell.continentZ = this.getCorrectedContinentCentre(cellPointY, sumY / 8.0F);
        }
    }

    public float getEdgeValue(float x, float z) {
        Resource<Cell> resource = Cell.getResource();
        Throwable var4 = null;

        float var6;
        try {
            Cell cell = (Cell)resource.get();
            this.apply(cell, x, z);
            var6 = cell.continentEdge;
        } catch (Throwable var15) {
            var4 = var15;
            throw var15;
        } finally {
            if (resource != null) {
                if (var4 != null) {
                    try {
                        resource.close();
                    } catch (Throwable var14) {
                        var4.addSuppressed(var14);
                    }
                } else {
                    resource.close();
                }
            }
        }

        return var6;
    }

    public long getNearestCenter(float x, float z) {
        Resource<Cell> resource = Cell.getResource();
        Throwable var4 = null;

        long var6;
        try {
            Cell cell = (Cell)resource.get();
            this.apply(cell, x, z);
            var6 = PosUtil.pack(cell.continentX, cell.continentZ);
        } catch (Throwable var16) {
            var4 = var16;
            throw var16;
        } finally {
            if (resource != null) {
                if (var4 != null) {
                    try {
                        resource.close();
                    } catch (Throwable var15) {
                        var4.addSuppressed(var15);
                    }
                } else {
                    resource.close();
                }
            }
        }

        return var6;
    }

    public Rivermap getRivermap(int x, int z) {
        return this.riverCache.getRivers(x, z);
    }

    protected Domain createWarp(Seed seed, int tectonicScale, Continent continent) {
        int warpScale = NoiseUtil.round((float)tectonicScale * 0.225F);
        double strength = (double)NoiseUtil.round((float)tectonicScale * 0.33F);
        return Domain.warp(
                Source.build(seed.next(), warpScale, continent.continentNoiseOctaves)
                        .gain((double)continent.continentNoiseGain)
                        .lacunarity((double)continent.continentNoiseLacunarity)
                        .build(Source.PERLIN2),
                Source.build(seed.next(), warpScale, continent.continentNoiseOctaves)
                        .gain((double)continent.continentNoiseGain)
                        .lacunarity((double)continent.continentNoiseLacunarity)
                        .build(Source.PERLIN2),
                Source.constant(strength)
        );
    }

    protected float getDistanceValue(float x, float y, int cellX, int cellY, float distance) {
        distance = this.getVariedDistanceValue(cellX, cellY, distance);
        distance = NoiseUtil.sqrt(distance);
        distance = NoiseUtil.map(distance, 0.05F, 0.25F, 0.2F);
        distance = this.getCoastalDistanceValue(x, y, distance);
        if (distance < this.controlPoints.inland && distance >= this.controlPoints.shallowOcean) {
            distance = this.getCoastalDistanceValue(x, y, distance);
        }

        return distance;
    }

    protected float getVariedDistanceValue(int cellX, int cellY, float distance) {
        if (this.variance > 0.0F && !this.isDefaultContinent(cellX, cellY)) {
            float sizeValue = getCellValue(this.varianceSeed, cellX, cellY);
            float sizeModifier = NoiseUtil.map(sizeValue, 0.0F, this.variance, this.variance);
            distance *= sizeModifier;
        }

        return distance;
    }

    protected float getCoastalDistanceValue(float x, float y, float distance) {
        if (distance > this.controlPoints.shallowOcean && distance < this.controlPoints.inland) {
            float alpha = distance / this.controlPoints.inland;
            float cliff = this.cliffNoise.getValue(x, y);
            distance = NoiseUtil.lerp(distance * cliff, distance, alpha);
            if (distance < this.controlPoints.shallowOcean) {
                distance = this.controlPoints.shallowOcean * this.bayNoise.getValue(x, y);
            }
        }

        return distance;
    }

    protected int getCorrectedContinentCentre(float point, float average) {
        point = NoiseUtil.lerp(point, average, 0.35F) / this.frequency;
        return (int)point;
    }

    protected static float midPoint(float a, float b) {
        return (a + b) * 0.5F;
    }

    protected static float getDistance(float x, float y, float ax, float ay, float bx, float by) {
        float mx = midPoint(ax, bx);
        float my = midPoint(ay, by);
        float dx = bx - ax;
        float dy = by - ay;
        float nx = -dy;
        return getDistance2Line(x, y, mx, my, mx + nx, my + dx);
    }

    protected static float getDistance2Line(float x, float y, float ax, float ay, float bx, float by) {
        float dx = bx - ax;
        float dy = by - ay;
        float v = (x - ax) * dx + (y - ay) * dy;
        v /= dx * dx + dy * dy;
        float ox = ax + dx * v;
        float oy = ay + dy * v;
        return Line.dist2(x, y, ox, oy);
    }
}

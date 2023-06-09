// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.continent.advanced;

import com.terraforged.engine.Seed;
import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.settings.WorldSettings;
import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.continent.SimpleContinent;
import com.terraforged.engine.world.rivermap.Rivermap;
import com.terraforged.noise.Source;
import com.terraforged.noise.domain.Domain;
import com.terraforged.noise.source.Line;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

public class AdvancedContinentGenerator extends AbstractContinent implements SimpleContinent
{
    protected static final float CENTER_CORRECTION = 0.35f;
    protected final float frequency;
    protected final float variance;
    protected final int varianceSeed;
    protected final Domain warp;
    protected final Module cliffNoise;
    protected final Module bayNoise;
    
    public AdvancedContinentGenerator(final Seed seed, final GeneratorContext context) {
        super(seed, context);
        final WorldSettings settings = context.settings.world;
        final int tectonicScale = settings.continent.continentScale * 4;
        this.frequency = 1.0f / tectonicScale;
        this.varianceSeed = seed.next();
        this.variance = settings.continent.continentSizeVariance;
        this.warp = this.createWarp(seed, tectonicScale, settings.continent);
        this.cliffNoise = Source.build(seed.next(), this.continentScale / 2, 2).build(Source.SIMPLEX2).clamp(0.1, 0.25).map(0.0, 1.0).freq(1.0f / this.frequency, 1.0f / this.frequency);
        this.bayNoise = Source.simplex(seed.next(), 100, 1).scale(0.1).bias(0.9).freq(1.0f / this.frequency, 1.0f / this.frequency);
    }
    
    @Override
    public void apply(final Cell cell, float x, float y) {
        final float wx = this.warp.getX(x, y);
        final float wy = this.warp.getY(x, y);
        x = wx * this.frequency;
        y = wy * this.frequency;
        final int xi = NoiseUtil.floor(x);
        final int yi = NoiseUtil.floor(y);
        int cellX = xi;
        int cellY = yi;
        float cellPointX = x;
        float cellPointY = y;
        float nearest = Float.MAX_VALUE;
        for (int cy = yi - 1; cy <= yi + 1; ++cy) {
            for (int cx = xi - 1; cx <= xi + 1; ++cx) {
                final Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                final float px = cx + vec.x * this.jitter;
                final float py = cy + vec.y * this.jitter;
                final float dist2 = Line.dist2(x, y, px, py);
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
        float sumX = 0.0f;
        float sumY = 0.0f;
        for (int cy2 = cellY - 1; cy2 <= cellY + 1; ++cy2) {
            for (int cx2 = cellX - 1; cx2 <= cellX + 1; ++cx2) {
                if (cx2 != cellX || cy2 != cellY) {
                    final Vec2f vec2 = NoiseUtil.cell(this.seed, cx2, cy2);
                    final float px2 = cx2 + vec2.x * this.jitter;
                    final float py2 = cy2 + vec2.y * this.jitter;
                    final float dist3 = getDistance(x, y, cellPointX, cellPointY, px2, py2);
                    sumX += px2;
                    sumY += py2;
                    if (dist3 < nearest) {
                        nearest = dist3;
                    }
                }
            }
        }
        if (this.shouldSkip(cellX, cellY)) {
            return;
        }
        cell.continentId = AbstractContinent.getCellValue(this.seed, cellX, cellY);
        cell.continentEdge = this.getDistanceValue(x, y, cellX, cellY, nearest);
        cell.continentX = this.getCorrectedContinentCentre(cellPointX, sumX / 8.0f);
        cell.continentZ = this.getCorrectedContinentCentre(cellPointY, sumY / 8.0f);
    }
    
    @Override
    public float getEdgeValue(final float x, final float z) {
        try (final Resource<Cell> resource = Cell.getResource()) {
            final Cell cell = resource.get();
            this.apply(cell, x, z);
            return cell.continentEdge;
        }
    }
    
    @Override
    public long getNearestCenter(final float x, final float z) {
        try (final Resource<Cell> resource = Cell.getResource()) {
            final Cell cell = resource.get();
            this.apply(cell, x, z);
            return PosUtil.pack(cell.continentX, cell.continentZ);
        }
    }
    
    @Override
    public Rivermap getRivermap(final int x, final int z) {
        return this.riverCache.getRivers(x, z);
    }
    
    protected Domain createWarp(final Seed seed, final int tectonicScale, final WorldSettings.Continent continent) {
        final int warpScale = NoiseUtil.round(tectonicScale * 0.225f);
        final double strength = NoiseUtil.round(tectonicScale * 0.33f);
        return Domain.warp(Source.build(seed.next(), warpScale, continent.continentNoiseOctaves).gain(continent.continentNoiseGain).lacunarity(continent.continentNoiseLacunarity).build(Source.PERLIN2), Source.build(seed.next(), warpScale, continent.continentNoiseOctaves).gain(continent.continentNoiseGain).lacunarity(continent.continentNoiseLacunarity).build(Source.PERLIN2), Source.constant(strength));
    }
    
    protected float getDistanceValue(final float x, final float y, final int cellX, final int cellY, float distance) {
        distance = this.getVariedDistanceValue(cellX, cellY, distance);
        distance = NoiseUtil.sqrt(distance);
        distance = NoiseUtil.map(distance, 0.05f, 0.25f, 0.2f);
        distance = this.getCoastalDistanceValue(x, y, distance);
        if (distance < this.controlPoints.inland && distance >= this.controlPoints.shallowOcean) {
            distance = this.getCoastalDistanceValue(x, y, distance);
        }
        return distance;
    }
    
    protected float getVariedDistanceValue(final int cellX, final int cellY, float distance) {
        if (this.variance > 0.0f && !this.isDefaultContinent(cellX, cellY)) {
            final float sizeValue = AbstractContinent.getCellValue(this.varianceSeed, cellX, cellY);
            final float sizeModifier = NoiseUtil.map(sizeValue, 0.0f, this.variance, this.variance);
            distance *= sizeModifier;
        }
        return distance;
    }
    
    protected float getCoastalDistanceValue(final float x, final float y, float distance) {
        if (distance > this.controlPoints.shallowOcean && distance < this.controlPoints.inland) {
            final float alpha = distance / this.controlPoints.inland;
            final float cliff = this.cliffNoise.getValue(x, y);
            distance = NoiseUtil.lerp(distance * cliff, distance, alpha);
            if (distance < this.controlPoints.shallowOcean) {
                distance = this.controlPoints.shallowOcean * this.bayNoise.getValue(x, y);
            }
        }
        return distance;
    }
    
    protected int getCorrectedContinentCentre(float point, final float average) {
        point = NoiseUtil.lerp(point, average, 0.35f) / this.frequency;
        return (int)point;
    }
    
    protected static float midPoint(final float a, final float b) {
        return (a + b) * 0.5f;
    }
    
    protected static float getDistance(final float x, final float y, final float ax, final float ay, final float bx, final float by) {
        final float mx = midPoint(ax, bx);
        final float my = midPoint(ay, by);
        final float dx = bx - ax;
        final float dy = by - ay;
        final float nx = -dy;
        final float ny = dx;
        return getDistance2Line(x, y, mx, my, mx + nx, my + ny);
    }
    
    protected static float getDistance2Line(final float x, final float y, final float ax, final float ay, final float bx, final float by) {
        final float dx = bx - ax;
        final float dy = by - ay;
        float v = (x - ax) * dx + (y - ay) * dy;
        v /= dx * dx + dy * dy;
        final float ox = ax + dx * v;
        final float oy = ay + dy * v;
        return Line.dist2(x, y, ox, oy);
    }
}

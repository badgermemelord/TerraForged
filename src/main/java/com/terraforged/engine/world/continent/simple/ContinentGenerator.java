// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.continent.simple;

import com.terraforged.engine.Seed;
import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.settings.WorldSettings;
import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.continent.SimpleContinent;
import com.terraforged.engine.world.heightmap.ControlPoints;
import com.terraforged.engine.world.rivermap.LegacyRiverCache;
import com.terraforged.engine.world.rivermap.RiverCache;
import com.terraforged.engine.world.rivermap.Rivermap;
import com.terraforged.noise.Source;
import com.terraforged.noise.domain.Domain;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.func.EdgeFunc;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

public abstract class ContinentGenerator implements SimpleContinent
{
    protected final int seed;
    protected final float frequency;
    protected final int continentScale;
    private final DistanceFunc distanceFunc;
    private final ControlPoints controlPoints;
    private final float clampMin;
    private final float clampMax;
    private final float clampRange;
    private final float offsetAlpha;
    protected final Domain warp;
    protected final Module shape;
    protected final RiverCache cache;
    
    public ContinentGenerator(final Seed seed, final GeneratorContext context) {
        final WorldSettings settings = context.settings.world;
        final int tectonicScale = settings.continent.continentScale * 4;
        this.continentScale = settings.continent.continentScale / 2;
        this.seed = seed.next();
        this.distanceFunc = settings.continent.continentShape;
        this.controlPoints = new ControlPoints(settings.controlPoints);
        this.frequency = 1.0f / tectonicScale;
        this.clampMin = 0.2f;
        this.clampMax = 1.0f;
        this.clampRange = this.clampMax - this.clampMin;
        this.offsetAlpha = context.settings.world.continent.continentJitter;
        this.warp = Domain.warp(Source.PERLIN, seed.next(), 20, 2, 20.0).warp(Domain.warp(Source.SIMPLEX, seed.next(), this.continentScale, 3, this.continentScale));
        this.shape = Source.simplex(seed.next(), settings.continent.continentScale * 2, 1).bias(0.65).clamp(0.0, 1.0);
        this.cache = new LegacyRiverCache(new SimpleRiverGenerator(this, context));
    }
    
    @Override
    public Rivermap getRivermap(final int x, final int y) {
        return this.cache.getRivers(x, y);
    }
    
    @Override
    public float getValue(final float x, final float y) {
        final Cell cell = new Cell();
        this.apply(cell, x, y);
        return cell.continentEdge;
    }
    
    @Override
    public void apply(final Cell cell, final float x, final float y) {
        final float ox = this.warp.getOffsetX(x, y);
        final float oz = this.warp.getOffsetY(x, y);
        float px = x + ox;
        float py = y + oz;
        px *= this.frequency;
        py *= this.frequency;
        final int xr = NoiseUtil.floor(px);
        final int yr = NoiseUtil.floor(py);
        int cellX = xr;
        int cellY = yr;
        float centerX = px;
        float centerY = py;
        float edgeDistance = 999999.0f;
        float edgeDistance2 = 999999.0f;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                final int cx = xr + dx;
                final int cy = yr + dy;
                final Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                final float cxf = cx + vec.x * this.offsetAlpha;
                final float cyf = cy + vec.y * this.offsetAlpha;
                final float distance = this.distanceFunc.apply(cxf - px, cyf - py);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                    centerX = cxf;
                    centerY = cyf;
                    cellX = cx;
                    cellY = cy;
                }
                else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
        cell.continentId = this.cellIdentity(this.seed, cellX, cellY);
        cell.continentEdge = this.cellEdgeValue(edgeDistance, edgeDistance2);
        cell.continentX = (int)(centerX / this.frequency);
        cell.continentZ = (int)(centerY / this.frequency);
        cell.continentEdge *= this.getShape(x, y, cell.continentEdge);
    }
    
    @Override
    public final float getEdgeValue(final float x, final float y) {
        final float ox = this.warp.getOffsetX(x, y);
        final float oz = this.warp.getOffsetY(x, y);
        float px = x + ox;
        float py = y + oz;
        px *= this.frequency;
        py *= this.frequency;
        final int xr = NoiseUtil.floor(px);
        final int yr = NoiseUtil.floor(py);
        float edgeDistance = 999999.0f;
        float edgeDistance2 = 999999.0f;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                final int cx = xr + dx;
                final int cy = yr + dy;
                final Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                final float cxf = cx + vec.x * this.offsetAlpha;
                final float cyf = cy + vec.y * this.offsetAlpha;
                final float distance = this.distanceFunc.apply(cxf - px, cyf - py);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                }
                else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
        final float edgeValue = this.cellEdgeValue(edgeDistance, edgeDistance2);
        final float shapeNoise = this.getShape(x, y, edgeValue);
        return edgeValue * shapeNoise;
    }
    
    @Override
    public long getNearestCenter(final float x, final float z) {
        final float ox = this.warp.getOffsetX(x, z);
        final float oz = this.warp.getOffsetY(x, z);
        float px = x + ox;
        float py = z + oz;
        px *= this.frequency;
        py *= this.frequency;
        float centerX = px;
        float centerY = py;
        final int xr = NoiseUtil.floor(px);
        final int yr = NoiseUtil.floor(py);
        float edgeDistance = 999999.0f;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                final int cx = xr + dx;
                final int cy = yr + dy;
                final Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                final float cxf = cx + vec.x * this.offsetAlpha;
                final float cyf = cy + vec.y * this.offsetAlpha;
                final float distance = this.distanceFunc.apply(cxf - px, cyf - py);
                if (distance < edgeDistance) {
                    edgeDistance = distance;
                    centerX = cxf;
                    centerY = cyf;
                }
            }
        }
        final int conX = (int)(centerX / this.frequency);
        final int conZ = (int)(centerY / this.frequency);
        return PosUtil.pack(conX, conZ);
    }
    
    @Override
    public float getDistanceToOcean(final int cx, final int cz, final float dx, final float dz) {
        float high = this.getDistanceToEdge(cx, cz, dx, dz);
        float low = 0.0f;
        for (int i = 0; i < 50; ++i) {
            final float mid = (low + high) / 2.0f;
            final float x = cx + dx * mid;
            final float z = cz + dz * mid;
            final float edge = this.getEdgeValue(x, z);
            if (edge > this.controlPoints.shallowOcean) {
                low = mid;
            }
            else {
                high = mid;
            }
            if (high - low < 10.0f) {
                break;
            }
        }
        return high;
    }
    
    @Override
    public float getDistanceToEdge(final int cx, final int cz, final float dx, final float dz) {
        float distance = (float)(this.continentScale * 4);
        for (int i = 0; i < 10; ++i) {
            final float x = cx + dx * distance;
            final float z = cz + dz * distance;
            long centerPos = this.getNearestCenter(x, z);
            int conX = PosUtil.unpackLeft(centerPos);
            int conZ = PosUtil.unpackRight(centerPos);
            distance += distance;
            if (conX != cx || conZ != cz) {
                float low = 0.0f;
                float high = distance;
                for (int j = 0; j < 50; ++j) {
                    final float mid = (low + high) / 2.0f;
                    final float px = cx + dx * mid;
                    final float pz = cz + dz * mid;
                    centerPos = this.getNearestCenter(px, pz);
                    conX = PosUtil.unpackLeft(centerPos);
                    conZ = PosUtil.unpackRight(centerPos);
                    if (conX == cx && conZ == cz) {
                        low = mid;
                    }
                    else {
                        high = mid;
                    }
                    if (high - low < 50.0f) {
                        break;
                    }
                }
                return high;
            }
        }
        return distance;
    }
    
    protected float cellIdentity(final int seed, final int cellX, final int cellY) {
        final float value = NoiseUtil.valCoord2D(seed, cellX, cellY);
        return NoiseUtil.map(value, -1.0f, 1.0f, 2.0f);
    }
    
    protected float cellEdgeValue(final float distance, final float distance2) {
        final EdgeFunc edge = EdgeFunc.DISTANCE_2_DIV;
        float value = edge.apply(distance, distance2);
        value = 1.0f - NoiseUtil.map(value, edge.min(), edge.max(), edge.range());
        if (value <= this.clampMin) {
            return 0.0f;
        }
        if (value >= this.clampMax) {
            return 1.0f;
        }
        return (value - this.clampMin) / this.clampRange;
    }
    
    protected float getShape(final float x, final float z, final float edgeValue) {
        if (edgeValue >= this.controlPoints.inland) {
            return 1.0f;
        }
        final float alpha = edgeValue / this.controlPoints.inland;
        return this.shape.getValue(x, z) * alpha;
    }
}

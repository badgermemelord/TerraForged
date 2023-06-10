//
// Source code recreated from a .class file by Quiltflower
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
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;
import com.terraforged.noise.domain.Domain;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.func.EdgeFunc;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

public abstract class ContinentGenerator implements SimpleContinent {
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

    public ContinentGenerator(Seed seed, GeneratorContext context) {
        WorldSettings settings = context.settings.world;
        int tectonicScale = settings.continent.continentScale * 4;
        this.continentScale = settings.continent.continentScale / 2;
        this.seed = seed.next();
        this.distanceFunc = settings.continent.continentShape;
        this.controlPoints = new ControlPoints(settings.controlPoints);
        this.frequency = 1.0F / (float)tectonicScale;
        this.clampMin = 0.2F;
        this.clampMax = 1.0F;
        this.clampRange = this.clampMax - this.clampMin;
        this.offsetAlpha = context.settings.world.continent.continentJitter;
        this.warp = Domain.warp(Source.PERLIN, seed.next(), 20, 2, 20.0)
                .warp(Domain.warp(Source.SIMPLEX, seed.next(), this.continentScale, 3, (double)this.continentScale));
        this.shape = Source.simplex(seed.next(), settings.continent.continentScale * 2, 1).bias(0.65).clamp(0.0, 1.0);
        this.cache = new LegacyRiverCache(new SimpleRiverGenerator(this, context));
    }

    public Rivermap getRivermap(int x, int y) {
        return this.cache.getRivers(x, y);
    }

    public float getValue(float x, float y) {
        Cell cell = new Cell();
        this.apply(cell, x, y);
        return cell.continentEdge;
    }

    public void apply(Cell cell, float x, float y) {
        float ox = this.warp.getOffsetX(x, y);
        float oz = this.warp.getOffsetY(x, y);
        float px = x + ox;
        float py = y + oz;
        px *= this.frequency;
        py *= this.frequency;
        int xr = NoiseUtil.floor(px);
        int yr = NoiseUtil.floor(py);
        int cellX = xr;
        int cellY = yr;
        float centerX = px;
        float centerY = py;
        float edgeDistance = 999999.0F;
        float edgeDistance2 = 999999.0F;

        for(int dy = -1; dy <= 1; ++dy) {
            for(int dx = -1; dx <= 1; ++dx) {
                int cx = xr + dx;
                int cy = yr + dy;
                Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                float cxf = (float)cx + vec.x * this.offsetAlpha;
                float cyf = (float)cy + vec.y * this.offsetAlpha;
                float distance = this.distanceFunc.apply(cxf - px, cyf - py);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                    centerX = cxf;
                    centerY = cyf;
                    cellX = cx;
                    cellY = cy;
                } else if (distance < edgeDistance2) {
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

    public final float getEdgeValue(float x, float y) {
        float ox = this.warp.getOffsetX(x, y);
        float oz = this.warp.getOffsetY(x, y);
        float px = x + ox;
        float py = y + oz;
        px *= this.frequency;
        py *= this.frequency;
        int xr = NoiseUtil.floor(px);
        int yr = NoiseUtil.floor(py);
        float edgeDistance = 999999.0F;
        float edgeDistance2 = 999999.0F;

        for(int dy = -1; dy <= 1; ++dy) {
            for(int dx = -1; dx <= 1; ++dx) {
                int cx = xr + dx;
                int cy = yr + dy;
                Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                float cxf = (float)cx + vec.x * this.offsetAlpha;
                float cyf = (float)cy + vec.y * this.offsetAlpha;
                float distance = this.distanceFunc.apply(cxf - px, cyf - py);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                } else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }

        float edgeValue = this.cellEdgeValue(edgeDistance, edgeDistance2);
        float shapeNoise = this.getShape(x, y, edgeValue);
        return edgeValue * shapeNoise;
    }

    public long getNearestCenter(float x, float z) {
        float ox = this.warp.getOffsetX(x, z);
        float oz = this.warp.getOffsetY(x, z);
        float px = x + ox;
        float py = z + oz;
        px *= this.frequency;
        py *= this.frequency;
        float centerX = px;
        float centerY = py;
        int xr = NoiseUtil.floor(px);
        int yr = NoiseUtil.floor(py);
        float edgeDistance = 999999.0F;

        for(int dy = -1; dy <= 1; ++dy) {
            for(int dx = -1; dx <= 1; ++dx) {
                int cx = xr + dx;
                int cy = yr + dy;
                Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                float cxf = (float)cx + vec.x * this.offsetAlpha;
                float cyf = (float)cy + vec.y * this.offsetAlpha;
                float distance = this.distanceFunc.apply(cxf - px, cyf - py);
                if (distance < edgeDistance) {
                    edgeDistance = distance;
                    centerX = cxf;
                    centerY = cyf;
                }
            }
        }

        int conX = (int)(centerX / this.frequency);
        int conZ = (int)(centerY / this.frequency);
        return PosUtil.pack(conX, conZ);
    }

    public float getDistanceToOcean(int cx, int cz, float dx, float dz) {
        float high = this.getDistanceToEdge(cx, cz, dx, dz);
        float low = 0.0F;

        for(int i = 0; i < 50; ++i) {
            float mid = (low + high) / 2.0F;
            float x = (float)cx + dx * mid;
            float z = (float)cz + dz * mid;
            float edge = this.getEdgeValue(x, z);
            if (edge > this.controlPoints.shallowOcean) {
                low = mid;
            } else {
                high = mid;
            }

            if (high - low < 10.0F) {
                break;
            }
        }

        return high;
    }

    public float getDistanceToEdge(int cx, int cz, float dx, float dz) {
        float distance = (float)(this.continentScale * 4);

        for(int i = 0; i < 10; ++i) {
            float x = (float)cx + dx * distance;
            float z = (float)cz + dz * distance;
            long centerPos = this.getNearestCenter(x, z);
            int conX = PosUtil.unpackLeft(centerPos);
            int conZ = PosUtil.unpackRight(centerPos);
            distance += distance;
            if (conX != cx || conZ != cz) {
                float low = 0.0F;
                float high = distance;

                for(int j = 0; j < 50; ++j) {
                    float mid = (low + high) / 2.0F;
                    float px = (float)cx + dx * mid;
                    float pz = (float)cz + dz * mid;
                    centerPos = this.getNearestCenter(px, pz);
                    conX = PosUtil.unpackLeft(centerPos);
                    conZ = PosUtil.unpackRight(centerPos);
                    if (conX == cx && conZ == cz) {
                        low = mid;
                    } else {
                        high = mid;
                    }

                    if (high - low < 50.0F) {
                        break;
                    }
                }

                return high;
            }
        }

        return distance;
    }

    protected float cellIdentity(int seed, int cellX, int cellY) {
        float value = NoiseUtil.valCoord2D(seed, cellX, cellY);
        return NoiseUtil.map(value, -1.0F, 1.0F, 2.0F);
    }

    protected float cellEdgeValue(float distance, float distance2) {
        EdgeFunc edge = EdgeFunc.DISTANCE_2_DIV;
        float value = edge.apply(distance, distance2);
        value = 1.0F - NoiseUtil.map(value, edge.min(), edge.max(), edge.range());
        if (value <= this.clampMin) {
            return 0.0F;
        } else {
            return value >= this.clampMax ? 1.0F : (value - this.clampMin) / this.clampRange;
        }
    }

    protected float getShape(float x, float z, float edgeValue) {
        if (edgeValue >= this.controlPoints.inland) {
            return 1.0F;
        } else {
            float alpha = edgeValue / this.controlPoints.inland;
            return this.shape.getValue(x, z) * alpha;
        }
    }
}

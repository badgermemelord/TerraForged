// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.continent.advanced;

import com.terraforged.engine.Seed;
import com.terraforged.engine.settings.WorldSettings;
import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.continent.SimpleContinent;
import com.terraforged.engine.world.continent.simple.SimpleRiverGenerator;
import com.terraforged.engine.world.heightmap.ControlPoints;
import com.terraforged.engine.world.rivermap.RiverCache;
import com.terraforged.noise.util.NoiseUtil;

public abstract class AbstractContinent implements SimpleContinent
{
    protected final int seed;
    protected final int skippingSeed;
    protected final int continentScale;
    protected final float jitter;
    protected final boolean hasSkipping;
    protected final float skipThreshold;
    protected final RiverCache riverCache;
    protected final ControlPoints controlPoints;
    
    public AbstractContinent(final Seed seed, final GeneratorContext context) {
        final WorldSettings settings = context.settings.world;
        this.seed = seed.next();
        this.skippingSeed = seed.next();
        this.continentScale = settings.continent.continentScale;
        this.jitter = settings.continent.continentJitter;
        this.skipThreshold = settings.continent.continentSkipping;
        this.hasSkipping = (this.skipThreshold > 0.0f);
        this.controlPoints = new ControlPoints(settings.controlPoints);
        this.riverCache = new RiverCache(new SimpleRiverGenerator(this, context));
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
    
    protected boolean isDefaultContinent(final int cellX, final int cellY) {
        return cellX == 0 && cellY == 0;
    }
    
    protected boolean shouldSkip(final int cellX, final int cellY) {
        if (this.hasSkipping && !this.isDefaultContinent(cellX, cellY)) {
            final float skipValue = getCellValue(this.skippingSeed, cellX, cellY);
            return skipValue < this.skipThreshold;
        }
        return false;
    }
    
    protected static float getCellValue(final int seed, final int cellX, final int cellY) {
        return 0.5f + NoiseUtil.valCoord2D(seed, cellX, cellY) * 0.5f;
    }
}

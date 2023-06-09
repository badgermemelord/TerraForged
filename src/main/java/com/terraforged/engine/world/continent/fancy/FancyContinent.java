// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.continent.fancy;

import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.heightmap.ControlPoints;
import com.terraforged.engine.world.rivermap.RiverGenerator;
import com.terraforged.engine.world.rivermap.Rivermap;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;
import com.terraforged.noise.Module;


import java.util.Random;

public class FancyContinent implements Module, RiverGenerator
{
    private final Island[] islands;
    private final FancyRiverGenerator riverGenerator;
    
    public FancyContinent(final int seed, final int nodes, final float radius, final GeneratorContext context, final FancyContinentGenerator continent) {
        final ControlPoints controlPoints = new ControlPoints(context.settings.world.controlPoints);
        this.islands = generateIslands(controlPoints, 3, nodes, radius, new Random(seed));
        this.riverGenerator = new FancyRiverGenerator(continent, context);
    }
    
    @Override
    public float getValue(final float x, final float y) {
        float value = 0.0f;
        for (final Island island : this.islands) {
            final float v = island.getEdgeValue(x, y);
            value = Math.max(v, value);
        }
        return process(value);
    }
    
    public Island getMain() {
        return this.islands[0];
    }
    
    public Island[] getIslands() {
        return this.islands;
    }
    
    public long getMin() {
        float x = Float.MAX_VALUE;
        float z = Float.MAX_VALUE;
        for (final Island island : this.islands) {
            x = Math.min(x, island.getMin().x);
            z = Math.min(z, island.getMin().y);
        }
        return PosUtil.packf(x, z);
    }
    
    public long getMax() {
        float x = Float.MIN_VALUE;
        float z = Float.MIN_VALUE;
        for (final Island island : this.islands) {
            x = Math.max(x, island.getMin().x);
            z = Math.max(z, island.getMin().y);
        }
        return PosUtil.packf(x, z);
    }
    
    public float getLandValue(final float x, final float y) {
        float value = 0.0f;
        for (final Island island : this.islands) {
            final float v = island.getLandValue(x, y);
            value = Math.max(v, value);
        }
        return value;
    }
    
    public long getValueId(final float x, final float y) {
        int id = -1;
        float value = 0.0f;
        for (final Island island : this.islands) {
            final float v = island.getEdgeValue(x, y);
            if (v > value) {
                value = v;
                id = island.getId();
            }
            value = Math.max(v, value);
        }
        return PosUtil.packMix(id, value);
    }
    
    @Override
    public Rivermap generateRivers(final int x, final int z, final long id) {
        return this.riverGenerator.generateRivers(x, z, id);
    }
    
    private static float process(final float value) {
        return value;
    }
    
    private static Island[] generateIslands(final ControlPoints controlPoints, final int islandCount, final int nodeCount, final float radius, final Random random) {
        final int dirs = 4;
        final Island main = generate(0, controlPoints, nodeCount, radius, random);
        final Island[] islands = new Island[1 + islandCount * dirs];
        islands[0] = main;
        int i = 1;
        final float yawStep = 1.0f / dirs * 6.2831855f;
        for (int dir = 0; dir < dirs; ++dir) {
            Island previous = main;
            int nCount = Math.max(2, nodeCount - 1);
            float r = radius * 0.5f;
            final float yaw = yawStep * dir + random.nextFloat() * yawStep;
            for (int island = 0; island < islandCount; ++island) {
                final Island next = generate(i, controlPoints, nCount, r, random);
                final float y = yaw + nextFloat(random, -0.2f, 0.2f);
                final float distance = previous.radius();
                final float dx = NoiseUtil.sin(y * 6.2831855f) * distance;
                final float dz = NoiseUtil.cos(y * 6.2831855f) * distance;
                final float ox = previous.getCenter().x + dx;
                final float oy = previous.getCenter().y + dz;
                next.translate(new Vec2f(ox, oy));
                nCount = Math.max(2, nCount - 1);
                r *= 0.8f;
                islands[i++] = next;
                previous = next;
            }
        }
        return islands;
    }
    
    private static Island generate(final int id, final ControlPoints controlPoints, final int nodes, final float radius, final Random random) {
        final float minScale = 0.75f;
        final float maxScale = 2.5f;
        final float minLen = radius * 1.5f;
        final float maxLen = radius * 3.5f;
        final float maxYaw = 1.5707964f;
        final float minYaw = -maxYaw;
        final Segment[] segments = new Segment[nodes - 1];
        Vec2f pointA = new Vec2f(0.0f, 0.0f);
        float scaleA = nextFloat(random, minScale, maxScale);
        float previousYaw = nextFloat(random, 0.0f, 6.2831855f);
        for (int i = 0; i < segments.length; ++i) {
            final float length = nextFloat(random, minLen, maxLen);
            final float yaw = previousYaw + nextFloat(random, minYaw, maxYaw);
            final float dx = NoiseUtil.sin(yaw) * length;
            final float dz = NoiseUtil.cos(yaw) * length;
            final Vec2f pointB = new Vec2f(pointA.x + dx, pointA.y + dz);
            final float scaleB = nextFloat(random, minScale, maxScale);
            segments[i] = new Segment(pointA, pointB, scaleA, scaleB);
            previousYaw = yaw;
            pointA = pointB;
            scaleA = scaleB;
        }
        return new Island(id, segments, controlPoints, radius * 3.0f, radius * 1.25f, radius, radius * 0.975f);
    }
    
    public static float nextFloat(final Random random, final float min, final float max) {
        return min + random.nextFloat() * (max - min);
    }
}

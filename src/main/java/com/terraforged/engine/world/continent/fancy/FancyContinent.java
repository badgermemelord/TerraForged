//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.continent.fancy;

import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.heightmap.ControlPoints;
import com.terraforged.engine.world.rivermap.RiverGenerator;
import com.terraforged.engine.world.rivermap.Rivermap;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;
import java.util.Random;

public class FancyContinent implements Module, RiverGenerator {
    private final Island[] islands;
    private final FancyRiverGenerator riverGenerator;

    public FancyContinent(int seed, int nodes, float radius, GeneratorContext context, FancyContinentGenerator continent) {
        ControlPoints controlPoints = new ControlPoints(context.settings.world.controlPoints);
        this.islands = generateIslands(controlPoints, 3, nodes, radius, new Random((long)seed));
        this.riverGenerator = new FancyRiverGenerator(continent, context);
    }

    public float getValue(float x, float y) {
        float value = 0.0F;

        for(Island island : this.islands) {
            float v = island.getEdgeValue(x, y);
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

        for(Island island : this.islands) {
            x = Math.min(x, island.getMin().x);
            z = Math.min(z, island.getMin().y);
        }

        return PosUtil.packf(x, z);
    }

    public long getMax() {
        float x = Float.MIN_VALUE;
        float z = Float.MIN_VALUE;

        for(Island island : this.islands) {
            x = Math.max(x, island.getMin().x);
            z = Math.max(z, island.getMin().y);
        }

        return PosUtil.packf(x, z);
    }

    public float getLandValue(float x, float y) {
        float value = 0.0F;

        for(Island island : this.islands) {
            float v = island.getLandValue(x, y);
            value = Math.max(v, value);
        }

        return value;
    }

    public long getValueId(float x, float y) {
        int id = -1;
        float value = 0.0F;

        for(Island island : this.islands) {
            float v = island.getEdgeValue(x, y);
            if (v > value) {
                value = v;
                id = island.getId();
            }

            value = Math.max(v, value);
        }

        return PosUtil.packMix(id, value);
    }

    public Rivermap generateRivers(int x, int z, long id) {
        return this.riverGenerator.generateRivers(x, z, id);
    }

    private static float process(float value) {
        return value;
    }

    private static Island[] generateIslands(ControlPoints controlPoints, int islandCount, int nodeCount, float radius, Random random) {
        int dirs = 4;
        Island main = generate(0, controlPoints, nodeCount, radius, random);
        Island[] islands = new Island[1 + islandCount * dirs];
        islands[0] = main;
        int i = 1;
        float yawStep = 1.0F / (float)dirs * (float) (Math.PI * 2);

        for(int dir = 0; dir < dirs; ++dir) {
            Island previous = main;
            int nCount = Math.max(2, nodeCount - 1);
            float r = radius * 0.5F;
            float yaw = yawStep * (float)dir + random.nextFloat() * yawStep;

            for(int island = 0; island < islandCount; ++island) {
                Island next = generate(i, controlPoints, nCount, r, random);
                float y = yaw + nextFloat(random, -0.2F, 0.2F);
                float distance = previous.radius();
                float dx = NoiseUtil.sin(y * (float) (Math.PI * 2)) * distance;
                float dz = NoiseUtil.cos(y * (float) (Math.PI * 2)) * distance;
                float ox = previous.getCenter().x + dx;
                float oy = previous.getCenter().y + dz;
                next.translate(new Vec2f(ox, oy));
                nCount = Math.max(2, nCount - 1);
                r *= 0.8F;
                islands[i++] = next;
                previous = next;
            }
        }

        return islands;
    }

    private static Island generate(int id, ControlPoints controlPoints, int nodes, float radius, Random random) {
        float minScale = 0.75F;
        float maxScale = 2.5F;
        float minLen = radius * 1.5F;
        float maxLen = radius * 3.5F;
        float maxYaw = (float) (Math.PI / 2);
        float minYaw = -maxYaw;
        Segment[] segments = new Segment[nodes - 1];
        Vec2f pointA = new Vec2f(0.0F, 0.0F);
        float scaleA = nextFloat(random, minScale, maxScale);
        float previousYaw = nextFloat(random, 0.0F, (float) (Math.PI * 2));

        for(int i = 0; i < segments.length; ++i) {
            float length = nextFloat(random, minLen, maxLen);
            float yaw = previousYaw + nextFloat(random, minYaw, maxYaw);
            float dx = NoiseUtil.sin(yaw) * length;
            float dz = NoiseUtil.cos(yaw) * length;
            Vec2f pointB = new Vec2f(pointA.x + dx, pointA.y + dz);
            float scaleB = nextFloat(random, minScale, maxScale);
            segments[i] = new Segment(pointA, pointB, scaleA, scaleB);
            previousYaw = yaw;
            pointA = pointB;
            scaleA = scaleB;
        }

        return new Island(id, segments, controlPoints, radius * 3.0F, radius * 1.25F, radius, radius * 0.975F);
    }

    public static float nextFloat(Random random, float min, float max) {
        return min + random.nextFloat() * (max - min);
    }
}

//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.continent.fancy;

import com.terraforged.engine.util.Variance;
import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.rivermap.Rivermap;
import com.terraforged.engine.world.rivermap.gen.GenWarp;
import com.terraforged.engine.world.rivermap.river.BaseRiverGenerator;
import com.terraforged.engine.world.rivermap.river.Network;
import com.terraforged.engine.world.rivermap.river.River;
import com.terraforged.engine.world.rivermap.river.RiverCarver;
import com.terraforged.engine.world.rivermap.river.RiverConfig;
import com.terraforged.engine.world.rivermap.river.RiverWarp;
import com.terraforged.engine.world.rivermap.river.Network.Builder;
import com.terraforged.engine.world.rivermap.river.RiverCarver.Settings;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FancyRiverGenerator extends BaseRiverGenerator<FancyContinentGenerator> {
    private static final float END_VALUE = 0.1F;
    private static final Variance MAIN_PADDING = Variance.of(0.05, 0.1);
    private static final Variance MAIN_JITTER = Variance.of(-0.2, 0.4);
    private final float freq;

    public FancyRiverGenerator(FancyContinentGenerator continent, GeneratorContext context) {
        super(continent, context);
        this.freq = 1.0F / (float)context.settings.world.continent.continentScale;
    }

    public Rivermap generateRivers(int x, int z, long id) {
        Random random = new Random(id + (long)this.seed);
        GenWarp warp = new GenWarp((int)id, this.continentScale);
        List<Network> networks = new ArrayList(32);
        List<Builder> roots = new ArrayList(16);

        for(Island island : ((FancyContinentGenerator)this.continent).getSource().getIslands()) {
            this.generateRoots(((FancyContinentGenerator)this.continent).getSource(), island, random, warp, roots);

            for(Builder river : roots) {
                networks.add(river.build());
            }

            roots.clear();
        }

        return new Rivermap(x, z, (Network[])networks.toArray(Network.NETWORKS), warp);
    }

    private void generateRoots(FancyContinent continent, Island island, Random random, GenWarp warp, List<Builder> roots) {
        Segment[] segments = island.getSegments();
        int lineCount = Math.max(1, 8 - island.getId());
        int endCount = Math.max(4, 12 - island.getId());

        for(int i = 0; i < segments.length; ++i) {
            boolean end = i == 0 || i == segments.length - 1;
            Segment segment = segments[i];
            int riverCount = end ? lineCount - 1 : lineCount;
            this.collectSegmentRoots(continent, island, segment, riverCount, random, warp, roots);
        }

        Segment first = segments[0];
        this.collectPointRoots(continent, island, first.a, first.scaleA, endCount, random, warp, roots);
        Segment last = segments[segments.length - 1];
        this.collectPointRoots(continent, island, last.b, last.scaleB, endCount, random, warp, roots);
    }

    private void collectSegmentRoots(FancyContinent continent, Island island, Segment segment, int count, Random random, GenWarp warp, List<Builder> roots) {
        float dx = segment.dx;
        float dy = segment.dy;
        float nx = dy / segment.length;
        float ny = -dx / segment.length;
        float stepSize = 1.0F / (float)(count + 2);

        for(int i = 0; i < count; ++i) {
            float progress = stepSize + stepSize * (float)i;
            if (progress > 1.0F) {
                return;
            }

            float startX = segment.a.x + dx * progress;
            float startZ = segment.a.y + dy * progress;
            float radiusScale = NoiseUtil.lerp(segment.scaleA, segment.scaleB, progress);
            float radius = island.coast() * radiusScale;
            int dir = random.nextBoolean() ? -1 : 1;
            float dirX = nx * (float)dir + MAIN_JITTER.next(random);
            float dirZ = ny * (float)dir + MAIN_JITTER.next(random);
            float scale = getExtendScale(island.getId(), startX, startZ, dirX, dirZ, radius, continent);
            if (scale != 0.0F) {
                float startPad = MAIN_PADDING.next(random);
                float x1 = startX + (float)dir * dirX * radius * startPad;
                float y1 = startZ + (float)dir * dirZ * radius * startPad;
                float x2 = startX + dirX * radius * scale;
                float y2 = startZ + dirZ * radius * scale;
                this.addRoot(x1, y1, x2, y2, this.main, random, warp, roots);
            }
        }
    }

    private void collectPointRoots(
            FancyContinent continent, Island island, Vec2f vec, float radiusScale, int count, Random random, GenWarp warp, List<Builder> roots
    ) {
        float yawStep = (float) (Math.PI * 2) / (float)count;
        float radius = island.coast() * radiusScale;

        for(int i = 0; i < count; ++i) {
            float yaw = yawStep * (float)i;
            float dx = NoiseUtil.cos(yaw);
            float dz = NoiseUtil.sin(yaw);
            float scale = getExtendScale(island.getId(), vec.x, vec.y, dx, dz, radius, continent);
            if (scale != 0.0F) {
                float startPad = MAIN_PADDING.next(random);
                float startX = vec.x + dx * startPad * radius;
                float startZ = vec.y + dz * startPad * radius;
                float endX = vec.x + dx * radius * scale;
                float endZ = vec.y + dz * radius * scale;
                if (!(continent.getValue(endX, endZ) > 0.1F)) {
                    this.addRoot(startX, startZ, endX, endZ, this.main, random, warp, roots);
                }
            }
        }
    }

    private void addRoot(float x1, float z1, float x2, float z2, RiverConfig config, Random random, GenWarp warp, List<Builder> roots) {
        River river = new River(x1 / this.freq, z1 / this.freq, x2 / this.freq, z2 / this.freq);
        if (!this.riverOverlaps(river, null, roots)) {
            Settings settings = creatSettings(random);
            settings.fadeIn = config.fade;
            settings.valleySize = 275.0F * River.FORK_VALLEY.next(random);
            RiverWarp riverWarp = RiverWarp.create(0.1F, 0.85F, random);
            RiverCarver carver = new RiverCarver(river, riverWarp, config, settings, this.levels);
            Builder network = Network.builder(carver);
            roots.add(network);
            this.generateForks(network, River.FORK_SPACING, this.fork, random, warp, roots, 0);
            this.generateWetlands(network, random);
        }
    }

    private static float getExtendScale(int islandId, float startX, float startZ, float dx, float dz, float radius, FancyContinent continent) {
        float scale = 1.0F;

        for(int i = 0; i < 25; ++i) {
            float x = startX + dx * radius * scale;
            float z = startZ + dz * radius * scale;
            long packed = continent.getValueId(x, z);
            if (PosUtil.unpackLeft(packed) != islandId) {
                return 0.0F;
            }

            if (PosUtil.unpackRightf(packed) < 0.1F) {
                return scale;
            }

            scale += 0.075F;
        }

        return 0.0F;
    }
}

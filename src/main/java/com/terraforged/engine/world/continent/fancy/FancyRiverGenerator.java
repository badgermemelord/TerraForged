// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.continent.fancy;

import com.terraforged.engine.util.Variance;
import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.rivermap.Rivermap;
import com.terraforged.engine.world.rivermap.gen.GenWarp;
import com.terraforged.engine.world.rivermap.river.*;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FancyRiverGenerator extends BaseRiverGenerator<FancyContinentGenerator>
{
    private static final float END_VALUE = 0.1f;
    private static final Variance MAIN_PADDING;
    private static final Variance MAIN_JITTER;
    private final float freq;
    
    public FancyRiverGenerator(final FancyContinentGenerator continent, final GeneratorContext context) {
        super(continent, context);
        this.freq = 1.0f / context.settings.world.continent.continentScale;
    }
    
    @Override
    public Rivermap generateRivers(final int x, final int z, final long id) {
        final Random random = new Random(id + this.seed);
        final GenWarp warp = new GenWarp((int)id, this.continentScale);
        final List<Network> networks = new ArrayList<Network>(32);
        final List<Network.Builder> roots = new ArrayList<Network.Builder>(16);
        for (final Island island : ((FancyContinentGenerator)this.continent).getSource().getIslands()) {
            this.generateRoots(((FancyContinentGenerator)this.continent).getSource(), island, random, warp, roots);
            for (final Network.Builder river : roots) {
                networks.add(river.build());
            }
            roots.clear();
        }
        return new Rivermap(x, z, networks.toArray(Network.NETWORKS), warp);
    }
    
    private void generateRoots(final FancyContinent continent, final Island island, final Random random, final GenWarp warp, final List<Network.Builder> roots) {
        final Segment[] segments = island.getSegments();
        final int lineCount = Math.max(1, 8 - island.getId());
        final int endCount = Math.max(4, 12 - island.getId());
        for (int i = 0; i < segments.length; ++i) {
            final boolean end = i == 0 || i == segments.length - 1;
            final Segment segment = segments[i];
            final int riverCount = end ? (lineCount - 1) : lineCount;
            this.collectSegmentRoots(continent, island, segment, riverCount, random, warp, roots);
        }
        final Segment first = segments[0];
        this.collectPointRoots(continent, island, first.a, first.scaleA, endCount, random, warp, roots);
        final Segment last = segments[segments.length - 1];
        this.collectPointRoots(continent, island, last.b, last.scaleB, endCount, random, warp, roots);
    }
    
    private void collectSegmentRoots(final FancyContinent continent, final Island island, final Segment segment, final int count, final Random random, final GenWarp warp, final List<Network.Builder> roots) {
        final float dx = segment.dx;
        final float dy = segment.dy;
        final float nx = dy / segment.length;
        final float ny = -dx / segment.length;
        final float stepSize = 1.0f / (count + 2);
        for (int i = 0; i < count; ++i) {
            final float progress = stepSize + stepSize * i;
            if (progress > 1.0f) {
                return;
            }
            final float startX = segment.a.x + dx * progress;
            final float startZ = segment.a.y + dy * progress;
            final float radiusScale = NoiseUtil.lerp(segment.scaleA, segment.scaleB, progress);
            final float radius = island.coast() * radiusScale;
            final int dir = random.nextBoolean() ? -1 : 1;
            final float dirX = nx * dir + FancyRiverGenerator.MAIN_JITTER.next(random);
            final float dirZ = ny * dir + FancyRiverGenerator.MAIN_JITTER.next(random);
            final float scale = getExtendScale(island.getId(), startX, startZ, dirX, dirZ, radius, continent);
            if (scale != 0.0f) {
                final float startPad = FancyRiverGenerator.MAIN_PADDING.next(random);
                final float x1 = startX + dir * dirX * radius * startPad;
                final float y1 = startZ + dir * dirZ * radius * startPad;
                final float x2 = startX + dirX * radius * scale;
                final float y2 = startZ + dirZ * radius * scale;
                this.addRoot(x1, y1, x2, y2, this.main, random, warp, roots);
            }
        }
    }
    
    private void collectPointRoots(final FancyContinent continent, final Island island, final Vec2f vec, final float radiusScale, final int count, final Random random, final GenWarp warp, final List<Network.Builder> roots) {
        final float yawStep = 6.2831855f / count;
        final float radius = island.coast() * radiusScale;
        for (int i = 0; i < count; ++i) {
            final float yaw = yawStep * i;
            final float dx = NoiseUtil.cos(yaw);
            final float dz = NoiseUtil.sin(yaw);
            final float scale = getExtendScale(island.getId(), vec.x, vec.y, dx, dz, radius, continent);
            if (scale != 0.0f) {
                final float startPad = FancyRiverGenerator.MAIN_PADDING.next(random);
                final float startX = vec.x + dx * startPad * radius;
                final float startZ = vec.y + dz * startPad * radius;
                final float endX = vec.x + dx * radius * scale;
                final float endZ = vec.y + dz * radius * scale;
                if (continent.getValue(endX, endZ) <= 0.1f) {
                    this.addRoot(startX, startZ, endX, endZ, this.main, random, warp, roots);
                }
            }
        }
    }
    
    private void addRoot(final float x1, final float z1, final float x2, final float z2, final RiverConfig config, final Random random, final GenWarp warp, final List<Network.Builder> roots) {
        final River river = new River(x1 / this.freq, z1 / this.freq, x2 / this.freq, z2 / this.freq);
        if (this.riverOverlaps(river, null, roots)) {
            return;
        }
        final RiverCarver.Settings settings = BaseRiverGenerator.creatSettings(random);
        settings.fadeIn = config.fade;
        settings.valleySize = 275.0f * River.FORK_VALLEY.next(random);
        final RiverWarp riverWarp = RiverWarp.create(0.1f, 0.85f, random);
        final RiverCarver carver = new RiverCarver(river, riverWarp, config, settings, this.levels);
        final Network.Builder network = Network.builder(carver);
        roots.add(network);
        this.generateForks(network, River.FORK_SPACING, this.fork, random, warp, roots, 0);
        this.generateWetlands(network, random);
    }
    
    private static float getExtendScale(final int islandId, final float startX, final float startZ, final float dx, final float dz, final float radius, final FancyContinent continent) {
        float scale = 1.0f;
        for (int i = 0; i < 25; ++i) {
            final float x = startX + dx * radius * scale;
            final float z = startZ + dz * radius * scale;
            final long packed = continent.getValueId(x, z);
            if (PosUtil.unpackLeft(packed) != islandId) {
                return 0.0f;
            }
            if (PosUtil.unpackRightf(packed) < 0.1f) {
                return scale;
            }
            scale += 0.075f;
        }
        return 0.0f;
    }
    
    static {
        MAIN_PADDING = Variance.of(0.05, 0.1);
        MAIN_JITTER = Variance.of(-0.2, 0.4);
    }
}

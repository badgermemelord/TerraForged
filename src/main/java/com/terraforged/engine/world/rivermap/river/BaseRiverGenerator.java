// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.rivermap.river;

import com.terraforged.engine.util.Variance;
import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.continent.Continent;
import com.terraforged.engine.world.heightmap.Levels;
import com.terraforged.engine.world.rivermap.RiverGenerator;
import com.terraforged.engine.world.rivermap.Rivermap;
import com.terraforged.engine.world.rivermap.gen.GenWarp;
import com.terraforged.engine.world.rivermap.lake.Lake;
import com.terraforged.engine.world.rivermap.lake.LakeConfig;
import com.terraforged.engine.world.rivermap.wetland.Wetland;
import com.terraforged.engine.world.rivermap.wetland.WetlandConfig;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public abstract class BaseRiverGenerator<T extends Continent> implements RiverGenerator
{
    protected final int count;
    protected final int continentScale;
    protected final float minEdgeValue;
    protected final int seed;
    protected final LakeConfig lake;
    protected final RiverConfig main;
    protected final RiverConfig fork;
    protected final WetlandConfig wetland;
    protected final T continent;
    protected final Levels levels;
    
    public BaseRiverGenerator(final T continent, final GeneratorContext context) {
        this.continent = continent;
        this.levels = context.levels;
        this.continentScale = context.settings.world.continent.continentScale;
        this.minEdgeValue = context.settings.world.controlPoints.inland;
        this.seed = context.seed.root() + context.settings.rivers.seedOffset;
        this.count = context.settings.rivers.riverCount;
        this.main = RiverConfig.builder(context.levels).bankHeight(context.settings.rivers.mainRivers.minBankHeight, context.settings.rivers.mainRivers.maxBankHeight).bankWidth(context.settings.rivers.mainRivers.bankWidth).bedWidth(context.settings.rivers.mainRivers.bedWidth).bedDepth(context.settings.rivers.mainRivers.bedDepth).fade(context.settings.rivers.mainRivers.fade).length(5000).main(true).order(0).build();
        this.fork = RiverConfig.builder(context.levels).bankHeight(context.settings.rivers.branchRivers.minBankHeight, context.settings.rivers.branchRivers.maxBankHeight).bankWidth(context.settings.rivers.branchRivers.bankWidth).bedWidth(context.settings.rivers.branchRivers.bedWidth).bedDepth(context.settings.rivers.branchRivers.bedDepth).fade(context.settings.rivers.branchRivers.fade).length(4500).order(1).build();
        this.wetland = new WetlandConfig(context.settings.rivers.wetlands);
        this.lake = LakeConfig.of(context.settings.rivers.lakes, context.levels);
    }
    
    @Override
    public Rivermap generateRivers(final int x, final int z, final long id) {
        final Random random = new Random(id + this.seed);
        final GenWarp warp = new GenWarp((int)id, this.continentScale);
        final List<Network.Builder> rivers = this.generateRoots(x, z, random, warp);
        Collections.shuffle(rivers, random);
        for (final Network.Builder root : rivers) {
            this.generateForks(root, River.MAIN_SPACING, this.fork, random, warp, rivers, 0);
        }
        for (final Network.Builder river : rivers) {
            this.generateWetlands(river, random);
        }
        final Network[] networks = rivers.stream().map((Function<? super Object, ?>)Network.Builder::build).toArray(Network[]::new);
        return new Rivermap(x, z, networks, warp);
    }
    
    public List<Network.Builder> generateRoots(final int x, final int z, final Random random, final GenWarp warp) {
        return Collections.emptyList();
    }
    
    public void generateForks(final Network.Builder parent, final Variance spacing, final RiverConfig config, final Random random, final GenWarp warp, final List<Network.Builder> rivers, final int depth) {
        if (depth > 2) {
            return;
        }
        final float length = 0.44f * parent.carver.river.length;
        if (length < 300.0f) {
            return;
        }
        int direction = random.nextBoolean() ? 1 : -1;
        for (float offset = 0.25f; offset < 0.9f; offset += spacing.next(random)) {
            for (boolean attempt = true; attempt; attempt = false) {
                direction = -direction;
                final float parentAngle = parent.carver.river.getAngle();
                final float forkAngle = direction * 6.2831855f * River.FORK_ANGLE.next(random);
                final float angle = parentAngle + forkAngle;
                final float dx = NoiseUtil.sin(angle);
                final float dz = NoiseUtil.cos(angle);
                final long v1 = parent.carver.river.pos(offset);
                final float x1 = PosUtil.unpackLeftf(v1);
                final float z1 = PosUtil.unpackRightf(v1);
                if (this.continent.getEdgeValue(x1, z1) >= this.minEdgeValue) {
                    final float x2 = x1 - dx * length;
                    final float z2 = z1 - dz * length;
                    if (this.continent.getEdgeValue(x2, z2) >= this.minEdgeValue) {
                        final RiverConfig forkConfig = parent.carver.createForkConfig(offset, this.levels);
                        final River river = new River(x2, z2, x1, z1);
                        if (!this.riverOverlaps(river, parent, rivers)) {
                            final float valleyWidth = 275.0f * River.FORK_VALLEY.next(random);
                            final RiverCarver.Settings settings = creatSettings(random);
                            settings.connecting = true;
                            settings.fadeIn = config.fade;
                            settings.valleySize = valleyWidth;
                            final RiverWarp forkWarp = parent.carver.warp.createChild(0.15f, 0.75f, 0.65f, random);
                            final RiverCarver fork = new RiverCarver(river, forkWarp, forkConfig, settings, this.levels);
                            final Network.Builder builder = Network.builder(fork);
                            parent.children.add(builder);
                            this.generateForks(builder, River.FORK_SPACING, config, random, warp, rivers, depth + 1);
                        }
                    }
                }
            }
        }
        this.addLake(parent, random, warp);
    }
    
    public void generateAdditionalLakes(final int x, final int z, final Random random, final List<Network.Builder> roots, final List<RiverCarver> rivers, final List<Lake> lakes) {
        final float size = 150.0f;
        final Variance sizeVariance = Variance.of(1.0, 0.25);
        final Variance angleVariance = Variance.of(1.9900000095367432, 0.019999999552965164);
        final Variance distanceVariance = Variance.of(0.6000000238418579, 0.30000001192092896);
        for (int i = 1; i < roots.size(); ++i) {
            final Network.Builder a = roots.get(i - 1);
            final Network.Builder b = roots.get(i);
            final float angle = 0.0f;
            final float dx = NoiseUtil.sin(angle);
            final float dz = NoiseUtil.cos(angle);
            final float distance = distanceVariance.next(random);
            final float lx = x + dx * a.carver.river.length * distance;
            final float lz = z + dz * a.carver.river.length * distance;
            final float variance = sizeVariance.next(random);
            final Vec2f center = new Vec2f(lx, lz);
            if (!this.lakeOverlaps(center, size, rivers)) {
                lakes.add(new Lake(center, size, variance, this.lake));
            }
        }
    }
    
    public void generateWetlands(final Network.Builder builder, final Random random) {
        final int skip = random.nextInt(this.wetland.skipSize);
        if (skip == 0) {
            final float width = this.wetland.width.next(random);
            final float length = this.wetland.length.next(random);
            final float riverLength = builder.carver.river.length();
            final float startPos = random.nextFloat() * 0.75f;
            final float endPos = startPos + random.nextFloat() * (length / riverLength);
            final long start = builder.carver.river.pos(startPos);
            final long end = builder.carver.river.pos(endPos);
            final float x1 = PosUtil.unpackLeftf(start);
            final float z1 = PosUtil.unpackRightf(start);
            final float x2 = PosUtil.unpackLeftf(end);
            final float z2 = PosUtil.unpackRightf(end);
            builder.wetlands.add(new Wetland(random.nextInt(), new Vec2f(x1, z1), new Vec2f(x2, z2), width, this.levels));
        }
        for (final Network.Builder child : builder.children) {
            this.generateWetlands(child, random);
        }
    }
    
    public void addLake(final Network.Builder branch, final Random random, final GenWarp warp) {
        if (random.nextFloat() <= this.lake.chance) {
            final float lakeSize = this.lake.sizeMin + random.nextFloat() * this.lake.sizeRange;
            final float cx = branch.carver.river.x1;
            final float cz = branch.carver.river.z1;
            if (this.lakeOverlapsOther(cx, cz, lakeSize, branch.lakes)) {
                return;
            }
            branch.lakes.add(new Lake(new Vec2f(cx, cz), lakeSize, 1.0f, this.lake));
        }
    }
    
    public boolean riverOverlaps(final River river, final Network.Builder parent, final List<Network.Builder> rivers) {
        for (final Network.Builder other : rivers) {
            if (other.overlaps(river, parent, 250.0f)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean lakeOverlaps(final Vec2f lake, final float size, final List<RiverCarver> rivers) {
        for (final RiverCarver other : rivers) {
            if (!other.main && other.river.overlaps(lake, size)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean lakeOverlapsOther(final float x, final float z, final float size, final List<Lake> lakes) {
        final float dist2 = size * size;
        for (final Lake other : lakes) {
            if (other.overlaps(x, z, dist2)) {
                return true;
            }
        }
        return false;
    }
    
    public static RiverCarver create(final float x1, final float z1, final float x2, final float z2, final RiverConfig config, final Levels levels, final Random random) {
        final River river = new River(x1, z1, x2, z2);
        final RiverWarp warp = RiverWarp.create(0.35f, random);
        final float valleyWidth = 275.0f * River.MAIN_VALLEY.next(random);
        final RiverCarver.Settings settings = creatSettings(random);
        settings.connecting = false;
        settings.fadeIn = config.fade;
        settings.valleySize = valleyWidth;
        return new RiverCarver(river, warp, config, settings, levels);
    }
    
    public static RiverCarver createFork(final float x1, final float z1, final float x2, final float z2, final float valleyWidth, final RiverConfig config, final Levels levels, final Random random) {
        final River river = new River(x1, z1, x2, z2);
        final RiverWarp warp = RiverWarp.create(0.4f, random);
        final RiverCarver.Settings settings = creatSettings(random);
        settings.connecting = true;
        settings.fadeIn = config.fade;
        settings.valleySize = valleyWidth;
        return new RiverCarver(river, warp, config, settings, levels);
    }
    
    public static RiverCarver.Settings creatSettings(final Random random) {
        final RiverCarver.Settings settings = new RiverCarver.Settings();
        settings.valleyCurve = RiverCarver.getValleyType(random);
        return settings;
    }
}

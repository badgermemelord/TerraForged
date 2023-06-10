//
// Source code recreated from a .class file by Quiltflower
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
import com.terraforged.engine.world.rivermap.river.Network.Builder;
import com.terraforged.engine.world.rivermap.river.RiverCarver.Settings;
import com.terraforged.engine.world.rivermap.wetland.Wetland;
import com.terraforged.engine.world.rivermap.wetland.WetlandConfig;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class BaseRiverGenerator<T extends Continent> implements RiverGenerator {
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

    public BaseRiverGenerator(T continent, GeneratorContext context) {
        this.continent = continent;
        this.levels = context.levels;
        this.continentScale = context.settings.world.continent.continentScale;
        this.minEdgeValue = context.settings.world.controlPoints.inland;
        this.seed = context.seed.root() + context.settings.rivers.seedOffset;
        this.count = context.settings.rivers.riverCount;
        this.main = RiverConfig.builder(context.levels)
                .bankHeight(context.settings.rivers.mainRivers.minBankHeight, context.settings.rivers.mainRivers.maxBankHeight)
                .bankWidth(context.settings.rivers.mainRivers.bankWidth)
                .bedWidth(context.settings.rivers.mainRivers.bedWidth)
                .bedDepth(context.settings.rivers.mainRivers.bedDepth)
                .fade((double)context.settings.rivers.mainRivers.fade)
                .length(5000)
                .main(true)
                .order(0)
                .build();
        this.fork = RiverConfig.builder(context.levels)
                .bankHeight(context.settings.rivers.branchRivers.minBankHeight, context.settings.rivers.branchRivers.maxBankHeight)
                .bankWidth(context.settings.rivers.branchRivers.bankWidth)
                .bedWidth(context.settings.rivers.branchRivers.bedWidth)
                .bedDepth(context.settings.rivers.branchRivers.bedDepth)
                .fade((double)context.settings.rivers.branchRivers.fade)
                .length(4500)
                .order(1)
                .build();
        this.wetland = new WetlandConfig(context.settings.rivers.wetlands);
        this.lake = LakeConfig.of(context.settings.rivers.lakes, context.levels);
    }

    public Rivermap generateRivers(int x, int z, long id) {
        Random random = new Random(id + (long)this.seed);
        GenWarp warp = new GenWarp((int)id, this.continentScale);
        List<Builder> rivers = this.generateRoots(x, z, random, warp);
        Collections.shuffle(rivers, random);

        for(Builder root : rivers) {
            this.generateForks(root, River.MAIN_SPACING, this.fork, random, warp, rivers, 0);
        }

        for(Builder river : rivers) {
            this.generateWetlands(river, random);
        }

        Network[] networks = (Network[])rivers.stream().map(Builder::build).toArray(x$0 -> new Network[x$0]);
        return new Rivermap(x, z, networks, warp);
    }

    public List<Builder> generateRoots(int x, int z, Random random, GenWarp warp) {
        return Collections.emptyList();
    }

    public void generateForks(Builder parent, Variance spacing, RiverConfig config, Random random, GenWarp warp, List<Builder> rivers, int depth) {
        if (depth <= 2) {
            float length = 0.44F * parent.carver.river.length;
            if (!(length < 300.0F)) {
                int direction = random.nextBoolean() ? 1 : -1;

                for(float offset = 0.25F; offset < 0.9F; offset += spacing.next(random)) {
                    for(boolean attempt = true; attempt; attempt = false) {
                        direction = -direction;
                        float parentAngle = parent.carver.river.getAngle();
                        float forkAngle = (float)direction * (float) (Math.PI * 2) * River.FORK_ANGLE.next(random);
                        float angle = parentAngle + forkAngle;
                        float dx = NoiseUtil.sin(angle);
                        float dz = NoiseUtil.cos(angle);
                        long v1 = parent.carver.river.pos(offset);
                        float x1 = PosUtil.unpackLeftf(v1);
                        float z1 = PosUtil.unpackRightf(v1);
                        if (!(this.continent.getEdgeValue(x1, z1) < this.minEdgeValue)) {
                            float x2 = x1 - dx * length;
                            float z2 = z1 - dz * length;
                            if (!(this.continent.getEdgeValue(x2, z2) < this.minEdgeValue)) {
                                RiverConfig forkConfig = parent.carver.createForkConfig(offset, this.levels);
                                River river = new River(x2, z2, x1, z1);
                                if (!this.riverOverlaps(river, parent, rivers)) {
                                    float valleyWidth = 275.0F * River.FORK_VALLEY.next(random);
                                    Settings settings = creatSettings(random);
                                    settings.connecting = true;
                                    settings.fadeIn = config.fade;
                                    settings.valleySize = valleyWidth;
                                    RiverWarp forkWarp = parent.carver.warp.createChild(0.15F, 0.75F, 0.65F, random);
                                    RiverCarver fork = new RiverCarver(river, forkWarp, forkConfig, settings, this.levels);
                                    Builder builder = Network.builder(fork);
                                    parent.children.add(builder);
                                    this.generateForks(builder, River.FORK_SPACING, config, random, warp, rivers, depth + 1);
                                }
                            }
                        }
                    }
                }

                this.addLake(parent, random, warp);
            }
        }
    }

    public void generateAdditionalLakes(int x, int z, Random random, List<Builder> roots, List<RiverCarver> rivers, List<Lake> lakes) {
        float size = 150.0F;
        Variance sizeVariance = Variance.of(1.0, 0.25);
        Variance angleVariance = Variance.of(1.99F, 0.02F);
        Variance distanceVariance = Variance.of(0.6F, 0.3F);

        for(int i = 1; i < roots.size(); ++i) {
            Builder a = (Builder)roots.get(i - 1);
            Builder b = (Builder)roots.get(i);
            float angle = 0.0F;
            float dx = NoiseUtil.sin(angle);
            float dz = NoiseUtil.cos(angle);
            float distance = distanceVariance.next(random);
            float lx = (float)x + dx * a.carver.river.length * distance;
            float lz = (float)z + dz * a.carver.river.length * distance;
            float variance = sizeVariance.next(random);
            Vec2f center = new Vec2f(lx, lz);
            if (!this.lakeOverlaps(center, size, rivers)) {
                lakes.add(new Lake(center, size, variance, this.lake));
            }
        }
    }

    public void generateWetlands(Builder builder, Random random) {
        int skip = random.nextInt(this.wetland.skipSize);
        if (skip == 0) {
            float width = this.wetland.width.next(random);
            float length = this.wetland.length.next(random);
            float riverLength = builder.carver.river.length();
            float startPos = random.nextFloat() * 0.75F;
            float endPos = startPos + random.nextFloat() * (length / riverLength);
            long start = builder.carver.river.pos(startPos);
            long end = builder.carver.river.pos(endPos);
            float x1 = PosUtil.unpackLeftf(start);
            float z1 = PosUtil.unpackRightf(start);
            float x2 = PosUtil.unpackLeftf(end);
            float z2 = PosUtil.unpackRightf(end);
            builder.wetlands.add(new Wetland(random.nextInt(), new Vec2f(x1, z1), new Vec2f(x2, z2), width, this.levels));
        }

        for(Builder child : builder.children) {
            this.generateWetlands(child, random);
        }
    }

    public void addLake(Builder branch, Random random, GenWarp warp) {
        if (random.nextFloat() <= this.lake.chance) {
            float lakeSize = this.lake.sizeMin + random.nextFloat() * this.lake.sizeRange;
            float cx = branch.carver.river.x1;
            float cz = branch.carver.river.z1;
            if (this.lakeOverlapsOther(cx, cz, lakeSize, branch.lakes)) {
                return;
            }

            branch.lakes.add(new Lake(new Vec2f(cx, cz), lakeSize, 1.0F, this.lake));
        }
    }

    public boolean riverOverlaps(River river, Builder parent, List<Builder> rivers) {
        for(Builder other : rivers) {
            if (other.overlaps(river, parent, 250.0F)) {
                return true;
            }
        }

        return false;
    }

    public boolean lakeOverlaps(Vec2f lake, float size, List<RiverCarver> rivers) {
        for(RiverCarver other : rivers) {
            if (!other.main && other.river.overlaps(lake, size)) {
                return true;
            }
        }

        return false;
    }

    public boolean lakeOverlapsOther(float x, float z, float size, List<Lake> lakes) {
        float dist2 = size * size;

        for(Lake other : lakes) {
            if (other.overlaps(x, z, dist2)) {
                return true;
            }
        }

        return false;
    }

    public static RiverCarver create(float x1, float z1, float x2, float z2, RiverConfig config, Levels levels, Random random) {
        River river = new River(x1, z1, x2, z2);
        RiverWarp warp = RiverWarp.create(0.35F, random);
        float valleyWidth = 275.0F * River.MAIN_VALLEY.next(random);
        Settings settings = creatSettings(random);
        settings.connecting = false;
        settings.fadeIn = config.fade;
        settings.valleySize = valleyWidth;
        return new RiverCarver(river, warp, config, settings, levels);
    }

    public static RiverCarver createFork(float x1, float z1, float x2, float z2, float valleyWidth, RiverConfig config, Levels levels, Random random) {
        River river = new River(x1, z1, x2, z2);
        RiverWarp warp = RiverWarp.create(0.4F, random);
        Settings settings = creatSettings(random);
        settings.connecting = true;
        settings.fadeIn = config.fade;
        settings.valleySize = valleyWidth;
        return new RiverCarver(river, warp, config, settings, levels);
    }

    public static Settings creatSettings(Random random) {
        Settings settings = new Settings();
        settings.valleyCurve = RiverCarver.getValleyType(random);
        return settings;
    }
}

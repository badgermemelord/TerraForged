// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.continent.simple;

import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.continent.SimpleContinent;
import com.terraforged.engine.world.rivermap.gen.GenWarp;
import com.terraforged.engine.world.rivermap.river.*;
import com.terraforged.noise.util.NoiseUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleRiverGenerator extends BaseRiverGenerator<SimpleContinent>
{
    public SimpleRiverGenerator(final SimpleContinent continent, final GeneratorContext context) {
        super(continent, context);
    }
    
    @Override
    public List<Network.Builder> generateRoots(final int x, final int z, final Random random, final GenWarp warp) {
        final float start = random.nextFloat();
        final float spacing = 6.2831855f / this.count;
        final float spaceVar = spacing * 0.75f;
        final float spaceBias = -spaceVar / 2.0f;
        final List<Network.Builder> roots = new ArrayList<Network.Builder>(this.count);
        for (int i = 0; i < this.count; ++i) {
            final float variance = random.nextFloat() * spaceVar + spaceBias;
            final float angle = start + spacing * i + variance;
            final float dx = NoiseUtil.sin(angle);
            final float dz = NoiseUtil.cos(angle);
            final float startMod = 0.05f + random.nextFloat() * 0.45f;
            final float length = ((SimpleContinent)this.continent).getDistanceToOcean(x, z, dx, dz);
            final float startDist = Math.max(400.0f, startMod * length);
            final float x2 = x + dx * startDist;
            final float z2 = z + dz * startDist;
            final float x3 = x + dx * length;
            final float z3 = z + dz * length;
            final float valleyWidth = 275.0f * River.MAIN_VALLEY.next(random);
            final River river = new River((float)(int)x2, (float)(int)z2, (float)(int)x3, (float)(int)z3);
            final RiverCarver.Settings settings = BaseRiverGenerator.creatSettings(random);
            settings.fadeIn = this.main.fade;
            settings.valleySize = valleyWidth;
            final RiverWarp riverWarp = RiverWarp.create(0.1f, 0.85f, random);
            final RiverCarver carver = new RiverCarver(river, riverWarp, this.main, settings, this.levels);
            final Network.Builder branch = Network.builder(carver);
            roots.add(branch);
            this.addLake(branch, random, warp);
        }
        return roots;
    }
}

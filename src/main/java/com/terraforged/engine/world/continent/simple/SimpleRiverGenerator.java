//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.continent.simple;

import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.continent.SimpleContinent;
import com.terraforged.engine.world.rivermap.gen.GenWarp;
import com.terraforged.engine.world.rivermap.river.BaseRiverGenerator;
import com.terraforged.engine.world.rivermap.river.Network;
import com.terraforged.engine.world.rivermap.river.River;
import com.terraforged.engine.world.rivermap.river.RiverCarver;
import com.terraforged.engine.world.rivermap.river.RiverWarp;
import com.terraforged.engine.world.rivermap.river.Network.Builder;
import com.terraforged.engine.world.rivermap.river.RiverCarver.Settings;
import com.terraforged.noise.util.NoiseUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleRiverGenerator extends BaseRiverGenerator<SimpleContinent> {
    public SimpleRiverGenerator(SimpleContinent continent, GeneratorContext context) {
        super(continent, context);
    }

    public List<Builder> generateRoots(int x, int z, Random random, GenWarp warp) {
        float start = random.nextFloat();
        float spacing = (float) (Math.PI * 2) / (float)this.count;
        float spaceVar = spacing * 0.75F;
        float spaceBias = -spaceVar / 2.0F;
        List<Builder> roots = new ArrayList(this.count);

        for(int i = 0; i < this.count; ++i) {
            float variance = random.nextFloat() * spaceVar + spaceBias;
            float angle = start + spacing * (float)i + variance;
            float dx = NoiseUtil.sin(angle);
            float dz = NoiseUtil.cos(angle);
            float startMod = 0.05F + random.nextFloat() * 0.45F;
            float length = ((SimpleContinent)this.continent).getDistanceToOcean(x, z, dx, dz);
            float startDist = Math.max(400.0F, startMod * length);
            float x1 = (float)x + dx * startDist;
            float z1 = (float)z + dz * startDist;
            float x2 = (float)x + dx * length;
            float z2 = (float)z + dz * length;
            float valleyWidth = 275.0F * River.MAIN_VALLEY.next(random);
            River river = new River((float)((int)x1), (float)((int)z1), (float)((int)x2), (float)((int)z2));
            Settings settings = creatSettings(random);
            settings.fadeIn = this.main.fade;
            settings.valleySize = valleyWidth;
            RiverWarp riverWarp = RiverWarp.create(0.1F, 0.85F, random);
            RiverCarver carver = new RiverCarver(river, riverWarp, this.main, settings, this.levels);
            Builder branch = Network.builder(carver);
            roots.add(branch);
            this.addLake(branch, random, warp);
        }

        return roots;
    }
}

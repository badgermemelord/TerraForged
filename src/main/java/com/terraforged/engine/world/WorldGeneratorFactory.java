//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world;

import com.terraforged.engine.world.climate.Climate;
import com.terraforged.engine.world.heightmap.Heightmap;
import java.util.function.Supplier;

public class WorldGeneratorFactory implements Supplier<WorldGenerator> {
    private final Heightmap heightmap;
    private final WorldFilters filters;

    public WorldGeneratorFactory(GeneratorContext context) {
        this.heightmap = new Heightmap(context);
        this.filters = new WorldFilters(context);
    }

    public WorldGeneratorFactory(GeneratorContext context, Heightmap heightmap) {
        this.heightmap = heightmap;
        this.filters = new WorldFilters(context);
    }

    public Heightmap getHeightmap() {
        return this.heightmap;
    }

    public Climate getClimate() {
        return this.getHeightmap().getClimate();
    }

    public WorldFilters getFilters() {
        return this.filters;
    }

    public WorldGenerator get() {
        return new WorldGenerator(this.heightmap, this.filters);
    }
}

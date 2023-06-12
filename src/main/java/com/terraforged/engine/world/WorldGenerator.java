//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world;

import com.terraforged.engine.world.heightmap.Heightmap;

public class WorldGenerator {
    private final Heightmap heightmap;
    private final WorldFilters filters;

    public WorldGenerator(Heightmap heightmap, WorldFilters filters) {
        this.heightmap = heightmap;
        this.filters = filters;
    }

    public Heightmap getHeightmap() {
        return this.heightmap;
    }

    public WorldFilters getFilters() {
        return this.filters;
    }
}

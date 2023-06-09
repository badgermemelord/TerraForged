// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world;

import com.terraforged.engine.world.climate.Climate;
import com.terraforged.engine.world.heightmap.Heightmap;

import java.util.function.Supplier;

public class WorldGeneratorFactory implements Supplier<WorldGenerator>
{
    private final Heightmap heightmap;
    private final WorldFilters filters;
    
    public WorldGeneratorFactory(final GeneratorContext context) {
        this.heightmap = new Heightmap(context);
        this.filters = new WorldFilters(context);
    }
    
    public WorldGeneratorFactory(final GeneratorContext context, final Heightmap heightmap) {
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
    
    @Override
    public WorldGenerator get() {
        return new WorldGenerator(this.heightmap, this.filters);
    }
}

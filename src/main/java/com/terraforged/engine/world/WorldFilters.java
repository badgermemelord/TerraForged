// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world;

import com.terraforged.engine.filter.*;
import com.terraforged.engine.settings.FilterSettings;
import com.terraforged.engine.tile.Tile;

import java.util.function.IntFunction;

public class WorldFilters
{
    private final Smoothing smoothing;
    private final Steepness steepness;
    private final BeachDetect beach;
    private final FilterSettings settings;
    private final WorldErosion<Erosion> erosion;
    private final int erosionIterations;
    private final int smoothingIterations;
    
    public WorldFilters(final GeneratorContext context) {
        final IntFunction<Erosion> factory = Erosion.factory(context);
        this.settings = context.settings.filters;
        this.beach = new BeachDetect(context);
        this.smoothing = new Smoothing(context.settings, context.levels);
        this.steepness = new Steepness(1, 10.0f, context.levels);
        this.erosion = new WorldErosion<Erosion>(factory, (e, size) -> e.getSize() == size);
        this.erosionIterations = context.settings.filters.erosion.dropletsPerChunk;
        this.smoothingIterations = context.settings.filters.smoothing.iterations;
    }
    
    public FilterSettings getSettings() {
        return this.settings;
    }
    
    public void apply(final Tile tile, final boolean optionalFilters) {
        final Filterable map = tile.filterable();
        if (optionalFilters) {
            this.applyOptionalFilters(map, tile.getRegionX(), tile.getRegionZ());
        }
        this.applyRequiredFilters(map, tile.getRegionX(), tile.getRegionZ());
    }
    
    public void applyRequiredFilters(final Filterable map, final int seedX, final int seedZ) {
        this.steepness.apply(map, seedX, seedZ, 1);
        this.beach.apply(map, seedX, seedZ, 1);
    }
    
    public void applyOptionalFilters(final Filterable map, final int seedX, final int seedZ) {
        final Erosion erosion = this.erosion.get(map.getSize().total);
        erosion.apply(map, seedX, seedZ, this.erosionIterations);
        this.smoothing.apply(map, seedX, seedZ, this.smoothingIterations);
    }
}

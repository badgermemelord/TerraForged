// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain.populator;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.cell.Populator;
import com.terraforged.engine.settings.TerrainSettings;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.noise.Source;

public class TerrainPopulator implements Populator
{
    protected final float weight;
    protected final Terrain type;
    protected final Module base;
    protected final Module variance;
    
    public TerrainPopulator(final Terrain type, final Module base, final Module variance, final float weight) {
        this.type = type;
        this.base = base;
        this.weight = weight;
        this.variance = variance;
    }
    
    public float getWeight() {
        return this.weight;
    }
    
    public Module getVariance() {
        return this.variance;
    }
    
    public Terrain getType() {
        return this.type;
    }
    
    @Override
    public void apply(final Cell cell, final float x, final float z) {
        final float base = this.base.getValue(x, z);
        final float variance = this.variance.getValue(x, z);
        cell.value = base + variance;
        if (cell.value < 0.0f) {
            cell.value = 0.0f;
        }
        else if (cell.value > 1.0f) {
            cell.value = 1.0f;
        }
        cell.terrain = this.type;
    }
    
    public TerrainConfig asConfig() {
        return new TerrainConfig(this.type, this.variance, this.weight);
    }
    
    public static Module clamp(final Module module) {
        if (module.minValue() < 0.0f || module.maxValue() > 1.0f) {
            return module.clamp(0.0, 1.0);
        }
        return module;
    }
    
    public static TerrainPopulator of(final Terrain type, final Module variance) {
        return new TerrainPopulator(type, Source.ZERO, variance, 1.0f);
    }
    
    public static TerrainPopulator of(final Terrain type, final Module base, final Module variance, final TerrainSettings.Terrain settings) {
        if (settings.verticalScale == 1.0f && settings.baseScale == 1.0f) {
            return new TerrainPopulator(type, base, variance, settings.weight);
        }
        return new ScaledPopulator(type, base, variance, settings.baseScale, settings.verticalScale, settings.weight);
    }
}

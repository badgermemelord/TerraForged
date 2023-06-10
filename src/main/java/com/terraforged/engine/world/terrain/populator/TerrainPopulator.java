//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.engine.world.terrain.populator;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.cell.Populator;
import com.terraforged.engine.settings.TerrainSettings;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;

public class TerrainPopulator implements Populator {
    protected final float weight;
    protected final Terrain type;
    protected final Module base;
    protected final Module variance;

    public TerrainPopulator(Terrain type, Module base, Module variance, float weight) {
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

    public void apply(Cell cell, float x, float z) {
        float base = this.base.getValue(x, z);
        float variance = this.variance.getValue(x, z);
        cell.value = base + variance;
        if (cell.value < 0.0F) {
            cell.value = 0.0F;
        } else if (cell.value > 1.0F) {
            cell.value = 1.0F;
        }

        cell.terrain = this.type;
    }

    public TerrainConfig asConfig() {
        return new TerrainConfig(this.type, this.variance, this.weight);
    }

    public static Module clamp(Module module) {
        return !(module.minValue() < 0.0F) && !(module.maxValue() > 1.0F) ? module : module.clamp(0.0, 1.0);
    }

    public static TerrainPopulator of(Terrain type, Module variance) {
        return new TerrainPopulator(type, Source.ZERO, variance, 1.0F);
    }

    public static TerrainPopulator of(Terrain type, Module base, Module variance, TerrainSettings.Terrain settings) {
        return (TerrainPopulator)(settings.verticalScale == 1.0F && settings.baseScale == 1.0F ? new TerrainPopulator(type, base, variance, settings.weight) : new ScaledPopulator(type, base, variance, settings.baseScale, settings.verticalScale, settings.weight));
    }
}
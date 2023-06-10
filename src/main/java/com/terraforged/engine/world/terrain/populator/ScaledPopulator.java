//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.engine.world.terrain.populator;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.noise.Module;

public class ScaledPopulator extends TerrainPopulator {
    private final float baseScale;
    private final float varianceScale;

    public ScaledPopulator(Terrain type, Module base, Module variance, float baseScale, float varianceScale, float weight) {
        super(type, base, variance, weight);
        this.baseScale = baseScale;
        this.varianceScale = varianceScale;
    }

    public void apply(Cell cell, float x, float z) {
        float base = this.base.getValue(x, z) * this.baseScale;
        float variance = this.variance.getValue(x, z) * this.varianceScale;
        cell.value = base + variance;
        if (cell.value < 0.0F) {
            cell.value = 0.0F;
        } else if (cell.value > 1.0F) {
            cell.value = 1.0F;
        }

        cell.terrain = this.type;
    }
}

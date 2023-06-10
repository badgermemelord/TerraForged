//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.filter;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.filter.Filter.Visitor;
import com.terraforged.engine.world.heightmap.Levels;

public class Steepness implements Filter, Visitor {
    private final int radius;
    private final float scaler;
    private final float waterLevel;
    private final float maxBeachLevel;

    public Steepness(int radius, float scaler, Levels levels) {
        this.radius = radius;
        this.scaler = scaler;
        this.waterLevel = levels.water;
        this.maxBeachLevel = levels.water(6);
    }

    public void apply(Filterable cellMap, int seedX, int seedZ, int iterations) {
        this.iterate(cellMap, this);
    }

    public void visit(Filterable cellMap, Cell cell, int cx, int cz) {
        float totalHeightDif = 0.0F;

        for(int dz = -1; dz <= 2; ++dz) {
            for(int dx = -1; dx <= 2; ++dx) {
                if (dx != 0 || dz != 0) {
                    int x = cx + dx * this.radius;
                    int z = cz + dz * this.radius;
                    Cell neighbour = cellMap.getCellRaw(x, z);
                    if (!neighbour.isAbsent()) {
                        float height = Math.max(neighbour.value, this.waterLevel);
                        totalHeightDif += Math.abs(cell.value - height) / (float)this.radius;
                    }
                }
            }
        }

        cell.gradient = Math.min(1.0F, totalHeightDif * this.scaler);
    }
}
